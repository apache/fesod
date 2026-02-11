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

package org.apache.fesod.sheet.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.commons.csv.CSVFormat;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.DataFormatData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;

/**
 * Tests {@link WorkBookUtil}
 */
@ExtendWith(MockitoExtension.class)
class WorkBookUtilTest {

    @Mock
    private WriteWorkbookHolder writeWorkbookHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @Mock
    private WriteWorkbook writeWorkbook;

    @AfterEach
    void tearDown() {
        Biff8EncryptionKey.setCurrentUserPassword(null);
    }

    @Test
    void test_createWorkBook_XLSX_SXSSF() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLSX);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(null);
        Mockito.when(writeWorkbookHolder.getInMemory()).thenReturn(false);
        Mockito.when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
        Mockito.when(globalConfiguration.getUse1904windowing()).thenReturn(null);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());
        Assertions.assertInstanceOf(SXSSFWorkbook.class, workbookCaptor.getValue());
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(Workbook.class));
    }

    @Test
    void test_createWorkBook_XLSX_SXSSF_use1904windowing() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLSX);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(null);
        Mockito.when(writeWorkbookHolder.getInMemory()).thenReturn(false);
        Mockito.when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
        Mockito.when(globalConfiguration.getUse1904windowing()).thenReturn(true);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());

        Assertions.assertInstanceOf(SXSSFWorkbook.class, workbookCaptor.getValue());

        SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbookCaptor.getValue();
        CTWorkbook ctWorkbook = sxssfWorkbook.getXSSFWorkbook().getCTWorkbook();
        Assertions.assertTrue(ctWorkbook.getWorkbookPr().getDate1904());
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(Workbook.class));
    }

    @Test
    void test_createWorkBook_XLSX_SXSSF_withTemplate() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLSX);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(createXLSX());
        Mockito.when(writeWorkbookHolder.getInMemory()).thenReturn(false);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(XSSFWorkbook.class));
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());
        Assertions.assertInstanceOf(SXSSFWorkbook.class, workbookCaptor.getValue());
    }

    @Test
    void test_createWorkBook_XLSX_SXSSF_withTemplate_inMemory() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLSX);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(createXLSX());
        Mockito.when(writeWorkbookHolder.getInMemory()).thenReturn(true);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(XSSFWorkbook.class));
        Mockito.verify(writeWorkbookHolder).setWorkbook(Mockito.any(XSSFWorkbook.class));
    }

    private ByteArrayInputStream createXLSX() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.createSheet("Template");
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Test
    void test_createWorkBook_XLSX_XSSF() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLSX);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(null);
        Mockito.when(writeWorkbookHolder.getInMemory()).thenReturn(true);
        Mockito.when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());

        Assertions.assertInstanceOf(XSSFWorkbook.class, workbookCaptor.getValue());
    }

    @Test
    void test_createWorkBook_XLS() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLS);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(null);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(HSSFWorkbook.class));
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());

        Assertions.assertInstanceOf(HSSFWorkbook.class, workbookCaptor.getValue());
    }

    @Test
    void test_createWorkBook_XLS_withTemplate() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLS);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(createXLS());

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setCachedWorkbook(Mockito.any(HSSFWorkbook.class));
        ArgumentCaptor<Workbook> workbookCaptor = ArgumentCaptor.forClass(Workbook.class);
        Mockito.verify(writeWorkbookHolder).setWorkbook(workbookCaptor.capture());

        Assertions.assertInstanceOf(HSSFWorkbook.class, workbookCaptor.getValue());
    }

    private ByteArrayInputStream createXLS() throws IOException {
        try (HSSFWorkbook workbook = new HSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.createSheet("Template");
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    @Test
    void test_createWorkBook_XLS_Password() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.XLS);
        Mockito.when(writeWorkbookHolder.getTempTemplateInputStream()).thenReturn(null);
        Mockito.when(writeWorkbookHolder.getPassword()).thenReturn("123456");

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setWorkbook(Mockito.any(HSSFWorkbook.class));
    }

    @Test
    void test_createWorkBook_CSV() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.CSV);
        Mockito.when(writeWorkbookHolder.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(writeWorkbookHolder.getCharset()).thenReturn(StandardCharsets.UTF_8);
        Mockito.when(writeWorkbookHolder.getWriteWorkbook()).thenReturn(writeWorkbook);

        Mockito.when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
        Mockito.when(globalConfiguration.getLocale()).thenReturn(Locale.SIMPLIFIED_CHINESE);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        // Verify
        Mockito.verify(writeWorkbookHolder).setWorkbook(Mockito.any(Workbook.class));
    }

    @Test
    void test_createWorkBook_CSV_withFormat() throws IOException {
        // Setup
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(ExcelTypeEnum.CSV);
        Mockito.when(writeWorkbookHolder.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        Mockito.when(writeWorkbookHolder.getCharset()).thenReturn(StandardCharsets.UTF_8);
        Mockito.when(writeWorkbookHolder.getWriteWorkbook()).thenReturn(writeWorkbook);
        Mockito.when(writeWorkbook.getCsvFormat()).thenReturn(CSVFormat.DEFAULT);

        Mockito.when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
        Mockito.when(globalConfiguration.getLocale()).thenReturn(Locale.SIMPLIFIED_CHINESE);

        // Execute
        WorkBookUtil.createWorkBook(writeWorkbookHolder);

        Mockito.verify(writeWorkbookHolder).setWorkbook(Mockito.any(Workbook.class));
    }

    @Test
    void test_createWorkBook_UnknownType() {
        Mockito.when(writeWorkbookHolder.getExcelType()).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> WorkBookUtil.createWorkBook(writeWorkbookHolder));
    }

    @Test
    void test_createSheet() {
        Workbook workbook = Mockito.mock(Workbook.class);
        String sheetName = "TestSheet";

        WorkBookUtil.createSheet(workbook, sheetName);

        Mockito.verify(workbook).createSheet(sheetName);
    }

    @Test
    void test_createRow() {
        Sheet sheet = Mockito.mock(Sheet.class);

        WorkBookUtil.createRow(sheet, 1);

        Mockito.verify(sheet).createRow(1);
    }

    @Test
    void test_createCell_basic() {
        Row row = Mockito.mock(Row.class);

        WorkBookUtil.createCell(row, 2);

        Mockito.verify(row).createCell(2);
    }

    @Test
    void test_createCell_withStyle() {
        Row row = Mockito.mock(Row.class);
        Cell cell = Mockito.mock(Cell.class);
        CellStyle style = Mockito.mock(CellStyle.class);
        Mockito.when(row.createCell(Mockito.anyInt())).thenReturn(cell);

        WorkBookUtil.createCell(row, 2, style);

        Mockito.verify(row).createCell(2);
        Mockito.verify(cell).setCellStyle(style);
    }

    @Test
    void test_createCell_withValue() {
        Row row = Mockito.mock(Row.class);
        Cell cell = Mockito.mock(Cell.class);
        CellStyle style = Mockito.mock(CellStyle.class);
        Mockito.when(row.createCell(Mockito.anyInt())).thenReturn(cell);

        WorkBookUtil.createCell(row, 2, style, "Hello");

        Mockito.verify(cell).setCellStyle(style);
        Mockito.verify(cell).setCellValue("Hello");
    }

    @Test
    void test_createCell_stringOnly() {
        Row row = Mockito.mock(Row.class);
        Cell cell = Mockito.mock(Cell.class);
        Mockito.when(row.createCell(Mockito.anyInt())).thenReturn(cell);

        WorkBookUtil.createCell(row, 2, "World");

        Mockito.verify(cell).setCellValue("World");
    }

    @Test
    void test_fillDataFormat_allNull() {
        WriteCellData<?> cellData = new WriteCellData<>();
        String format = null;
        String defaultFormat = "yyyy-MM-dd";

        WorkBookUtil.fillDataFormat(cellData, format, defaultFormat);

        Assertions.assertNotNull(cellData.getWriteCellStyle());
        Assertions.assertNotNull(cellData.getWriteCellStyle().getDataFormatData());
        Assertions.assertEquals(
                defaultFormat, cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void test_fillDataFormat_withFormat() {
        WriteCellData<?> cellData = new WriteCellData<>();
        String format = "#.00";
        String defaultFormat = "General";

        WorkBookUtil.fillDataFormat(cellData, format, defaultFormat);

        Assertions.assertEquals(
                format, cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }

    @Test
    void test_fillDataFormat_existingFormat() {
        WriteCellData<?> cellData = new WriteCellData<>();
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setFormat("Existing");
        writeCellStyle.setDataFormatData(dataFormatData);
        cellData.setWriteCellStyle(writeCellStyle);

        WorkBookUtil.fillDataFormat(cellData, "NewFormat", "Default");

        Assertions.assertEquals(
                "Existing", cellData.getWriteCellStyle().getDataFormatData().getFormat());
    }
}
