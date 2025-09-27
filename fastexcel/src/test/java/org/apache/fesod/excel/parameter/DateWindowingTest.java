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

package org.apache.fesod.excel.parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DateWindowingTest {

    private static File file03DateWindowing1900;
    private static File file03DateWindowing1900Exists;
    private static File file03DateWindowing1904;
    private static File file03DateWindowing1904Exists;
    private static File file07DateWindowing1900;
    private static File file07DateWindowing1904;
    private static File fileCsvDateWindowing1900;
    private static File fileCsvDateWindowing1904;

    @BeforeAll
    public static void init() {
        file03DateWindowing1900 = TestFileUtil.createNewFile("datewindowing1900.xls");
        file03DateWindowing1900Exists = TestFileUtil.readFile("datewindowing/1900DateWindowing.xls");
        file03DateWindowing1904 = TestFileUtil.createNewFile("datewindowing1904.xls");
        file03DateWindowing1904Exists = TestFileUtil.readFile("datewindowing/1904DateWindowing.xls");
        file07DateWindowing1900 = TestFileUtil.createNewFile("datewindowing1900.xlsx");
        file07DateWindowing1904 = TestFileUtil.createNewFile("datewindowing1904.xlsx");
        fileCsvDateWindowing1900 = TestFileUtil.createNewFile("datewindowing1900.csv");
        fileCsvDateWindowing1904 = TestFileUtil.createNewFile("datewindowing1904.csv");
    }

    @Test
    void test03WriteAndRead() {
        // writing a file
        FastExcel.write(file03DateWindowing1900, ParameterData.class)
                .excelType(ExcelTypeEnum.XLS)
                .use1904windowing(Boolean.FALSE)
                .sheet()
                .doWrite(data());

        // not support setting actual file to true.
        FastExcel.write(file03DateWindowing1904, ParameterData.class)
                .excelType(ExcelTypeEnum.XLS)
                .use1904windowing(Boolean.TRUE)
                .sheet()
                .doWrite(data());

        // reading a file
        Assertions.assertThrows(ExcelAnalysisException.class, () -> {
            FastExcel.read(file03DateWindowing1904, new DateWindowingListener(Boolean.TRUE))
                    .excelType(ExcelTypeEnum.XLS)
                    .head(ParameterData.class)
                    .doReadAllSync();
        });
        FastExcel.read(file03DateWindowing1900, new DateWindowingListener(Boolean.FALSE))
                .excelType(ExcelTypeEnum.XLS)
                .use1904windowing(Boolean.FALSE)
                .head(ParameterData.class)
                .doReadAllSync();
        FastExcel.read(file03DateWindowing1904, new DateWindowingListener(Boolean.TRUE))
                .excelType(ExcelTypeEnum.XLS)
                .use1904windowing(Boolean.TRUE)
                .head(ParameterData.class)
                .doReadAllSync();
        FastExcel.read(file03DateWindowing1900Exists, new DateWindowingListener(Boolean.FALSE))
                .excelType(ExcelTypeEnum.XLS)
                .head(ParameterData.class)
                .doReadAllSync();
        FastExcel.read(file03DateWindowing1904Exists, new DateWindowingListener(Boolean.TRUE))
                .excelType(ExcelTypeEnum.XLS)
                .head(ParameterData.class)
                .doReadAllSync();
    }

    @Test
    void test07WriteAndRead() {
        // writing a file
        FastExcel.write(file07DateWindowing1900, ParameterData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .use1904windowing(Boolean.FALSE)
                .sheet()
                .doWrite(data());
        FastExcel.write(file07DateWindowing1904, ParameterData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .use1904windowing(Boolean.TRUE)
                .sheet()
                .doWrite(data());

        // reading a file
        FastExcel.read(file07DateWindowing1900, new DateWindowingListener(Boolean.FALSE))
                .excelType(ExcelTypeEnum.XLSX)
                .head(ParameterData.class)
                .doReadAllSync();
        FastExcel.read(file07DateWindowing1904, new DateWindowingListener(Boolean.TRUE))
                .excelType(ExcelTypeEnum.XLSX)
                .head(ParameterData.class)
                .doReadAllSync();
    }

    @Test
    void testCsvWriteAndRead() {
        // writing a file
        FastExcel.write(fileCsvDateWindowing1900, ParameterData.class)
                .excelType(ExcelTypeEnum.CSV)
                .use1904windowing(Boolean.FALSE)
                .sheet()
                .doWrite(data());
        FastExcel.write(fileCsvDateWindowing1904, ParameterData.class)
                .excelType(ExcelTypeEnum.CSV)
                .use1904windowing(Boolean.TRUE)
                .sheet()
                .doWrite(data());

        // reading a file
        FastExcel.read(fileCsvDateWindowing1900, new DateWindowingListener(Boolean.FALSE))
                .excelType(ExcelTypeEnum.CSV)
                .use1904windowing(Boolean.FALSE)
                .head(ParameterData.class)
                .doReadAllSync();
        Assertions.assertThrows(ExcelAnalysisException.class, () -> {
            FastExcel.read(fileCsvDateWindowing1904, new DateWindowingListener(Boolean.TRUE))
                    .excelType(ExcelTypeEnum.CSV)
                    .head(ParameterData.class)
                    .doReadAllSync();
        });
        FastExcel.read(fileCsvDateWindowing1904, new DateWindowingListener(Boolean.TRUE))
                .excelType(ExcelTypeEnum.CSV)
                .use1904windowing(Boolean.TRUE)
                .head(ParameterData.class)
                .doReadAllSync();
        FastExcel.read(fileCsvDateWindowing1904, new DateWindowingListener(Boolean.FALSE))
                .excelType(ExcelTypeEnum.CSV)
                .head(ParameterData.class)
                .doReadAllSync();
    }

    private List<ParameterData> data() {
        List<ParameterData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ParameterData simpleData = new ParameterData();
            simpleData.setName("姓名" + i);
            simpleData.setDate(new Date());
            list.add(simpleData);
        }
        return list;
    }
}
