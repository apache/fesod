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

package org.apache.fesod.excel.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;

public class TestFileUtil {

    public static InputStream getResourcesFileInputStream(String fileName) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader == null ? null : contextClassLoader.getResourceAsStream(fileName);
    }

    public static String getPath() {
        return resolveRootPath();
    }

    public static TestPathBuild pathBuild() {
        return new TestPathBuild();
    }

    public static File createNewFile(String pathName) {
        File file = pathFromRoot(pathName).toFile();
        if (file.exists()) {
            file.delete();
        } else {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        return file;
    }

    public static File readFile(String pathName) {
        return pathFromRoot(pathName).toFile();
    }

    public static File readUserHomeFile(String pathName) {
        return new File(System.getProperty("user.home") + File.separator + pathName);
    }

    /**
     * build to test file path
     **/
    public static class TestPathBuild {
        private TestPathBuild() {
            subPath = new ArrayList<>();
        }

        private final List<String> subPath;

        public TestPathBuild sub(String dirOrFile) {
            subPath.add(dirOrFile);
            return this;
        }

        public String getPath() {
            String rootPath = resolveRootPath();
            if (CollectionUtils.isEmpty(subPath)) {
                return rootPath;
            }
            if (subPath.size() == 1) {
                return rootPath + subPath.get(0);
            }
            StringBuilder path = new StringBuilder(rootPath);
            path.append(subPath.get(0));
            for (int i = 1; i < subPath.size(); i++) {
                path.append(File.separator).append(subPath.get(i));
            }
            return path.toString();
        }
    }

    private static String resolveRootPath() {
        Path codeSourceLocation = resolveFromCodeSource();
        if (codeSourceLocation != null) {
            return appendSeparator(codeSourceLocation.toString());
        }

        URL resource = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            resource = contextClassLoader.getResource("");
        }
        if (resource == null) {
            resource = TestFileUtil.class.getResource("/");
        }
        URL finalResource = Objects.requireNonNull(resource, "Unable to locate test resources root");
        String path = finalResource.getPath();
        if ("file".equals(finalResource.getProtocol())) {
            if (path.startsWith("file:")) {
                path = path.substring("file:".length());
            }
        }
        return appendSeparator(path);
    }

    private static Path pathFromRoot(String relativePath) {
        return Paths.get(resolveRootPath(), relativePath);
    }

    private static Path resolveFromCodeSource() {
        try {
            if (TestFileUtil.class.getProtectionDomain() == null
                    || TestFileUtil.class.getProtectionDomain().getCodeSource() == null) {
                return null;
            }
            Path location = Paths.get(TestFileUtil.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
            if (!location.toFile().isDirectory()) {
                return null;
            }
            return location;
        } catch (Exception ex) {
            return null;
        }
    }

    private static String appendSeparator(String path) {
        return path.endsWith(File.separator) ? path : path + File.separator;
    }
}
