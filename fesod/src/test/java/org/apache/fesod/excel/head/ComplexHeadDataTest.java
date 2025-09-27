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

package org.apache.fesod.excel.head;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ComplexHeadDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File file07AutomaticMergeHead;
    private static File file03AutomaticMergeHead;
    private static File fileCsvAutomaticMergeHead;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("complexHead07.xlsx");
        file03 = TestFileUtil.createNewFile("complexHead03.xls");
        fileCsv = TestFileUtil.createNewFile("complexHeadCsv.csv");
        file07AutomaticMergeHead = TestFileUtil.createNewFile("complexHeadAutomaticMergeHead07.xlsx");
        file03AutomaticMergeHead = TestFileUtil.createNewFile("complexHeadAutomaticMergeHead03.xls");
        fileCsvAutomaticMergeHead = TestFileUtil.createNewFile("complexHeadAutomaticMergeHeadCsv.csv");
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
        FastExcel.write(file, ComplexHeadData.class).sheet().doWrite(data());
        FastExcel.read(file, ComplexHeadData.class, new ComplexDataListener())
                .xlsxSAXParserFactoryName("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl")
                .sheet()
                .doRead();
    }

    @Test
    public void t11ReadAndWriteAutomaticMergeHead07() {
        readAndWriteAutomaticMergeHead(file07AutomaticMergeHead);
    }

    @Test
    public void t12ReadAndWriteAutomaticMergeHead03() {
        readAndWriteAutomaticMergeHead(file03AutomaticMergeHead);
    }

    @Test
    public void t13ReadAndWriteAutomaticMergeHeadCsv() {
        readAndWriteAutomaticMergeHead(fileCsvAutomaticMergeHead);
    }

    private void readAndWriteAutomaticMergeHead(File file) {
        FastExcel.write(file, ComplexHeadData.class)
                .automaticMergeHead(Boolean.FALSE)
                .sheet()
                .doWrite(data());
        FastExcel.read(file, ComplexHeadData.class, new ComplexDataListener())
                .sheet()
                .doRead();
    }

    private List<ComplexHeadData> data() {
        List<ComplexHeadData> list = new ArrayList<ComplexHeadData>();
        ComplexHeadData data = new ComplexHeadData();
        data.setString0("字符串0");
        data.setString1("字符串1");
        data.setString2("字符串2");
        data.setString3("字符串3");
        data.setString4("字符串4");
        list.add(data);
        return list;
    }
}
