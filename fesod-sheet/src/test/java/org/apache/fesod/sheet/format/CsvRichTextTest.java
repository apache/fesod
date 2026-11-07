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

package org.apache.fesod.sheet.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.csv.CsvRichTextString;
import org.apache.fesod.sheet.metadata.csv.CsvRow;
import org.apache.fesod.sheet.metadata.data.RichTextStringData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(Tags.FORMAT)
@Tag(Tags.WRITE)
class CsvRichTextTest extends AbstractExcelTest {

    private static final String TEXT = "保留, \"rich text\"\nsecond line";

    @ParameterizedTest(name = "{0}, inMemory={1}")
    @MethodSource("formatsAndMemoryModes")
    void rich_text_is_written_as_plain_text(ExcelFormat format, boolean inMemory) throws IOException {
        File file = createTempFile(format);
        List<List<Object>> data = Arrays.asList(
                Arrays.asList("left", richText("KEEP-TEXT"), "right"),
                Arrays.asList("left", richText(TEXT), "right"),
                Arrays.asList("left", richText(""), "right"),
                Arrays.asList("left", "PLAIN-TEXT", "right"));

        FesodSheet.write(file)
                .excelType(format.toExcelTypeEnum())
                .inMemory(inMemory)
                .withBom(false)
                .needHead(false)
                .sheet()
                .doWrite(data);

        assertEquals(
                Arrays.asList(
                        Arrays.asList("left", "KEEP-TEXT", "right"),
                        Arrays.asList("left", TEXT, "right"),
                        Arrays.asList("left", "", "right"),
                        Arrays.asList("left", "PLAIN-TEXT", "right")),
                readRows(file, format));
    }

    @ParameterizedTest(name = "{0}, inMemory={1}")
    @MethodSource("formatsAndMemoryModes")
    void plain_and_rich_text_setters_replace_each_other(ExcelFormat format, boolean inMemory) throws IOException {
        File file = writeWithHandler(format, inMemory, new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                if (context.getColumnIndex() != 1) {
                    return;
                }
                Cell cell = context.getCell();
                assertText(cell, "ORIGINAL");
                RichTextString richText = createRichText(format, "RICH");
                cell.setCellValue(richText);
                if (format == ExcelFormat.CSV) {
                    assertSame(richText, cell.getRichStringCellValue());
                }
                assertText(cell, "RICH");
                cell.setCellValue("PLAIN");
                assertText(cell, "PLAIN");
                cell.setCellValue(createRichText(format, TEXT));
                assertText(cell, TEXT);
            }
        });

        assertEquals(Collections.singletonList(Arrays.asList("left", TEXT, "right")), readRows(file, format));
    }

    @Test
    void csv_rich_text_instances_replace_plain_text() {
        Cell cell = new CsvRow(null, null, 0).createCell(0);
        cell.setCellValue("ORIGINAL");
        RichTextString richText = new CsvRichTextString(TEXT);
        cell.setCellValue(richText);
        assertSame(richText, cell.getRichStringCellValue());
        assertText(cell, TEXT);
        cell.setCellValue("PLAIN");
        assertText(cell, "PLAIN");
    }

    @ParameterizedTest(name = "{0}, inMemory={1}")
    @MethodSource("formatsAndMemoryModes")
    void null_and_blank_values_clear_rich_text(ExcelFormat format, boolean inMemory) throws IOException {
        File file = writeWithHandler(format, inMemory, new CellWriteHandler() {
            @Override
            public void afterCellDispose(CellWriteHandlerContext context) {
                if (context.getColumnIndex() != 1) {
                    return;
                }
                Cell cell = context.getCell();
                cell.setCellValue(createRichText(format, "discarded"));
                cell.setCellValue((String) null);
                assertEquals(CellType.BLANK, cell.getCellType());
                cell.setCellValue("replacement");
                assertText(cell, "replacement");
                cell.setBlank();
                cell.setCellValue(42);
                cell.setCellValue(createRichText(format, TEXT));
                assertText(cell, TEXT);
                cell.setCellValue((RichTextString) null);
                assertEquals(CellType.BLANK, cell.getCellType());
            }
        });

        assertEquals(Collections.singletonList(Arrays.asList("left", "", "right")), readRows(file, format));
    }

    private File writeWithHandler(ExcelFormat format, boolean inMemory, CellWriteHandler handler) throws IOException {
        File file = createTempFile(format);
        FesodSheet.write(file)
                .excelType(format.toExcelTypeEnum())
                .inMemory(inMemory)
                .withBom(false)
                .needHead(false)
                .registerWriteHandler(handler)
                .sheet()
                .doWrite(Collections.singletonList(Arrays.asList("left", "ORIGINAL", "right")));
        return file;
    }

    private static WriteCellData<String> richText(String text) {
        WriteCellData<String> data = new WriteCellData<>(CellDataTypeEnum.RICH_TEXT_STRING);
        data.setRichTextStringDataValue(new RichTextStringData(text));
        return data;
    }

    private static RichTextString createRichText(ExcelFormat format, String text) {
        return format == ExcelFormat.XLS ? new HSSFRichTextString(text) : new XSSFRichTextString(text);
    }

    private static void assertText(Cell cell, String text) {
        assertEquals(CellType.STRING, cell.getCellType());
        assertEquals(text, cell.getStringCellValue());
        assertNotNull(cell.getRichStringCellValue());
        assertEquals(text, cell.getRichStringCellValue().getString());
    }

    private static Stream<Arguments> formatsAndMemoryModes() {
        return Arrays.stream(ExcelFormat.values())
                .flatMap(format -> Stream.of(Arguments.of(format, false), Arguments.of(format, true)));
    }

    private static List<List<String>> readRows(File file, ExcelFormat format) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        if (format == ExcelFormat.CSV) {
            try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
                    CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
                for (CSVRecord record : parser) {
                    rows.add(Arrays.asList(record.get(0), record.get(1), record.get(2)));
                    assertEquals(3, record.size());
                }
            }
        } else {
            try (Workbook workbook = WorkbookFactory.create(file)) {
                for (Row row : workbook.getSheetAt(0)) {
                    rows.add(Arrays.asList(
                            row.getCell(0).getStringCellValue(),
                            row.getCell(1).getStringCellValue(),
                            row.getCell(2).getStringCellValue()));
                }
            }
        }
        return rows;
    }
}
