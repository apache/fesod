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

package org.apache.fesod.cli.core.sheet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.write.metadata.WriteSheet;

/**
 * Sheet format converter
 */
public class SheetConverter {

    public void convert(Path inputPath, Path outputPath, Map<String, Object> options) {
        Integer sheetIndex = (Integer) options.get("sheetIndex");
        String sheetName = (String) options.get("sheetName");
        Boolean convertAll = (Boolean) options.get("convertAll");

        // Default to convert all sheets if no specific sheet is specified
        if (convertAll == null && sheetIndex == null && sheetName == null) {
            convertAll = true;
        }

        if (convertAll != null && convertAll) {
            convertAllSheets(inputPath, outputPath);
        } else if (sheetIndex != null) {
            convertSingleSheetByIndex(inputPath, outputPath, sheetIndex);
        } else if (sheetName != null) {
            convertSingleSheetByName(inputPath, outputPath, sheetName);
        } else {
            // Fallback to first sheet only
            convertSingleSheetByIndex(inputPath, outputPath, 0);
        }
    }

    private void convertAllSheets(Path inputPath, Path outputPath) {
        try (ExcelReader reader =
                        FesodSheet.read(inputPath.toFile()).headRowNumber(0).build();
                ExcelWriter writer = FesodSheet.write(outputPath.toFile()).build()) {

            List<ReadSheet> sheets = reader.excelExecutor().sheetList();

            for (int i = 0; i < sheets.size(); i++) {
                ReadSheet readSheet = sheets.get(i);

                // Read data from this sheet
                List<Map<Integer, String>> result = FesodSheet.read(inputPath.toFile())
                        .headRowNumber(0)
                        .sheet(readSheet.getSheetNo())
                        .doReadSync();

                // Convert to list format
                List<List<String>> sheetData = convertToListData(result);

                // Write to output with same sheet name
                WriteSheet writeSheet =
                        FesodSheet.writerSheet(i, readSheet.getSheetName()).build();
                writer.write(sheetData, writeSheet);
            }
        }
    }

    private void convertSingleSheetByIndex(Path inputPath, Path outputPath, int sheetIndex) {
        List<Map<Integer, String>> result = FesodSheet.read(inputPath.toFile())
                .headRowNumber(0)
                .sheet(sheetIndex)
                .doReadSync();

        List<List<String>> sheetData = convertToListData(result);

        FesodSheet.write(outputPath.toFile()).sheet("Sheet1").doWrite(sheetData);
    }

    private void convertSingleSheetByName(Path inputPath, Path outputPath, String sheetName) {
        List<Map<Integer, String>> result = FesodSheet.read(inputPath.toFile())
                .headRowNumber(0)
                .sheet(sheetName)
                .doReadSync();

        List<List<String>> sheetData = convertToListData(result);

        FesodSheet.write(outputPath.toFile()).sheet(sheetName).doWrite(sheetData);
    }

    private List<List<String>> convertToListData(List<Map<Integer, String>> result) {
        List<List<String>> data = new ArrayList<>();
        for (Map<Integer, String> rowData : result) {
            List<String> row = new ArrayList<>();
            for (String value : rowData.values()) {
                row.add(value);
            }
            data.add(row);
        }
        return data;
    }
}
