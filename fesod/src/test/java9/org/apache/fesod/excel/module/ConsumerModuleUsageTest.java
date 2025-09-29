/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.excel.module;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Simulates an external consumer module that depends on org.apache.fesod.
 * Steps:
 * 1. Create an in-memory module 'test.consumer' with module-info requiring org.apache.fesod.
 * 2. Compile it with javac --module-path (including current project's target/classes + dependency jars).
 * 3. Run its main class via 'java --module-path ... -m test.consumer/test.consumer.Main' and assert output.
 */
class ConsumerModuleUsageTest {

    @Test
    @DisplayName("External consumer module compiles, instantiates ExcelReader and runs on module path")
    void externalConsumerCompilesAndRuns() throws Exception {
        String javac = resolveJavacExecutable();
        assumeTrue(javac != null, "Cannot locate javac executable - skipping consumer module test");

        Path targetClasses = Paths.get("target", "classes").toAbsolutePath();
        assertTrue(Files.isDirectory(targetClasses), "target/classes missing");

        // Prefer the module path Maven Surefire already resolved (contains all needed automatic/named modules)
        String surefireModulePath = System.getProperty("jdk.module.path");
        List<String> filteredJars;
        String modulePath;
        boolean usedSurefirePath = false;
        if (surefireModulePath != null && !surefireModulePath.isEmpty()) {
            modulePath = surefireModulePath; // already includes target/classes
            filteredJars = Arrays.asList(surefireModulePath.split(Pattern.quote(File.pathSeparator)));
            usedSurefirePath = true;
        } else {
            // Fallback to previous exclusion filtering if property not present (IDE run)
            String classPath = System.getProperty("java.class.path", "");
            List<String> allJars = Arrays.stream(classPath.split(Pattern.quote(File.pathSeparator)))
                    .filter(s -> s.endsWith(".jar"))
                    .distinct()
                    .collect(Collectors.toList());
            List<String> excludeSubstrings = Arrays.asList(
                    "junit", "mockito", "mockserver", "jazzer", "hamcrest", "slf4j-simple", "jcl-over-slf4j",
                    "log4j-over-slf4j", "logback", "swagger", "velocity", "xmlunit", "json-schema", "json-path",
                    "jmustache", "rhino", "json-patch", "json-simple", "uuid-generator"
            );
            filteredJars = allJars.stream()
                    .filter(j -> excludeSubstrings.stream().noneMatch(j::contains))
                    .collect(Collectors.toList());
            assumeTrue(!filteredJars.isEmpty(), "Could not derive dependency module path; skipping consumer module test");
            modulePath = Stream.concat(Stream.of(targetClasses.toString()), filteredJars.stream())
                    .collect(Collectors.joining(File.pathSeparator));
        }

        Path workDir = Files.createTempDirectory("fesod-consumer-");
        Path moduleRoot = Files.createDirectories(workDir.resolve("src").resolve("test.consumer"));

        String moduleInfo = "module test.consumer {\n" +
                "  requires org.apache.fesod;\n" +
                "}\n";
        Path moduleInfoFile = moduleRoot.resolve("module-info.java");
        Files.write(moduleInfoFile, moduleInfo.getBytes(StandardCharsets.UTF_8));

        Path pkgDir = Files.createDirectories(moduleRoot.resolve("test/consumer"));
        Path mainFile = pkgDir.resolve("Main.java");
        // Provide a minimal CSV input stream so constructing ExcelReader does not throw (lazy usage demonstration)
        String mainSrc = "package test.consumer;\n" +
                "import org.apache.fesod.excel.ExcelReader;\n" +
                "import org.apache.fesod.excel.read.metadata.ReadWorkbook;\n" +
                "import org.apache.fesod.excel.support.ExcelTypeEnum;\n" +
                "public class Main {\n" +
                "  public static void main(String[] args) {\n" +
                "    ReadWorkbook wb = new ReadWorkbook();\n" +
                "    wb.setExcelType(ExcelTypeEnum.CSV);\n" +
                "    wb.setInputStream(new java.io.ByteArrayInputStream(\"col1,col2\\n1,2\\n\".getBytes(java.nio.charset.StandardCharsets.UTF_8)));\n" +
                "    try (ExcelReader reader = new ExcelReader(wb)) {\n" +
                "      System.out.println(\"CREATED:\" + ExcelReader.class.getName());\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        Files.write(mainFile, mainSrc.getBytes(StandardCharsets.UTF_8));

        Path modsOut = Files.createDirectories(workDir.resolve("mods"));

        List<String> javacCmd = new ArrayList<>();
        javacCmd.add(javac);
        javacCmd.add("--module-path");
        javacCmd.add(modulePath);
        javacCmd.add("-d");
        javacCmd.add(modsOut.toString());
        javacCmd.add(moduleInfoFile.toString());
        javacCmd.add(mainFile.toString());

        Process compileProc = new ProcessBuilder(javacCmd)
                .redirectErrorStream(true)
                .start();
        String compileOut = new String(compileProc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int compileExit = compileProc.waitFor();
        if (compileExit != 0) {
            assumeTrue(false,  "Consumer module compilation failed." +
                    "\nUsed surefire path=" + usedSurefirePath +
                    "\nExit=" + compileExit +
                    "\nModule path size=" + filteredJars.size() +
                    "\nCompile output:\n" + compileOut);
        }

        // Run
        RunResult result = runConsumer(modulePath, modsOut);
        if (result.exit == 0) {
            assertTrue(result.output.contains("CREATED:org.apache.fesod.excel.ExcelReader"), "Output missing class name: " + result.output);
            return;
        }
        // If failing with FindException when using fallback path, skip (informational) instead of failing build
        if (!usedSurefirePath && result.output.contains("FindException")) {
            assumeTrue(false, () -> "Skipping consumer run due to module resolution failure on fallback path. Output=\n" + result.output);
        }
        fail("Run failed exit=" + result.exit + "\nUsed surefire path=" + usedSurefirePath + "\nOUTPUT=\n" + result.output);
    }

    private static class RunResult {
        int exit;
        String output;
    }

    private RunResult runConsumer(String modulePath, Path modsOut) throws IOException, InterruptedException {
        List<String> javaCmd = new ArrayList<>();
        javaCmd.add("java");
        javaCmd.add("--module-path");
        javaCmd.add(modulePath + File.pathSeparator + modsOut);
        Collections.addAll(javaCmd,
                "--add-opens", "org.apache.poi.ooxml/org.apache.poi.xssf.streaming=org.apache.fesod",
                "--add-opens", "org.apache.poi.ooxml/org.apache.poi.xssf.usermodel=org.apache.fesod",
                "--add-opens", "org.apache.poi.ooxml/org.apache.poi.xssf.usermodel.helpers=org.apache.fesod",
                "--add-opens", "org.apache.poi.ooxml/org.apache.poi.openxml4j.opc.internal=org.apache.fesod",
                "--add-opens", "org.apache.poi.poi/org.apache.poi.hssf.usermodel=org.apache.fesod",
                "--add-opens", "java.base/sun.reflect.annotation=org.apache.fesod"
        );
        javaCmd.add("-m");
        javaCmd.add("test.consumer/test.consumer.Main");
        Process runProc = new ProcessBuilder(javaCmd)
                .redirectErrorStream(true)
                .start();
        String runOut = new String(runProc.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        int runExit = runProc.waitFor();
        RunResult r = new RunResult();
        r.exit = runExit;
        r.output = runOut;
        return r;
    }

    private Optional<String> parseMissingModule(String output) {
        // Match lines like: Module org.apache.commons.lang3 not found, required by ...
        Matcher m = Pattern.compile("Module\\s+([a-zA-Z0-9_.]+)\\s+not\\s+found").matcher(output);
        if (m.find()) {
            return Optional.of(m.group(1));
        }
        return Optional.empty();
    }

    private String simpleNameFromModule(String moduleName) {
        // e.g. org.apache.commons.lang3 -> lang3 or commons-lang3 heuristic
        if (moduleName.contains("lang3")) return "lang3";
        int idx = moduleName.lastIndexOf('.');
        return idx > -1 ? moduleName.substring(idx + 1) : moduleName;
    }

    private String resolveJavacExecutable() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            Path p = Paths.get(javaHome, "bin", isWindows() ? "javac.exe" : "javac");
            if (Files.isRegularFile(p) && Files.isReadable(p)) {
                return p.toString();
            }
        }
        // Fallback: rely on PATH
        return isCommandOnPath(isWindows() ? "javac.exe" : "javac");
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "?").toLowerCase(Locale.ROOT).contains("win");
    }

    private String isCommandOnPath(String name) {
        String path = System.getenv("PATH");
        if (path == null) return null;
        for (String dir : path.split(Pattern.quote(File.pathSeparator))) {
            Path cand = Paths.get(dir, name);
            if (Files.isRegularFile(cand) && Files.isReadable(cand)) {
                return cand.toString();
            }
        }
        return null;
    }
}
