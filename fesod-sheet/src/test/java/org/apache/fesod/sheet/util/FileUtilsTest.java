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

package org.apache.fesod.sheet.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

/**
 * Tests {@link FileUtils}
 */
class FileUtilsTest {

    @TempDir
    Path tempDir;

    private String originalTempPrefix;
    private String originalPoiFilesPath;
    private String originalCachePath;

    @BeforeEach
    void setUp() {
        originalTempPrefix = FileUtils.getTempFilePrefix();
        originalPoiFilesPath = FileUtils.getPoiFilesPath();
        originalCachePath = FileUtils.getCachePath();
    }

    @AfterEach
    void tearDown() {
        FileUtils.setTempFilePrefix(originalTempPrefix);
        FileUtils.setPoiFilesPath(originalPoiFilesPath);
        FileUtils.setCachePath(originalCachePath);
    }

    @Test
    void test_ReadWrite_loop() throws IOException {
        String content = "Hello, this is a test string.";
        File targetFile = tempDir.resolve("test_rw.txt").toFile();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));

        FileUtils.writeToFile(targetFile, inputStream);

        Assertions.assertTrue(targetFile.exists());
        Assertions.assertTrue(targetFile.length() > 0);

        byte[] readBytes = FileUtils.readFileToByteArray(targetFile);
        String readContent = new String(readBytes, StandardCharsets.UTF_8);

        Assertions.assertEquals(content, readContent);
    }

    @Test
    void test_writeToFile_exception() {
        InputStream badStream = new ByteArrayInputStream("test".getBytes());
        try {
            badStream.close();
        } catch (IOException ignored) {
        }

        File directoryAsFile = tempDir.resolve("subdir").toFile();
        directoryAsFile.mkdir();

        InputStream validStream = new ByteArrayInputStream("test".getBytes());

        Assertions.assertThrows(ExcelAnalysisException.class, () -> {
            FileUtils.writeToFile(directoryAsFile, validStream);
        });
    }

    @Test
    void test_finally_inputStream_shouldClose() throws IOException {
        File file = tempDir.resolve("close_test.txt").toFile();
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockInputStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(-1);

        FileUtils.writeToFile(file, mockInputStream, true);

        Mockito.verify(mockInputStream).close();
    }

    @Test
    void test_finally_inputStream_shouldNotClose() throws IOException {
        File file = tempDir.resolve("noclose_test.txt").toFile();
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockInputStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(-1);

        FileUtils.writeToFile(file, mockInputStream, false);

        Mockito.verify(mockInputStream, Mockito.never()).close();
    }

    @Test
    void test_finally_inputStream_closeException() throws IOException {
        File file = tempDir.resolve("ex_test.txt").toFile();
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockInputStream.read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(-1);

        Mockito.doThrow(new IOException("Close failed")).when(mockInputStream).close();

        Assertions.assertThrows(ExcelAnalysisException.class, () -> {
            FileUtils.writeToFile(file, mockInputStream, true);
        });
    }

    @Test
    void test_openInputStream_notFound() {
        File nonExistentFile = tempDir.resolve(UUID.randomUUID().toString()).toFile();

        Assertions.assertThrows(FileNotFoundException.class, () -> {
            FileUtils.openInputStream(nonExistentFile);
        });
    }

    @Test
    void test_openInputStream_isDirectory() {
        File dir = tempDir.resolve("test_dir").toFile();
        dir.mkdirs();

        IOException ex = Assertions.assertThrows(IOException.class, () -> {
            FileUtils.openInputStream(dir);
        });
        Assertions.assertTrue(ex.getMessage().contains("exists but is a directory"));
    }

    @Test
    void test_openInputStream_noRead() {
        File fileMocked = Mockito.mock(File.class);

        Mockito.when(fileMocked.exists()).thenReturn(true);
        Mockito.when(fileMocked.isDirectory()).thenReturn(false);
        Mockito.when(fileMocked.canRead()).thenReturn(false);
        Mockito.when(fileMocked.toString()).thenReturn("/path/to/locked_file");

        Assertions.assertThrows(IOException.class, () -> {
            FileUtils.openInputStream(fileMocked);
        });
    }

    @Test
    void test_createDirectory() {
        File nestedDir = tempDir.resolve("a/b/c").toFile();

        File result = FileUtils.createDirectory(nestedDir);

        Assertions.assertTrue(result.exists());
        Assertions.assertTrue(result.isDirectory());
    }

    @Test
    void test_createDirectory_exception() {
        File dirMocked = Mockito.mock(File.class);

        Mockito.when(dirMocked.exists()).thenReturn(false);
        Mockito.when(dirMocked.mkdirs()).thenReturn(false);
        Mockito.when(dirMocked.toString()).thenReturn("/path/to/locked_file");

        Assertions.assertThrows(ExcelCommonException.class, () -> {
            FileUtils.createDirectory(dirMocked);
        });
    }

    @Test
    void test_createTmpFile() {
        String fileName = "my_temp.xlsx";
        File tmpFile = FileUtils.createTmpFile(fileName);

        Assertions.assertNotNull(tmpFile);
        Assertions.assertFalse(tmpFile.exists());
        Assertions.assertEquals(fileName, tmpFile.getName());

        Assertions.assertTrue(tmpFile.getParentFile().exists());
    }

    @Test
    void test_createCacheTmpFile() {
        File cacheDir = FileUtils.createCacheTmpFile();

        Assertions.assertNotNull(cacheDir);
        Assertions.assertTrue(cacheDir.exists());
        Assertions.assertTrue(cacheDir.isDirectory());
        Assertions.assertTrue(cacheDir.getAbsolutePath().contains(FileUtils.EX_CACHE));
    }

    @Test
    void test_delete_recursive() throws IOException {
        // root/
        //   - file1.txt
        //   - sub/
        //     - file2.txt
        File root = tempDir.resolve("root").toFile();
        File sub = new File(root, "sub");
        FileUtils.createDirectory(sub);

        File file1 = new File(root, "file1.txt");
        File file2 = new File(sub, "file2.txt");

        Files.write(file1.toPath(), "content".getBytes());
        Files.write(file2.toPath(), "content".getBytes());

        Assertions.assertTrue(file1.exists());
        Assertions.assertTrue(file2.exists());

        FileUtils.delete(root);

        Assertions.assertFalse(root.exists());
        Assertions.assertFalse(file1.exists());
        Assertions.assertFalse(sub.exists());
        Assertions.assertFalse(file2.exists());
    }

    @Test
    void test_delete_singleFile() throws IOException {
        File file = tempDir.resolve("single.txt").toFile();
        file.createNewFile();

        FileUtils.delete(file);

        Assertions.assertFalse(file.exists());
    }

    @Test
    void test_delete_directory() {
        File root = tempDir.resolve("root").toFile();
        FileUtils.createDirectory(root);

        Assertions.assertTrue(root.exists());

        FileUtils.delete(root);

        Assertions.assertFalse(root.exists());
    }

    @Test
    void test_GetterSetter() {
        String originalPrefix = FileUtils.getTempFilePrefix();
        String newPrefix = tempDir.toString() + File.separator;

        try {
            FileUtils.setTempFilePrefix(newPrefix);
            Assertions.assertEquals(newPrefix, FileUtils.getTempFilePrefix());

            FileUtils.setCachePath(newPrefix + "cache");
            Assertions.assertEquals(newPrefix + "cache", FileUtils.getCachePath());

            FileUtils.setPoiFilesPath(newPrefix + "poi");
            Assertions.assertEquals(newPrefix + "poi", FileUtils.getPoiFilesPath());
        } finally {
            FileUtils.setTempFilePrefix(originalPrefix);
        }
    }

    @Test
    void test_createPoiFilesDirectory() throws NoSuchFieldException, IllegalAccessException {
        Field strategyField = TempFile.class.getDeclaredField("strategy");
        strategyField.setAccessible(true);

        TempFileCreationStrategy originalStrategy = (TempFileCreationStrategy) strategyField.get(null);

        try {
            FileUtils.createPoiFilesDirectory();

            TempFileCreationStrategy newStrategy = (TempFileCreationStrategy) strategyField.get(null);

            Assertions.assertNotNull(newStrategy);
            Assertions.assertEquals(FesodTempFileCreationStrategy.class, newStrategy.getClass());
            Assertions.assertNotSame(originalStrategy, newStrategy);
        } finally {
            TempFile.setTempFileCreationStrategy(originalStrategy);
        }
    }
}
