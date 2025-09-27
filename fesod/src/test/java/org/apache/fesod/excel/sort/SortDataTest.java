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

package org.apache.fesod.excel.sort;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SortDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File sortNoHead07;
    private static File sortNoHead03;
    private static File sortNoHeadCsv;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("sort.xlsx");
        file03 = TestFileUtil.createNewFile("sort.xls");
        fileCsv = TestFileUtil.createNewFile("sort.csv");
        sortNoHead07 = TestFileUtil.createNewFile("sortNoHead.xlsx");
        sortNoHead03 = TestFileUtil.createNewFile("sortNoHead.xls");
        sortNoHeadCsv = TestFileUtil.createNewFile("sortNoHead.csv");
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

    @Test
    public void t11ReadAndWriteNoHead07() {
        readAndWriteNoHead(sortNoHead07);
    }

    @Test
    public void t12ReadAndWriteNoHead03() {
        readAndWriteNoHead(sortNoHead03);
    }

    @Test
    public void t13ReadAndWriteNoHeadCsv() {
        readAndWriteNoHead(sortNoHeadCsv);
    }

    private void readAndWrite(File file) {
        FastExcel.write(file, SortData.class).sheet().doWrite(data());
        List<Map<Integer, String>> dataMap = FastExcel.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals("column1", record.get(0));
        Assertions.assertEquals("column2", record.get(1));
        Assertions.assertEquals("column3", record.get(2));
        Assertions.assertEquals("column4", record.get(3));
        Assertions.assertEquals("column5", record.get(4));
        Assertions.assertEquals("column6", record.get(5));

        FastExcel.read(file, SortData.class, new SortDataListener()).sheet().doRead();
    }

    private void readAndWriteNoHead(File file) {
        FastExcel.write(file).head(head()).sheet().doWrite(data());
        List<Map<Integer, String>> dataMap = FastExcel.read(file).sheet().doReadSync();
        Assertions.assertEquals(1, dataMap.size());
        Map<Integer, String> record = dataMap.get(0);
        Assertions.assertEquals("column1", record.get(0));
        Assertions.assertEquals("column2", record.get(1));
        Assertions.assertEquals("column3", record.get(2));
        Assertions.assertEquals("column4", record.get(3));
        Assertions.assertEquals("column5", record.get(4));
        Assertions.assertEquals("column6", record.get(5));
        FastExcel.read(file, SortData.class, new SortDataListener()).sheet().doRead();
    }

    private List<List<String>> head() {
        List<List<String>> head = new ArrayList<List<String>>();
        head.add(Collections.singletonList("column1"));
        head.add(Collections.singletonList("column2"));
        head.add(Collections.singletonList("column3"));
        head.add(Collections.singletonList("column4"));
        head.add(Collections.singletonList("column5"));
        head.add(Collections.singletonList("column6"));
        return head;
    }

    private List<SortData> data() {
        List<SortData> list = new ArrayList<SortData>();
        SortData sortData = new SortData();
        sortData.setColumn1("column1");
        sortData.setColumn2("column2");
        sortData.setColumn3("column3");
        sortData.setColumn4("column4");
        sortData.setColumn5("column5");
        sortData.setColumn6("column6");
        list.add(sortData);
        return list;
    }
}
