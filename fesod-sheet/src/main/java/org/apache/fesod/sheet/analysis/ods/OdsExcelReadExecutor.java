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

package org.apache.fesod.sheet.analysis.ods;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.fesod.sheet.analysis.ExcelReadExecutor;
import org.apache.fesod.sheet.context.ods.OdsReadContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelAnalysisStopSheetException;
import org.apache.fesod.sheet.metadata.Cell;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.sheet.read.metadata.holder.ods.OdsReadWorkbookHolder;
import org.apache.fesod.sheet.util.SheetUtils;
import org.apache.fesod.sheet.util.StringUtils;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.w3c.dom.Node;

/**
 * ODS Excel Read Executor, responsible for reading and processing ODS (OpenDocument Spreadsheet) files.
 */
@Slf4j
public class OdsExcelReadExecutor implements ExcelReadExecutor {

    // List of sheets to be read
    private final List<ReadSheet> sheetList;
    // Context for ODS reading operation
    private final OdsReadContext odsReadContext;
    // ODF Spreadsheet Document
    private OdfSpreadsheetDocument odfDocument;

    public OdsExcelReadExecutor(OdsReadContext odsReadContext) {
        this.odsReadContext = odsReadContext;
        this.sheetList = new ArrayList<>();
        initSheetList();
    }

    /**
     * Initialize the sheet list from the ODS document.
     */
    private void initSheetList() {
        try {
            OdsReadWorkbookHolder workbookHolder = odsReadContext.odsReadWorkbookHolder();
            if (workbookHolder.getFile() != null) {
                odfDocument = OdfSpreadsheetDocument.loadDocument(workbookHolder.getFile());
            } else if (workbookHolder.getInputStream() != null) {
                odfDocument = OdfSpreadsheetDocument.loadDocument(workbookHolder.getInputStream());
            } else {
                throw new ExcelAnalysisException("File and inputStream must be a non-null.");
            }
            workbookHolder.setOdfSpreadsheetDocument(odfDocument);

            List<OdfTable> tables = odfDocument.getTableList();
            for (int i = 0; i < tables.size(); i++) {
                OdfTable table = tables.get(i);
                ReadSheet readSheet = new ReadSheet();
                readSheet.setSheetNo(i);
                readSheet.setSheetName(table.getTableName());
                sheetList.add(readSheet);
            }
        } catch (Exception e) {
            throw new ExcelAnalysisException("Failed to load ODS document", e);
        }
    }

    @Override
    public List<ReadSheet> sheetList() {
        return sheetList;
    }

