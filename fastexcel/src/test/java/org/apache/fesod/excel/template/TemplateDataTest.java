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

package org.apache.fesod.excel.template;

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
public class TemplateDataTest {

    private static File file07;
    private static File file03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("template07.xlsx");
        file03 = TestFileUtil.createNewFile("template03.xls");
    }

    @Test
    public void t01ReadAndWrite07() {
        readAndWrite07(file07);
    }

    @Test
    public void t02ReadAndWrite03() {
        readAndWrite03(file03);
    }

    private void readAndWrite07(File file) {
        FastExcel.write(file, TemplateData.class)
                .withTemplate(TestFileUtil.readFile("template" + File.separator + "template07.xlsx"))
                .sheet()
                .doWrite(data());
        FastExcel.read(file, TemplateData.class, new TemplateDataListener())
                .headRowNumber(3)
                .sheet()
                .doRead();
    }

    private void readAndWrite03(File file) {
        FastExcel.write(file, TemplateData.class)
                .withTemplate(TestFileUtil.readFile("template" + File.separator + "template03.xls"))
                .sheet()
                .doWrite(data());
        FastExcel.read(file, TemplateData.class, new TemplateDataListener())
                .headRowNumber(3)
                .sheet()
                .doRead();
    }

    private List<TemplateData> data() {
        List<TemplateData> list = new ArrayList<TemplateData>();
        TemplateData data = new TemplateData();
        data.setString0("字符串0");
        data.setString1("字符串01");
        TemplateData data1 = new TemplateData();
        data1.setString0("字符串1");
        data1.setString1("字符串11");
        list.add(data);
        list.add(data1);
        return list;
    }
}
