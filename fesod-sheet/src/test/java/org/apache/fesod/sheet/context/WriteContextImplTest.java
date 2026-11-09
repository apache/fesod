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

import java.io.IOException;
import java.io.OutputStream;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.WRITE)
class WriteContextImplTest {

    /**
     * When writing the workbook fails and closing the stream then fails too, the write failure is the cause.
     */
    @Test
    void finish_writeAndCloseFail_reportsWriteFailure() {
        OutputStream brokenStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("write failed");
            }

            @Override
            public void close() throws IOException {
                throw new IOException("close failed");
            }
        };

        ExcelGenerateException e = Assertions.assertThrows(
                ExcelGenerateException.class, () -> FesodSheet.write(brokenStream, SimpleData.class)
                        .excelType(ExcelTypeEnum.XLSX)
                        .sheet()
                        .doWrite(TestDataBuilder.simpleData(10)));
        Assertions.assertEquals("write failed", e.getCause().getMessage());
        Assertions.assertEquals(1, e.getCause().getSuppressed().length);
        Assertions.assertEquals("close failed", e.getCause().getSuppressed()[0].getMessage());
    }
}
