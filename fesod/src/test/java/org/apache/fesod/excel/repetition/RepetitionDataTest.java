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

package org.apache.fesod.excel.repetition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class RepetitionDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File fileTable07;
    private static File fileTable03;
    private static File fileTableCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("repetition07.xlsx");
        file03 = TestFileUtil.createNewFile("repetition03.xls");
        fileCsv = TestFileUtil.createNewFile("repetitionCsv.csv");
        fileTable07 = TestFileUtil.createNewFile("repetitionTable07.xlsx");
        fileTable03 = TestFileUtil.createNewFile("repetitionTable03.xls");
        fileTableCsv = TestFileUtil.createNewFile("repetitionTableCsv.csv");
    }

    @Test
    public void t01ReadAndWrite07() {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() {
        readAndWrite(fileCsv);
    }

    private void readAndWrite(File file) {
        try (ExcelWriter excelWriter =
                FastExcel.write(file, RepetitionData.class).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet(0).build();
            excelWriter.write(data(), writeSheet).write(data(), writeSheet);
        }
        try (ExcelReader excelReader = FastExcel.read(file, RepetitionData.class, new RepetitionDataListener())
                .build()) {
            ReadSheet readSheet = FastExcel.readSheet(0).build();
            excelReader.read(readSheet);
        }
    }

    @Test
    public void t11ReadAndWriteTable07() {
        readAndWriteTable(fileTable07);
    }

    @Test
    public void t12ReadAndWriteTable03() {
        readAndWriteTable(fileTable03);
    }

    @Test
    public void t13ReadAndWriteTableCsv() {
        readAndWriteTable(fileTableCsv);
    }

    private void readAndWriteTable(File file) {
        try (ExcelWriter excelWriter =
                FastExcel.write(file, RepetitionData.class).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet(0).build();
            WriteTable writeTable =
                    FastExcel.writerTable(0).relativeHeadRowIndex(0).build();
            excelWriter.write(data(), writeSheet, writeTable).write(data(), writeSheet, writeTable);
        }
        try (ExcelReader excelReader = FastExcel.read(file, RepetitionData.class, new RepetitionDataListener())
                .build()) {
            ReadSheet readSheet = FastExcel.readSheet(0).headRowNumber(2).build();
            excelReader.read(readSheet);
        }
    }

    private List<RepetitionData> data() {
        List<RepetitionData> list = new ArrayList<RepetitionData>();
        RepetitionData data = new RepetitionData();
        data.setString("字符串0");
        list.add(data);
        return list;
    }
}
