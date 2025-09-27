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

package org.apache.fesod.excel.temp.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.temp.LockData;
import org.junit.jupiter.api.Test;

/**
 * 测试poi
 *
 *
 **/
public class RepeatTest {

    @Test
    public void xlsTest1() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xls")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r1 = FastExcel.readSheet(0).build();
            ReadSheet r2 = FastExcel.readSheet(2).build();
            reader.read(r1);
            reader.read(r2);
            reader.finish();
        }
    }

    @Test
    public void xlsTest2() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xls")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r2 = FastExcel.readSheet(1).build();
            reader.read(r2);
            reader.finish();
        }
    }

    @Test
    public void xlsTest3() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xls")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r2 = FastExcel.readSheet(0).build();
            reader.read(r2);
            reader.finish();
        }
    }

    @Test
    public void xlsxTest1() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xlsx")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r1 = FastExcel.readSheet(0).build();
            ReadSheet r2 = FastExcel.readSheet(2).build();
            reader.read(r1);
            reader.read(r2);
            reader.finish();
        }
    }

    @Test
    public void xlsxTest2() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xlsx")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r2 = FastExcel.readSheet(1).build();
            reader.read(r2);
            reader.finish();
        }
    }

    @Test
    public void xlsxTest3() throws IOException {
        try (ExcelReader reader = FastExcel.read(
                        Files.newInputStream(Paths.get("src/test/resources/repeat/repeat.xlsx")),
                        LockData.class,
                        new RepeatListener())
                .headRowNumber(0)
                .build()) {
            ReadSheet r2 = FastExcel.readSheet(0).build();
            reader.read(r2);
            reader.finish();
        }
    }
}
