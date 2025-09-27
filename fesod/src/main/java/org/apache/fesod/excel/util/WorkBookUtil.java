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

package org.apache.fesod.excel.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.apache.fesod.excel.metadata.csv.CsvWorkbook;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.excel.write.metadata.style.WriteCellStyle;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;

/**
 *
 */
public class WorkBookUtil {

    private WorkBookUtil() {}

    public static void createWorkBook(WriteWorkbookHolder writeWorkbookHolder) throws IOException {
        switch (writeWorkbookHolder.getExcelType()) {
            case XLSX:
                if (writeWorkbookHolder.getTempTemplateInputStream() != null) {
                    XSSFWorkbook xssfWorkbook = new XSSFWorkbook(writeWorkbookHolder.getTempTemplateInputStream());
                    writeWorkbookHolder.setCachedWorkbook(xssfWorkbook);
                    if (writeWorkbookHolder.getInMemory()) {
                        writeWorkbookHolder.setWorkbook(xssfWorkbook);
                    } else {
                        writeWorkbookHolder.setWorkbook(new SXSSFWorkbook(xssfWorkbook));
                    }
                    return;
                }
                Workbook workbook = writeWorkbookHolder.getInMemory() ? new XSSFWorkbook() : new SXSSFWorkbook();
                Boolean use1904windowing =
                        writeWorkbookHolder.getGlobalConfiguration().getUse1904windowing();
                if (use1904windowing != null) {
                    CTWorkbook ctWorkbook;
                    if (workbook instanceof XSSFWorkbook) {
                        ctWorkbook = ((XSSFWorkbook) workbook).getCTWorkbook();
                    } else {
                        ctWorkbook =
                                ((SXSSFWorkbook) workbook).getXSSFWorkbook().getCTWorkbook();
                    }
                    ctWorkbook.getWorkbookPr().setDate1904(use1904windowing);
                }
                writeWorkbookHolder.setCachedWorkbook(workbook);
                writeWorkbookHolder.setWorkbook(workbook);
                return;
            case XLS:
                HSSFWorkbook hssfWorkbook;
                if (writeWorkbookHolder.getTempTemplateInputStream() != null) {
                    hssfWorkbook =
                            new HSSFWorkbook(new POIFSFileSystem(writeWorkbookHolder.getTempTemplateInputStream()));
                } else {
                    hssfWorkbook = new HSSFWorkbook();
                }
                writeWorkbookHolder.setCachedWorkbook(hssfWorkbook);
                writeWorkbookHolder.setWorkbook(hssfWorkbook);
                if (writeWorkbookHolder.getPassword() != null) {
                    Biff8EncryptionKey.setCurrentUserPassword(writeWorkbookHolder.getPassword());
                    hssfWorkbook.writeProtectWorkbook(writeWorkbookHolder.getPassword(), StringUtils.EMPTY);
                }
                return;
            case CSV:
                CsvWorkbook csvWorkbook = new CsvWorkbook(
                        new PrintWriter(new OutputStreamWriter(
                                writeWorkbookHolder.getOutputStream(), writeWorkbookHolder.getCharset())),
                        writeWorkbookHolder.getGlobalConfiguration().getLocale(),
                        writeWorkbookHolder.getGlobalConfiguration().getUse1904windowing(),
                        writeWorkbookHolder.getGlobalConfiguration().getUseScientificFormat(),
                        writeWorkbookHolder.getCharset(),
                        writeWorkbookHolder.getWithBom());
                if (writeWorkbookHolder.getWriteWorkbook().getCsvFormat() != null) {
                    csvWorkbook.setCsvFormat(
                            writeWorkbookHolder.getWriteWorkbook().getCsvFormat());
                }
                writeWorkbookHolder.setCachedWorkbook(csvWorkbook);
                writeWorkbookHolder.setWorkbook(csvWorkbook);
                return;
            default:
                throw new UnsupportedOperationException("Wrong excel type.");
        }
    }

    public static Sheet createSheet(Workbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    public static Row createRow(Sheet sheet, int rowNum) {
        return sheet.createRow(rowNum);
    }

    public static Cell createCell(Row row, int colNum) {
        return row.createCell(colNum);
    }

    public static Cell createCell(Row row, int colNum, CellStyle cellStyle) {
        Cell cell = row.createCell(colNum);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static Cell createCell(Row row, int colNum, CellStyle cellStyle, String cellValue) {
        Cell cell = createCell(row, colNum, cellStyle);
        cell.setCellValue(cellValue);
        return cell;
    }

    public static Cell createCell(Row row, int colNum, String cellValue) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(cellValue);
        return cell;
    }

    public static void fillDataFormat(WriteCellData<?> cellData, String format, String defaultFormat) {
        if (cellData.getWriteCellStyle() == null) {
            cellData.setWriteCellStyle(new WriteCellStyle());
        }
        if (cellData.getWriteCellStyle().getDataFormatData() == null) {
            cellData.getWriteCellStyle().setDataFormatData(new DataFormatData());
        }
        if (cellData.getWriteCellStyle().getDataFormatData().getFormat() == null) {
            if (format == null) {
                cellData.getWriteCellStyle().getDataFormatData().setFormat(defaultFormat);
            } else {
                cellData.getWriteCellStyle().getDataFormatData().setFormat(format);
            }
        }
    }
}
