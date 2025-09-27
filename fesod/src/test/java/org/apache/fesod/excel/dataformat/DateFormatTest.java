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

package org.apache.fesod.excel.dataformat;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DateFormatTest {

    private static File file07V2;
    private static File file07;
    private static File file03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.readFile("dataformat" + File.separator + "dataformat.xlsx");
        file03 = TestFileUtil.readFile("dataformat" + File.separator + "dataformat.xls");
        file07V2 = TestFileUtil.readFile("dataformat" + File.separator + "dataformatv2.xlsx");
    }

    @Test
    public void t01Read07() {
        readCn(file07);
        readUs(file07);
    }

    @Test
    public void t02Read03() {
        readCn(file03);
        readUs(file03);
    }

    @Test
    public void t03Read() {
        List<Map<Integer, String>> dataMap =
                FastExcel.read(file07V2).headRowNumber(0).doReadAllSync();
        log.info("dataMap:{}", JSON.toJSONString(dataMap));
        Assertions.assertEquals("15:00", dataMap.get(0).get(0));
        Assertions.assertEquals("2023-1-01 00:00:00", dataMap.get(1).get(0));
        Assertions.assertEquals("2023-1-01 00:00:00", dataMap.get(2).get(0));
        Assertions.assertEquals("2023-1-01 00:00:01", dataMap.get(3).get(0));
        Assertions.assertEquals("2023-1-01 00:00:00", dataMap.get(4).get(0));
        Assertions.assertEquals("2023-1-01 00:00:00", dataMap.get(5).get(0));
        Assertions.assertEquals("2023-1-01 00:00:01", dataMap.get(6).get(0));
    }

    private void readCn(File file) {
        List<DateFormatData> list = FastExcel.read(file, DateFormatData.class, null)
                .locale(Locale.CHINA)
                .sheet()
                .doReadSync();
        for (DateFormatData data : list) {
            if (!Objects.equals(data.getDateStringCn(), data.getDate())
                    && !Objects.equals(data.getDateStringCn2(), data.getDate())) {
                log.info("date:cn:{},{},{}", data.getDateStringCn(), data.getDateStringCn2(), data.getDate());
            }
            if (data.getNumberStringCn() != null && !data.getNumberStringCn().equals(data.getNumber())) {
                log.info("number:cn{},{}", data.getNumberStringCn(), data.getNumber());
            }
        }
        for (DateFormatData data : list) {
            // The way dates are read in Chinese is different on Linux and Mac, so it is acceptable if it matches
            // either one.
            // For example, on Linux: 1-Jan -> 1-1月
            // On Mac: 1-Jan -> 1-一月
            Assertions.assertTrue(Objects.equals(data.getDateStringCn(), data.getDate())
                    || Objects.equals(data.getDateStringCn2(), data.getDate()));
            Assertions.assertEquals(data.getNumberStringCn(), data.getNumber());
        }
    }

    private void readUs(File file) {
        List<DateFormatData> list = FastExcel.read(file, DateFormatData.class, null)
                .locale(Locale.US)
                .sheet()
                .doReadSync();
        for (DateFormatData data : list) {
            if (data.getDateStringUs() != null && !data.getDateStringUs().equals(data.getDate())) {
                log.info("date:us:{},{}", data.getDateStringUs(), data.getDate());
            }
            if (data.getNumberStringUs() != null && !data.getNumberStringUs().equals(data.getNumber())) {
                log.info("number:us{},{}", data.getNumberStringUs(), data.getNumber());
            }
        }
        for (DateFormatData data : list) {
            Assertions.assertEquals(data.getDateStringUs(), data.getDate());
            Assertions.assertEquals(data.getNumberStringUs(), data.getNumber());
        }
    }
}
