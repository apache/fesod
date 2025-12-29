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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;

/**
 * Sheet writer implementation
 */
public class SheetWriter {

    public void write(Map<String, Object> data, Path outputPath, String sheetName, Map<String, Object> options) {
        Object dataObj = data.get("data");

        if (dataObj instanceof String) {
            dataObj = JSON.parse((String) dataObj);
        }

        List<List<String>> rows = new ArrayList<List<String>>();

        if (dataObj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) dataObj;

            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);

                if (item instanceof JSONObject) {
                    JSONObject jsonObj = (JSONObject) item;
                    List<String> row = new ArrayList<String>();
                    for (Object val : jsonObj.values()) {
                        row.add(val != null ? val.toString() : "");
                    }
                    rows.add(row);
                } else if (item instanceof List) {
                    List<?> list = (List<?>) item;
                    List<String> row = new ArrayList<String>();
                    for (Object val : list) {
                        row.add(val != null ? val.toString() : "");
                    }
                    rows.add(row);
                }
            }
        }

        FesodSheet.write(outputPath.toFile()).sheet(sheetName).doWrite(rows);
    }
}
