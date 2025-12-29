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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.cli.core.DocumentProcessor;
import org.apache.fesod.cli.exception.FileProcessException;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sheet document processor implementation
 */
public class SheetProcessor implements DocumentProcessor {

    private static final Logger log = LoggerFactory.getLogger(SheetProcessor.class);

    private final SheetReader reader;
    private final SheetWriter writer;
    private final SheetConverter converter;

    public SheetProcessor() {
        this.reader = new SheetReader();
        this.writer = new SheetWriter();
        this.converter = new SheetConverter();
    }

    @Override
    public Map<String, Object> read(Path inputPath, Map<String, Object> options) {
        log.info("Reading spreadsheet from: {}", inputPath);

        try {
            Integer sheetIndex = (Integer) options.get("sheetIndex");
            String sheetName = (String) options.get("sheetName");
            Boolean readAll = (Boolean) options.get("readAll");
            if (readAll == null) {
                readAll = false;
            }

            return reader.read(inputPath, sheetIndex, sheetName, readAll);

        } catch (Exception e) {
            throw new FileProcessException("Failed to read spreadsheet:  " + e.getMessage(), e);
        }
    }

    @Override
    public void write(Map<String, Object> data, Path outputPath, Map<String, Object> options) {
        log.info("Writing spreadsheet to: {}", outputPath);

        try {
            String sheetName = (String) options.get("sheetName");
            if (sheetName == null) {
                sheetName = "Sheet1";
            }
            writer.write(data, outputPath, sheetName, options);

        } catch (Exception e) {
            throw new FileProcessException("Failed to write spreadsheet: " + e.getMessage(), e);
        }
    }

    @Override
    public void convert(Path inputPath, Path outputPath, Map<String, Object> options) {
        log.info("Converting {} to {}", inputPath, outputPath);

        try {
            converter.convert(inputPath, outputPath, options);

        } catch (Exception e) {
            throw new FileProcessException("Failed to convert spreadsheet: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getInfo(Path inputPath) {
        log.info("Getting info for: {}", inputPath);

        try {
            Map<String, Object> info = new LinkedHashMap<String, Object>();
            List<ReadSheet> sheets;

            try (ExcelReader excelReader = FesodSheet.read(inputPath.toFile()).build()) {
                sheets = excelReader.excelExecutor().sheetList();
            }

            info.put("file", inputPath.toString());
            info.put("fileSize", inputPath.toFile().length());
            info.put("sheetCount", sheets.size());

            List<Map<String, Object>> sheetInfoList = new ArrayList<Map<String, Object>>();
            for (ReadSheet sheet : sheets) {
                Map<String, Object> sheetInfo = new LinkedHashMap<String, Object>();
                sheetInfo.put("index", sheet.getSheetNo());
                sheetInfo.put("name", sheet.getSheetName());
                sheetInfo.put("hidden", sheet.isHidden());
                sheetInfo.put("rowCount", sheet.getNumRows());
                sheetInfoList.add(sheetInfo);
            }

            info.put("sheets", sheetInfoList);
            return info;

        } catch (Exception e) {
            throw new FileProcessException("Failed to get spreadsheet info: " + e.getMessage(), e);
        }
    }

    @Override
    public String getModuleName() {
        return "sheet";
    }
}
