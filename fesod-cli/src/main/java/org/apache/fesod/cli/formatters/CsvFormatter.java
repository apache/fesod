/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.   You may obtain a copy of the License at
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
package org.apache.fesod.cli.formatters;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * CSV formatter implementation
 */
public class CsvFormatter implements OutputFormatter {
    
    @Override
    public String format(Map<String, Object> data) {
        try {
            StringWriter out = new StringWriter();
            CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT);
            
            Object dataObj = data.get("data");
            
            if (dataObj instanceof JSONArray) {
                JSONArray array = (JSONArray) dataObj;
                
                for (int i = 0; i < array.size(); i++) {
                    Object item = array.get(i);
                    
                    if (item instanceof JSONObject) {
                        JSONObject obj = (JSONObject) item;
                        printer.printRecord(obj.values());
                    }
                }
            }
            
            printer.close();
            return out.toString();
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to format as CSV: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void writeToFile(String content, Path outputPath) {
        try {
            Files.write(outputPath, content.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV to file: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getFormatType() {
        return "csv";
    }
}

