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

package org.apache.fesod.sheet.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.FastExcel;
import org.apache.fesod.sheet.metadata.csv.CsvRow;
import org.apache.fesod.sheet.metadata.csv.CsvSheet;
import org.apache.fesod.sheet.metadata.csv.CsvWorkbook;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CsvRowTest {

    @Mock
    private CsvWorkbook csvWorkbook;

    @Mock
    private CsvSheet csvSheet;

    private CsvRow csvRow;

    private static File fileCsvNoModel;
    private static File fileCsvModel;

    @BeforeEach
    void setUp() {
        csvRow = new CsvRow(csvWorkbook, csvSheet, 1);

        Cell firstCell = csvRow.createCell(0, CellType.STRING);
        firstCell.setCellValue("No");
        Cell middleCell = csvRow.createCell(1, CellType.STRING);
        middleCell.setCellValue("Name");
        Cell lastCell = csvRow.createCell(2, CellType.STRING);
        lastCell.setCellValue("Age");

        fileCsvNoModel = TestFileUtil.createNewFile("csv-no-model.csv");
        fileCsvModel = TestFileUtil.createNewFile("csv-model.csv");
    }

    @Test
    void testGetCellWithFirstIndexShouldReturnFirstCell() {
        Cell actualCell = csvRow.getCell(0);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("No", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithMiddleIndexShouldReturnMiddleCell() {
        Cell actualCell = csvRow.getCell(1);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("Name", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithLastIndexShouldReturnLastCell() {
        Cell actualCell = csvRow.getCell(2);
        Assertions.assertNotNull(actualCell);
        Assertions.assertEquals("Age", actualCell.getStringCellValue());
    }

    @Test
    void testGetCellWithOutOfBoundsIndexShouldReturnNull() {
        Cell actualCell1 = csvRow.getCell(3);
        Assertions.assertNull(actualCell1);

        Cell actualCell2 = csvRow.getCell(-1);
        Assertions.assertNull(actualCell2);
    }

    @Test
    void testCsvWriteWithOutModelShouldSuccess() {
        FastExcel.write(fileCsvNoModel)
                .head(head())
                .registerWriteHandler(new AssertCsvHeadDataWriteHandler(head(), data()))
                .csv()
                .doWrite(data());
    }

    @Test
    void testCsvWriteWithModelShouldSuccess() {
        FastExcel.write(fileCsvModel)
                .head(SimpleCsvData.class)
                .registerWriteHandler(new AssertCsvHeadDataWriteHandler(head(), data()))
                .csv()
                .doWrite(modelData());
    }

    private static List<SimpleCsvData> modelData() {
        List<SimpleCsvData> data = new ArrayList<>();
        data.add(new SimpleCsvData("1", "Jackson", "20"));
        data.add(new SimpleCsvData("2", "Tom", "21"));
        data.add(new SimpleCsvData("3", "Sophia", "20"));
        return data;
    }

    private static List<List<String>> data() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("1", "Jackson", "20"));
        data.add(Arrays.asList("2", "Tom", "21"));
        data.add(Arrays.asList("3", "Sophia", "20"));
        return data;
    }

    private List<List<String>> head() {
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("No"));
        head.add(Arrays.asList("Name"));
        head.add(Arrays.asList("Age"));
        return head;
    }
}
