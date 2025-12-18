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

package org.apache.fesod.sheet.examples.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Utility for example files.
 */
public class ExampleFileUtil {

    public static InputStream getResourcesFileInputStream(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("" + fileName);
    }

    public static String getPath() {
        return ExampleFileUtil.class.getResource("/").getPath();
    }

    public static ExamplePathBuild pathBuild() {
        return new ExamplePathBuild();
    }

    public static File createNewFile(String pathName) {
        File file = new File(getPath() + pathName);
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
        return new File(getPath() + pathName);
    }

    public static File readUserHomeFile(String pathName) {
        return new File(System.getProperty("user.home") + File.separator + pathName);
    }

    /**
     * Get the path to a file in the example resource directory.
     *
     * @param fileName the file name relative to the example directory (e.g., "demo.xlsx" or "templates/simple.xlsx")
     * @return the full path to the file
     */
    public static String getExamplePath(String fileName) {
        return getPath() + "example" + File.separator + fileName;
    }

    /**
     * Check if a file exists in the example resource directory.
     *
     * @param fileName the file name relative to the example directory
     * @return true if the file exists, false otherwise
     */
    public static boolean exampleFileExists(String fileName) {
        File file = new File(getExamplePath(fileName));
        return file.exists();
    }

    /**
     * Get a File object for a file in the example resource directory.
     *
     * @param fileName the file name relative to the example directory
     * @return the File object
     */
    public static File getExampleFile(String fileName) {
        return new File(getExamplePath(fileName));
    }

    /**
     * Get the path to write output files in the system temp directory.
     *
     * @param fileName the output file name
     * @return the full path to the output file in temp directory
     */
    public static String getTempPath(String fileName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return tmpDir + File.separator + fileName;
    }

    /**
     * build to example file path
     **/
    public static class ExamplePathBuild {
        private ExamplePathBuild() {
            subPath = new ArrayList<>();
        }

        private final List<String> subPath;

        public ExamplePathBuild sub(String dirOrFile) {
            subPath.add(dirOrFile);
            return this;
        }

        public String getPath() {
            if (CollectionUtils.isEmpty(subPath)) {
                return ExampleFileUtil.class.getResource("/").getPath();
            }
            if (subPath.size() == 1) {
                return ExampleFileUtil.class.getResource("/").getPath() + subPath.get(0);
            }
            StringBuilder path =
                    new StringBuilder(ExampleFileUtil.class.getResource("/").getPath());
            path.append(subPath.get(0));
            for (int i = 1; i < subPath.size(); i++) {
                path.append(File.separator).append(subPath.get(i));
            }
            return path.toString();
        }
    }
}
