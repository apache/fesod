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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.cache.MapCache;
import org.apache.fesod.excel.converters.string.StringStringConverter;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.support.ExcelTypeEnum;
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
public class ParameterDataTest {

    private static File file07;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("parameter07.xlsx");
        fileCsv = TestFileUtil.createNewFile("parameterCsv.csv");
    }

    @Test
    public void t01ReadAndWrite() throws Exception {
        readAndWrite1(file07, ExcelTypeEnum.XLSX);
        readAndWrite2(file07, ExcelTypeEnum.XLSX);
        readAndWrite3(file07, ExcelTypeEnum.XLSX);
        readAndWrite4(file07, ExcelTypeEnum.XLSX);
        readAndWrite5(file07, ExcelTypeEnum.XLSX);
        readAndWrite6(file07, ExcelTypeEnum.XLSX);
        readAndWrite7(file07, ExcelTypeEnum.XLSX);
    }

    @Test
    public void t02ReadAndWrite() throws Exception {
        readAndWrite1(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite2(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite3(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite4(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite5(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite6(fileCsv, ExcelTypeEnum.CSV);
        readAndWrite7(fileCsv, ExcelTypeEnum.CSV);
    }

    private void readAndWrite1(File file, ExcelTypeEnum type) {
        FastExcel.write(file.getPath()).head(ParameterData.class).sheet().doWrite(data());
        FastExcel.read(file.getPath())
                .head(ParameterData.class)
                .registerReadListener(new ParameterDataListener())
                .sheet()
                .doRead();
    }

    private void readAndWrite2(File file, ExcelTypeEnum type) {
        FastExcel.write(file.getPath(), ParameterData.class).sheet().doWrite(data());
        FastExcel.read(file.getPath(), ParameterData.class, new ParameterDataListener())
                .sheet()
                .doRead();
    }

    private void readAndWrite3(File file, ExcelTypeEnum type) throws Exception {
        FastExcel.write(new FileOutputStream(file))
                .excelType(type)
                .head(ParameterData.class)
                .sheet()
                .doWrite(data());
        FastExcel.read(file.getPath())
                .head(ParameterData.class)
                .registerReadListener(new ParameterDataListener())
                .sheet()
                .doRead();
    }

    private void readAndWrite4(File file, ExcelTypeEnum type) throws Exception {
        FastExcel.write(new FileOutputStream(file), ParameterData.class)
                .excelType(type)
                .sheet()
                .doWrite(data());
        FastExcel.read(file.getPath(), new ParameterDataListener())
                .head(ParameterData.class)
                .sheet()
                .doRead();
    }

    private void readAndWrite5(File file, ExcelTypeEnum type) throws Exception {
        ExcelWriter excelWriter = FastExcel.write(new FileOutputStream(file))
                .excelType(type)
                .head(ParameterData.class)
                .relativeHeadRowIndex(0)
                .build();
        WriteSheet writeSheet = FastExcel.writerSheet(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.FALSE)
                .build();
        WriteTable writeTable = FastExcel.writerTable(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.TRUE)
                .build();
        excelWriter.write(data(), writeSheet, writeTable);
        excelWriter.finish();

        ExcelReader excelReader = FastExcel.read(file.getPath(), new ParameterDataListener())
                .head(ParameterData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        ReadSheet readSheet = FastExcel.readSheet()
                .head(ParameterData.class)
                .use1904windowing(Boolean.FALSE)
                .headRowNumber(1)
                .sheetNo(0)
                .sheetName("0")
                .build();
        excelReader.read(readSheet);
        excelReader.finish();

        excelReader = FastExcel.read(file.getPath(), new ParameterDataListener())
                .head(ParameterData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        excelReader.read();
        excelReader.finish();
    }

    private void readAndWrite6(File file, ExcelTypeEnum type) throws Exception {
        ExcelWriter excelWriter = FastExcel.write(new FileOutputStream(file))
                .excelType(type)
                .head(ParameterData.class)
                .relativeHeadRowIndex(0)
                .build();
        WriteSheet writeSheet = FastExcel.writerSheet(0)
                .relativeHeadRowIndex(0)
                .needHead(Boolean.FALSE)
                .build();
        WriteTable writeTable = FastExcel.writerTable(0)
                .registerConverter(new StringStringConverter())
                .relativeHeadRowIndex(0)
                .needHead(Boolean.TRUE)
                .build();
        excelWriter.write(data(), writeSheet, writeTable);
        excelWriter.finish();

        ExcelReader excelReader = FastExcel.read(file.getPath(), new ParameterDataListener())
                .head(ParameterData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        ReadSheet readSheet = FastExcel.readSheet("0")
                .head(ParameterData.class)
                .use1904windowing(Boolean.FALSE)
                .headRowNumber(1)
                .sheetNo(0)
                .build();
        excelReader.read(readSheet);
        excelReader.finish();

        excelReader = FastExcel.read(file.getPath(), new ParameterDataListener())
                .head(ParameterData.class)
                .mandatoryUseInputStream(Boolean.FALSE)
                .autoCloseStream(Boolean.TRUE)
                .readCache(new MapCache())
                .build();
        excelReader.read();
        excelReader.finish();
    }

    private void readAndWrite7(File file, ExcelTypeEnum type) {
        FastExcel.write(file, ParameterData.class)
                .registerConverter(new StringStringConverter())
                .sheet()
                .registerConverter(new StringStringConverter())
                .needHead(Boolean.FALSE)
                .table(0)
                .needHead(Boolean.TRUE)
                .doWrite(data());
        FastExcel.read(file.getPath())
                .head(ParameterData.class)
                .registerReadListener(new ParameterDataListener())
                .sheet()
                .registerConverter(new StringStringConverter())
                .doRead();
    }

    private List<ParameterData> data() {
        List<ParameterData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ParameterData simpleData = new ParameterData();
            simpleData.setName("姓名" + i);
            list.add(simpleData);
        }
        return list;
    }
}
