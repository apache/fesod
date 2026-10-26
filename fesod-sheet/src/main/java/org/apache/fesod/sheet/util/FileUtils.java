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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.IoUtils;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.poi.util.TempFile;

@Slf4j
public class FileUtils {
    public static final String POI_FILES = "poifiles";
    public static final String EX_CACHE = "excache";
    /**
     * If a server has multiple projects in use at the same time, a directory with the same name will be created under
     * the temporary directory, but each project is run by a different user, so there is a permission problem, so each
     * project creates a unique UUID as a separate Temporary Files.
     */
    @Getter
    @Setter
    private static String tempFilePrefix =
            System.getProperty(TempFile.JAVA_IO_TMPDIR) + File.separator + UUID.randomUUID() + File.separator;
    /**
     * Used to store poi temporary files.
     */
    @Getter
    @Setter
    private static String poiFilesPath = tempFilePrefix + POI_FILES + File.separator;
    /**
     * Used to store easy excel temporary files.
     */
    @Getter
    @Setter
    private static String cachePath = tempFilePrefix + EX_CACHE + File.separator;

    private static final int WRITE_BUFF_SIZE = 8192;

    private FileUtils() {}

    static {
        // Create a temporary directory in advance
        File tempFile = new File(tempFilePrefix);
        createDirectory(tempFile);
        tempFile.deleteOnExit();
        // Initialize the cache directory
        File cacheFile = new File(cachePath);
        createDirectory(cacheFile);
    }

    /**
     * Reads the contents of a file into a byte array. * The file is always closed.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFileToByteArray(final File file) throws IOException {
        try (InputStream in = openInputStream(file)) {
            final long fileLength = file.length();
            return fileLength > 0 ? IoUtils.toByteArray(in, (int) fileLength) : IoUtils.toByteArray(in);
        }
    }

    /**
     * Opens a {@link FileInputStream} for the specified file, providing better error messages than simply calling
     * <code>new FileInputStream(file)</code>.
     * <p>
     * At the end of the method either the stream will be successfully opened, or an exception will have been thrown.
     * <p>
     * An exception is thrown if the file does not exist. An exception is thrown if the file object exists but is a
     * directory. An exception is thrown if the file exists but cannot be read.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     * Write inputStream to file
     *
     * @param file        file
     * @param inputStream inputStream
     */
    public static void writeToFile(File file, InputStream inputStream) {
        writeToFile(file, inputStream, true);
    }

    /**
     * Write inputStream to file
     *
     * @param file             file
     * @param inputStream      inputStream
     * @param closeInputStream closeInputStream
     */
    public static void writeToFile(File file, InputStream inputStream, boolean closeInputStream) {
        if (!closeInputStream) {
            copyToFile(file, inputStream);
            return;
        }
        try (InputStream in = inputStream) {
            copyToFile(file, in);
        } catch (IOException e) {
            throw new ExcelAnalysisException("Can not close 'inputStream'", e);
        }
    }

    private static void copyToFile(File file, InputStream inputStream) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            int bytesRead;
            byte[] buffer = new byte[WRITE_BUFF_SIZE];
            while ((bytesRead = inputStream.read(buffer, 0, WRITE_BUFF_SIZE)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new ExcelAnalysisException("Can not create temporary file!", e);
        }
    }

    public static void createPoiFilesDirectory() {
        TempFile.setTempFileCreationStrategy(new FesodTempFileCreationStrategy());
    }

    public static File createCacheTmpFile() {
        return createDirectory(new File(cachePath + UUID.randomUUID().toString()));
    }

    public static File createTmpFile(String fileName) {
        File directory = createDirectory(new File(tempFilePrefix));
        return new File(directory, fileName);
    }

    /**
     * @param directory
     */
    public static File createDirectory(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new ExcelCommonException("Cannot create directory:" + directory.getAbsolutePath());
        }
        return directory;
    }

    /**
     * delete file
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            deleteOrWarn(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteOrWarn(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            deleteOrWarn(file);
        }
    }

    private static void deleteOrWarn(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.warn("Can not delete file: {}", file.getAbsolutePath(), e);
        }
    }
}
