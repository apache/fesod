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

package org.apache.fesod.excel.analysis.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.fesod.excel.analysis.ExcelReadExecutor;
import org.apache.fesod.excel.context.csv.CsvReadContext;
import org.apache.fesod.excel.enums.ByteOrderMarkEnum;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.exception.ExcelAnalysisStopSheetException;
import org.apache.fesod.excel.metadata.Cell;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.csv.CsvReadWorkbookHolder;
import org.apache.fesod.excel.util.SheetUtils;
import org.apache.fesod.excel.util.StringUtils;

/**
 * CSV Excel Read Executor, responsible for reading and processing CSV files.
 */
@Slf4j
public class CsvExcelReadExecutor implements ExcelReadExecutor {

    // List of sheets to be read
    private final List<ReadSheet> sheetList;
    // Context for CSV reading operation
    private final CsvReadContext csvReadContext;

    public CsvExcelReadExecutor(CsvReadContext csvReadContext) {
        this.csvReadContext = csvReadContext;
        sheetList = new ArrayList<>();
        ReadSheet readSheet = new ReadSheet();
        sheetList.add(readSheet);
        readSheet.setSheetNo(0);
    }

    @Override
    public List<ReadSheet> sheetList() {
        return sheetList;
    }

    /**
     * Overrides the execute method to parse and process CSV files.
     * This method first attempts to create a CSV parser, then iterates through each sheet,
     * and processes each record in the CSV file.
     */
    @Override
    public void execute() {
        CSVParser csvParser;
        try {
            // Create a CSV parser instance
            csvParser = csvParser();
            // Store the CSV parser instance in the context for subsequent processing
            csvReadContext.csvReadWorkbookHolder().setCsvParser(csvParser);
        } catch (IOException e) {
            throw new ExcelAnalysisException(e);
        }
        // Iterate through each sheet in the sheet list
        for (ReadSheet readSheet : sheetList) {
            // Match and update the readSheet object
            readSheet = SheetUtils.match(readSheet, csvReadContext);
            // If the match result is null, skip the current sheet
            if (readSheet == null) {
                continue;
            }
            try {
                // Set the current sheet being processed in the context
                csvReadContext.currentSheet(readSheet);

                // Initialize the row index
                int rowIndex = 0;

                for (CSVRecord record : csvParser) {
                    // Process the current record, incrementing the row index after each processing
                    dealRecord(record, rowIndex++);
                }
            } catch (ExcelAnalysisStopSheetException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Custom stop!", e);
                }
            } catch (UncheckedIOException e) {
                // Apache Commons CSV may throw UncheckedIOException wrapping an IOException when the input
                // contains truncated quoted fields or reaches EOF unexpectedly. Treat such cases as benign
                // and end the current sheet gracefully; otherwise, rethrow as analysis exception.
                if (isBenignCsvParseException(e)) {
                    if (log.isDebugEnabled()) {
                        log.debug("CSV parse finished early due to benign parse error: {}", e.getMessage());
                    } else if (log.isWarnEnabled()) {
                        log.warn("CSV parse finished early due to benign parse error.");
                    }
                } else {
                    throw new ExcelAnalysisException(e);
                }
            }

            // The last sheet is read
            csvReadContext.analysisEventProcessor().endSheet(csvReadContext);
        }
    }

    /**
     * Initializes and returns a CSVParser instance based on the configuration provided in the CsvReadContext.
     * This method determines the appropriate input stream and character set to create the CSV parser.
     *
     * @return A CSVParser instance for parsing CSV files.
     * @throws IOException If an I/O error occurs while accessing the input stream or file.
     */
    private CSVParser csvParser() throws IOException {
        // Retrieve the CsvReadWorkbookHolder instance from the CsvReadContext.
        CsvReadWorkbookHolder csvReadWorkbookHolder = csvReadContext.csvReadWorkbookHolder();
        // Get the CSV format configuration from the CsvReadWorkbookHolder.
        CSVFormat csvFormat = csvReadWorkbookHolder.getCsvFormat();
        // Determine the ByteOrderMarkEnum based on the character set name.
        ByteOrderMarkEnum byteOrderMark = ByteOrderMarkEnum.valueOfByCharsetName(
                csvReadContext.csvReadWorkbookHolder().getCharset().name());

        // If the configuration mandates the use of an input stream, build the CSV parser using the input stream.
        if (csvReadWorkbookHolder.getMandatoryUseInputStream()) {
            return buildCsvParser(csvFormat, csvReadWorkbookHolder.getInputStream(), byteOrderMark);
        }

        // If a file is provided in the configuration, build the CSV parser using the file's input stream.
        if (csvReadWorkbookHolder.getFile() != null) {
            return buildCsvParser(
                    csvFormat,
                    Files.newInputStream(csvReadWorkbookHolder.getFile().toPath()),
                    byteOrderMark);
        }

        // As a fallback, build the CSV parser using the input stream.
        return buildCsvParser(csvFormat, csvReadWorkbookHolder.getInputStream(), byteOrderMark);
    }

    /**
     * Builds and returns a CSVParser instance based on the provided CSVFormat, InputStream, and ByteOrderMarkEnum.
     *
     * <p>
     * This method checks if the byteOrderMark is null. If it is null, it creates a CSVParser using the provided
     * input stream and charset. Otherwise, it wraps the input stream with a BOMInputStream to handle files with a
     * Byte Order Mark, ensuring proper decoding of the file content.
     * </p>
     *
     * @param csvFormat     The format configuration for parsing the CSV file.
     * @param inputStream   The input stream from which the CSV data will be read.
     * @param byteOrderMark The enumeration representing the Byte Order Mark (BOM) of the file's character set.
     * @return A CSVParser instance configured to parse the CSV data.
     * @throws IOException If an I/O error occurs while creating the parser or reading from the input stream.
     */
    private CSVParser buildCsvParser(CSVFormat csvFormat, InputStream inputStream, ByteOrderMarkEnum byteOrderMark)
            throws IOException {
        if (byteOrderMark == null) {
            return csvFormat.parse(new InputStreamReader(
                    inputStream, csvReadContext.csvReadWorkbookHolder().getCharset()));
        }
        return csvFormat.parse(new InputStreamReader(
                new BOMInputStream(inputStream, byteOrderMark.getByteOrderMark()),
                csvReadContext.csvReadWorkbookHolder().getCharset()));
    }

    /**
     * Processes a single CSV record and maps its content to a structured format for further analysis.
     *
     * @param record   The CSV record to be processed.
     * @param rowIndex The index of the current row being processed.
     *                 This method performs the following steps:
     *                 1. Initializes a `LinkedHashMap` to store cell data, ensuring the order of columns is preserved.
     *                 2. Iterates through each cell in the CSV record using an iterator.
     *                 3. For each cell, creates a `ReadCellData` object and sets its metadata (row index, column index, type, and value).
     *                 - If the cell is not blank, it is treated as a string and optionally trimmed based on the `autoTrim` configuration.
     *                 - If the cell is blank, it is marked as empty.
     *                 4. Adds the processed cell data to the `cellMap`.
     *                 5. Determines the row type: if the `cellMap` is empty, the row is marked as `EMPTY`; otherwise, it is marked as `DATA`.
     *                 6. Creates a `ReadRowHolder` object with the row's metadata and cell map, and stores it in the context.
     *                 7. Updates the context's sheet holder with the cell map and row index.
     *                 8. Notifies the analysis event processor that the row processing has ended.
     */
    private void dealRecord(CSVRecord record, int rowIndex) {
        Map<Integer, Cell> cellMap = new LinkedHashMap<>();
        Iterator<String> cellIterator = record.iterator();
        int columnIndex = 0;
        Boolean autoTrim =
                csvReadContext.csvReadWorkbookHolder().globalConfiguration().getAutoTrim();
        Boolean autoStrip =
                csvReadContext.csvReadWorkbookHolder().globalConfiguration().getAutoStrip();
        while (cellIterator.hasNext()) {
            String cellString = cellIterator.next();
            ReadCellData<String> readCellData = new ReadCellData<>();
            readCellData.setRowIndex(rowIndex);
            readCellData.setColumnIndex(columnIndex);

            // csv is an empty string of whether <code>,,</code> is read or <code>,"",</code>
            if (StringUtils.isNotBlank(cellString)) {
                readCellData.setType(CellDataTypeEnum.STRING);
                if (autoStrip) {
                    readCellData.setStringValue(StringUtils.strip(cellString));
                } else if (autoTrim) {
                    readCellData.setStringValue(cellString.trim());
                } else {
                    readCellData.setStringValue(cellString);
                }
            } else {
                readCellData.setType(CellDataTypeEnum.EMPTY);
            }
            cellMap.put(columnIndex++, readCellData);
        }

        RowTypeEnum rowType = MapUtils.isEmpty(cellMap) ? RowTypeEnum.EMPTY : RowTypeEnum.DATA;
        ReadRowHolder readRowHolder = new ReadRowHolder(
                rowIndex, rowType, csvReadContext.readWorkbookHolder().getGlobalConfiguration(), cellMap);
        csvReadContext.readRowHolder(readRowHolder);

        csvReadContext.csvReadSheetHolder().setCellMap(cellMap);
        csvReadContext.csvReadSheetHolder().setRowIndex(rowIndex);
        csvReadContext.analysisEventProcessor().endRow(csvReadContext);
    }

    /**
     * Determine whether an UncheckedIOException from Commons CSV is benign, i.e., caused by
     * truncated quoted fields or early EOF while parsing an encapsulated token. In such cases
     * we should stop reading the current sheet gracefully rather than failing the whole read.
     */
    private static boolean isBenignCsvParseException(Throwable t) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof IOException) {
                String msg = cur.getMessage();
                if (msg != null) {
                    // Messages observed from Apache Commons CSV
                    if (msg.contains("EOF reached before encapsulated token finished")
                            || msg.contains("encapsulated token finished")
                            || msg.contains("Unexpected EOF in quoted field")
                            || msg.contains("Unclosed quoted field")) {
                        return true;
                    }
                }
            }
            cur = cur.getCause();
        }
        return false;
    }
}
