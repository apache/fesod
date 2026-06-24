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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.cache.Ehcache;
import org.apache.fesod.sheet.enums.ReadDefaultReturnEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.poi.util.TempFile;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Compatible with some special files
 *
 *
 */
@Tag(Tags.ROUND_TRIP)
@Slf4j
@Tag(Tags.FORMAT)
public class CompatibilityTest extends AbstractExcelTest {

    @Test
    public void readXlsWithSpecialCharacters() {
        List<Map<Integer, Object>> list = readCompatibilityFile("t01.xls");
        assertEquals(2, list.size());
        Map<Integer, Object> row1 = list.get(1);
        assertEquals("Q235(碳钢)", row1.get(0));
    }

    @Test
    public void readXlsxWithSharedStringNamespace() {
        // Exist in `sharedStrings.xml` `x:t` start tag, need to be compatible
        List<Map<Integer, Object>> list = readCompatibilityFile("t02.xlsx", 0);
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals(3, list.size());
        Map<Integer, Object> row2 = list.get(2);
        assertEquals("1，2-戊二醇", row2.get(2));
    }

    @Test
    public void readXlsxIgnoreNullColumns() {
        // In the presence of the first line of a lot of null columns, ignore null columns
        List<Map<Integer, Object>> list = readCompatibilityFile("t03.xlsx");
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals(1, list.size());
        Map<Integer, Object> row0 = list.get(0);
        assertEquals(12, row0.size());
    }

    @Test
    public void readXlsxWithNamespacedTag() {
        // Exist in `sheet1.xml` `ns2:t` start tag, need to be compatible
        List<Map<Integer, Object>> list = readCompatibilityFile("t04.xlsx");
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals(56, list.size());
        Map<Integer, Object> row0 = list.get(0);
        assertEquals("QQSJK28F152A012242S0081", row0.get(5));
    }

    @Test
    public void readXlsxDateRounding() {
        // Excel read date needs to be rounded
        List<Map<Integer, String>> list = readCompatibilityFile("t05.xlsx");
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals("2023-01-01 00:00:00", list.get(0).get(0));
        assertEquals("2023-01-01 00:00:00", list.get(1).get(0));
        assertEquals("2023-01-01 00:00:00", list.get(2).get(0));
        assertEquals("2023-01-01 00:00:01", list.get(3).get(0));
        assertEquals("2023-01-01 00:00:01", list.get(4).get(0));
    }

    @Test
    public void readXlsxPrecisionFormat() {
        // Keep error precision digital format
        List<Map<Integer, String>> list = readCompatibilityFile("t06.xlsx", 0);
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals("2087.03", list.get(0).get(2));
    }

    @Test
    public void readXlsxActualDataReturn() {
        // Excel read date needs to be rounded
        List<Map<Integer, Object>> list = FesodSheet.read(compatibilityFile("t07.xlsx"))
                .readDefaultReturn(ReadDefaultReturnEnum.ACTUAL_DATA)
                .sheet()
                .doReadSync();
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals(0, new BigDecimal("24.1998124").compareTo((BigDecimal)
                        list.get(0).get(11)));

        list = readCompatibilityFile("t07.xlsx");
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals("24.20", list.get(0).get(11));
    }

    @Test
    public void readWithCacheAfterTempDeletion() throws Exception {
        // Temporary files may be deleted if there is no operation for a long time, so they need to be recreated.
        File file = new File(tempDir, "compatibility-cache.xlsx");
        FesodSheet.write(file, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(10));

        List<Map<Integer, Object>> list =
                FesodSheet.read(file).readCache(new Ehcache(null, 20)).sheet().doReadSync();
        assertEquals(10L, list.size());

        // Save file content before deleting the system temp dir (which also removes @TempDir)
        byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());

        FileUtils.delete(new File(System.getProperty(TempFile.JAVA_IO_TMPDIR)));

        // Recreate the file after temp dir deletion to continue the test
        file.getParentFile().mkdirs();
        java.nio.file.Files.write(file.toPath(), fileContent);

        list = FesodSheet.read(file).readCache(new Ehcache(null, 20)).sheet().doReadSync();
        assertEquals(10L, list.size());
    }

    @Test
    public void readXlsxWithEscapeSequence() {
        // `SH_x005f_x000D_Z002` exists in `ShardingString.xml` and needs to be replaced by: `SH_x000D_Z002`
        File file = compatibilityFile("t09.xlsx");
        List<Map<Integer, Object>> list =
                FesodSheet.read(file).headRowNumber(0).sheet().doReadSync();
        log.info("data:{}", JSON.toJSONString(list));
        assertEquals(1, list.size());

        assertEquals("SH_x000D_Z002", list.get(0).get(0));
    }

    private File compatibilityFile(String fileName) {
        return readFile("compatibility/" + fileName);
    }

    private <T> List<Map<Integer, T>> readCompatibilityFile(String fileName) {
        return FesodSheet.read(compatibilityFile(fileName)).sheet().doReadSync();
    }

    private <T> List<Map<Integer, T>> readCompatibilityFile(String fileName, int headRowNumber) {
        return FesodSheet.read(compatibilityFile(fileName))
                .sheet()
                .headRowNumber(headRowNumber)
                .doReadSync();
    }
}
