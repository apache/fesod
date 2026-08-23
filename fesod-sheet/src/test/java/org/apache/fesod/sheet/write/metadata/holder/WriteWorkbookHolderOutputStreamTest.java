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

package org.apache.fesod.sheet.write.metadata.holder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the output stream lifecycle of {@link WriteWorkbookHolder} when workbook
 * initialization fails.
 */
public class WriteWorkbookHolderOutputStreamTest {

    @TempDir
    Path tempDir;

    @Test
    void constructorClosesOutputFileStreamWhenTemplateCopyFails() throws IOException {
        File outputFile = tempDir.resolve("output.xlsx").toFile();
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setFile(outputFile);
        writeWorkbook.setTemplateFile(tempDir.resolve("missing-template.xlsx").toFile());

        Assertions.assertThrows(ExcelGenerateException.class, () -> new WriteWorkbookHolder(writeWorkbook));
        // On Windows an open FileOutputStream locks the file, so deletion fails if the holder
        // leaked the stream it opened. On Unix the deletion succeeds either way, keeping the
        // assertion portable.
        Assertions.assertTrue(outputFile.delete(), "output stream should be closed after initialization failure");
    }

    @Test
    void constructorDoesNotCloseCallerProvidedOutputStreamOnFailure() throws IOException {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setOutputStream(new ByteArrayOutputStream());
        writeWorkbook.setTemplateFile(tempDir.resolve("missing-template.xlsx").toFile());

        Assertions.assertThrows(ExcelGenerateException.class, () -> new WriteWorkbookHolder(writeWorkbook));
        // Streams passed in by the caller remain the caller's responsibility, so the holder must
        // not close them on failure.
        Assertions.assertDoesNotThrow(() -> writeWorkbook.getOutputStream().write(1));
    }
}
