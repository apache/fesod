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

package org.apache.fesod.sheet.context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * A password-protected xlsx written to a stream is staged in a temp file. If workbook.write fails
 * after that file is created, the temp file must still be removed.
 */
public class WriteContextImplEncryptTempFileTest {

    @TempDir
    Path tempDir;

    @Test
    void deletesTempFileWhenEncryptedStreamWriteFails() throws Exception {
        String originalPrefix = FileUtils.getTempFilePrefix();
        File isolated = tempDir.resolve("encrypt-tmp").toFile();
        Assertions.assertTrue(isolated.mkdirs());
        FileUtils.setTempFilePrefix(isolated.getAbsolutePath());
        Workbook original = null;
        try {
            ByteArrayOutputStream callerStream = new ByteArrayOutputStream();
            WriteWorkbook writeWorkbook = new WriteWorkbook();
            writeWorkbook.setPassword("secret");
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
            writeWorkbook.setOutputStream(callerStream);
            writeWorkbook.setAutoCloseStream(Boolean.FALSE);

            WriteContextImpl context = new WriteContextImpl(writeWorkbook);
            original = context.writeWorkbookHolder().getWorkbook();
            Workbook failing = mock(Workbook.class);
            AtomicBoolean firstWriteWasTempFile = new AtomicBoolean(false);
            doAnswer(invocation -> {
                        OutputStream out = invocation.getArgument(0);
                        if (!firstWriteWasTempFile.get() && out != callerStream) {
                            firstWriteWasTempFile.set(true);
                        }
                        if (!firstWriteWasTempFile.get()) {
                            throw new AssertionError("encrypt path was skipped; write went to the caller stream");
                        }
                        throw new IOException("disk full");
                    })
                    .when(failing)
                    .write(any(OutputStream.class));
            context.writeWorkbookHolder().setWorkbook(failing);

            ExcelGenerateException thrown =
                    Assertions.assertThrows(ExcelGenerateException.class, () -> context.finish(false));
            Assertions.assertTrue(firstWriteWasTempFile.get(), "workbook.write should stage a temp xlsx");
            Assertions.assertEquals("disk full", thrown.getCause().getMessage());

            File[] leftover = isolated.listFiles();
            Assertions.assertTrue(
                    leftover == null || leftover.length == 0, "encrypted stream write should not leave a temp xlsx");
        } finally {
            FileUtils.setTempFilePrefix(originalPrefix);
            if (original != null) {
                original.close();
            }
        }
    }
}