    /**
     * Execute the reading process for all sheets.
     */
    @Override
    public void execute() {
        List<OdfTable> tables = odfDocument.getTableList();

        for (ReadSheet readSheet : sheetList) {
            readSheet = SheetUtils.match(readSheet, odsReadContext);
            if (readSheet == null) {
                continue;
            }

            try {
                odsReadContext.currentSheet(readSheet);

                OdfTable table = tables.get(readSheet.getSheetNo());
                int rowCount = table.getRowCount();

                for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                    OdfTableRow row = table.getRowByIndex(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    dealRow(table, row, rowIndex);
                }
            } catch (ExcelAnalysisStopSheetException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Custom stop!", e);
                }
            }

            odsReadContext.analysisEventProcessor().endSheet(odsReadContext);
        }
    }

    /**
     * Process a single row from the ODS table.
     * Uses DOM traversal to avoid performance issues with getCellByIndex() and getCellCount().
     *
     * @param table    The ODF table
     * @param row      The ODF table row
     * @param rowIndex The index of the current row
     */
    private void dealRow(OdfTable table, OdfTableRow row, int rowIndex) {
        Map<Integer, Cell> cellMap = new LinkedHashMap<>();
        Boolean autoTrim =
                odsReadContext.odsReadWorkbookHolder().globalConfiguration().getAutoTrim();
        Boolean autoStrip =
                odsReadContext.odsReadWorkbookHolder().globalConfiguration().getAutoStrip();

        // Use DOM traversal to iterate through cells directly
        // This avoids performance issues with getCellByIndex() which can trigger column expansion
        int columnIndex = 0;
        Node cellNode = row.getOdfElement().getFirstChild();

        while (cellNode != null) {
            if (cellNode instanceof TableTableCellElementBase) {
                TableTableCellElementBase cellElement = (TableTableCellElementBase) cellNode;

                // Handle repeated cells
                int repeatCount = 1;
                Integer columnsRepeated = cellElement.getTableNumberColumnsRepeatedAttribute();
                if (columnsRepeated != null && columnsRepeated > 1) {
                    repeatCount = columnsRepeated;
                }

                // Process cell data directly from the DOM element
                processCellDataFromElement(cellMap, cellElement, rowIndex, columnIndex, autoTrim, autoStrip);

                // For repeated cells, advance the column index accordingly
                columnIndex += repeatCount;
            }
            cellNode = cellNode.getNextSibling();
        }

        RowTypeEnum rowType = MapUtils.isEmpty(cellMap) ? RowTypeEnum.EMPTY : RowTypeEnum.DATA;
        ReadRowHolder readRowHolder = new ReadRowHolder(
                rowIndex, rowType, odsReadContext.readWorkbookHolder().getGlobalConfiguration(), cellMap);
        odsReadContext.readRowHolder(readRowHolder);

        odsReadContext.odsReadSheetHolder().setCellMap(cellMap);
        odsReadContext.odsReadSheetHolder().setRowIndex(rowIndex);
        odsReadContext.analysisEventProcessor().endRow(odsReadContext);
    }

    /**
     * Process cell data directly from DOM element and add to the cell map.
     */
    private void processCellDataFromElement(
            Map<Integer, Cell> cellMap,
            TableTableCellElementBase cellElement,
            int rowIndex,
            int columnIndex,
            Boolean autoTrim,
            Boolean autoStrip) {
        ReadCellData<String> readCellData = new ReadCellData<>();
        readCellData.setRowIndex(rowIndex);
        readCellData.setColumnIndex(columnIndex);

        String cellValue = getCellValueFromElement(cellElement);
        String valueType = cellElement.getOfficeValueTypeAttribute();

        if (StringUtils.isNotBlank(cellValue)) {
            readCellData.setType(determineCellTypeFromElement(valueType));
            if (autoStrip) {
                readCellData.setStringValue(StringUtils.strip(cellValue));
            } else if (autoTrim) {
                readCellData.setStringValue(cellValue.trim());
            } else {
                readCellData.setStringValue(cellValue);
            }

            // Handle numeric values
            if (readCellData.getType() == CellDataTypeEnum.NUMBER) {
                try {
                    Double numericValue = cellElement.getOfficeValueAttribute();
                    if (numericValue != null) {
                        readCellData.setNumberValue(new java.math.BigDecimal(numericValue.toString()));
                    }
                } catch (Exception e) {
                    // Keep as string if parsing fails
                    readCellData.setType(CellDataTypeEnum.STRING);
                }
            }

            // Handle boolean values
            if (readCellData.getType() == CellDataTypeEnum.BOOLEAN) {
                try {
                    Boolean boolValue = cellElement.getOfficeBooleanValueAttribute();
                    if (boolValue != null) {
                        readCellData.setBooleanValue(boolValue);
                    }
                } catch (Exception e) {
                    readCellData.setType(CellDataTypeEnum.STRING);
                }
            }

            cellMap.put(columnIndex, readCellData);
        } else {
            readCellData.setType(CellDataTypeEnum.EMPTY);
            // Don't add empty cells to the map to save memory
        }
    }

    /**
     * Get cell value directly from DOM element.
     */
    private String getCellValueFromElement(TableTableCellElementBase cellElement) {
        if (cellElement == null) {
            return null;
        }

        String valueType = cellElement.getOfficeValueTypeAttribute();
        if (valueType == null) {
            // Try to get text content
            return cellElement.getTextContent();
        }

        switch (valueType) {
            case "float":
            case "currency":
            case "percentage":
                Double doubleValue = cellElement.getOfficeValueAttribute();
                if (doubleValue != null) {
                    // Remove trailing zeros for display
                    if (doubleValue == Math.floor(doubleValue) && !Double.isInfinite(doubleValue)) {
                        return String.valueOf(doubleValue.longValue());
                    }
                    return doubleValue.toString();
                }
                return cellElement.getTextContent();
            case "date":
            case "time":
                return cellElement.getTextContent();
            case "boolean":
                Boolean boolValue = cellElement.getOfficeBooleanValueAttribute();
                return boolValue != null ? boolValue.toString() : cellElement.getTextContent();
            case "string":
            default:
                String stringValue = cellElement.getOfficeStringValueAttribute();
                if (stringValue != null) {
                    return stringValue;
                }
                return cellElement.getTextContent();
        }
    }

    /**
     * Determine cell type from value type string.
     */
    private CellDataTypeEnum determineCellTypeFromElement(String valueType) {
        if (valueType == null) {
            return CellDataTypeEnum.STRING;
        }

        switch (valueType) {
            case "float":
            case "currency":
            case "percentage":
                return CellDataTypeEnum.NUMBER;
            case "date":
            case "time":
                return CellDataTypeEnum.STRING;
            case "boolean":
                return CellDataTypeEnum.BOOLEAN;
            case "string":
            default:
                return CellDataTypeEnum.STRING;
        }
    }

    /**
     * Get the string value from an ODF cell.
     *
     * @param cell The ODF table cell
     * @return The cell value as a string
     */
    private String getCellValue(OdfTableCell cell) {
        if (cell == null) {
            return null;
        }

        String valueType = cell.getValueType();
        if (valueType == null) {
            return cell.getDisplayText();
        }

        switch (valueType) {
            case "float":
            case "currency":
            case "percentage":
                Double doubleValue = cell.getDoubleValue();
                if (doubleValue != null) {
                    // Remove trailing zeros for display
                    if (doubleValue == Math.floor(doubleValue) && !Double.isInfinite(doubleValue)) {
                        return String.valueOf(doubleValue.longValue());
                    }
                    return doubleValue.toString();
                }
                return null;
            case "date":
            case "time":
                return cell.getDisplayText();
            case "boolean":
                Boolean boolValue = cell.getBooleanValue();
                return boolValue != null ? boolValue.toString() : null;
            case "string":
            default:
                return cell.getStringValue();
        }
    }

    /**
     * Determine the cell data type based on ODF cell type.
     *
     * @param cell The ODF table cell
     * @return The corresponding CellDataTypeEnum
     */
    private CellDataTypeEnum determineCellType(OdfTableCell cell) {
        if (cell == null) {
            return CellDataTypeEnum.EMPTY;
        }

        String valueType = cell.getValueType();
        if (valueType == null) {
            return CellDataTypeEnum.STRING;
        }

        switch (valueType) {
            case "float":
            case "currency":
            case "percentage":
                return CellDataTypeEnum.NUMBER;
            case "date":
            case "time":
                return CellDataTypeEnum.STRING; // Dates are returned as formatted strings
            case "boolean":
                return CellDataTypeEnum.BOOLEAN;
            case "string":
            default:
                return CellDataTypeEnum.STRING;
        }
    }
}
