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
public class NoHeadDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("noHead07.xlsx");
        file03 = TestFileUtil.createNewFile("noHead03.xls");
        fileCsv = TestFileUtil.createNewFile("noHeadCsv.csv");
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
        FastExcel.write(file, NoHeadData.class).needHead(Boolean.FALSE).sheet().doWrite(data());
        FastExcel.read(file, NoHeadData.class, new NoHeadDataListener())
                .headRowNumber(0)
                .sheet()
                .doRead();
    }

    private List<NoHeadData> data() {
        List<NoHeadData> list = new ArrayList<NoHeadData>();
        NoHeadData data = new NoHeadData();
        data.setString("字符串0");
        list.add(data);
        return list;
    }
}
