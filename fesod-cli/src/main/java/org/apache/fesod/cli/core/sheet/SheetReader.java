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

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;

/**
 * Sheet reader implementation
 */
public class SheetReader {

    public Map<String, Object> read(Path inputPath, Integer sheetIndex, String sheetName, Boolean readAll) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        if (readAll) {
            result.put("sheets", readAllSheets(inputPath));
        } else if (sheetName != null) {
            result.put("data", readSheetByName(inputPath, sheetName));
        } else {
            int index = sheetIndex != null ? sheetIndex : 0;
            result.put("data", readSheetByIndex(inputPath, index));
        }

        return result;
    }

    private List<Map<String, Object>> readAllSheets(Path inputPath) {
        List<Map<String, Object>> allSheets = new ArrayList<Map<String, Object>>();
        List<ReadSheet> sheets;

        try (ExcelReader excelReader = FesodSheet.read(inputPath.toFile()).build()) {
            sheets = excelReader.excelExecutor().sheetList();
        }

        for (ReadSheet sheet : sheets) {
            Map<String, Object> sheetData = new LinkedHashMap<String, Object>();
            sheetData.put("name", sheet.getSheetName());
            sheetData.put("index", sheet.getSheetNo());
            sheetData.put("rows", readSheetByIndex(inputPath, sheet.getSheetNo()));
            allSheets.add(sheetData);
        }

        return allSheets;
    }

    private JSONArray readSheetByIndex(Path inputPath, int sheetIndex) {
        List<Map<Integer, String>> result = FesodSheet.read(inputPath.toFile())
                .headRowNumber(0)
                .sheet(sheetIndex)
                .doReadSync();
        return convertToJsonArray(result);
    }

    private JSONArray readSheetByName(Path inputPath, String sheetName) {
        List<Map<Integer, String>> result = FesodSheet.read(inputPath.toFile())
                .headRowNumber(0)
                .sheet(sheetName)
                .doReadSync();
        return convertToJsonArray(result);
    }

    private JSONArray convertToJsonArray(List<Map<Integer, String>> data) {
        JSONArray jsonArray = new JSONArray();
        for (Map<Integer, String> rowData : data) {
            JSONObject row = new JSONObject(new LinkedHashMap<String, Object>());
            for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
                row.put("col_" + entry.getKey(), entry.getValue());
            }
            jsonArray.add(row);
        }
        return jsonArray;
    }
}
