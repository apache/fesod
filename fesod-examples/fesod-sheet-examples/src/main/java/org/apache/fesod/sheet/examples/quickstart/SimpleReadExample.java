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

package org.apache.fesod.sheet.examples.quickstart;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.quickstart.data.DemoData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.listener.PageReadListener;

/**
 * Simplest way to read an Excel file.
 */
@Slf4j
public class SimpleReadExample {

    public static void main(String[] args) {
        simpleRead();
    }

    /**
     * Simplest way to read
     * <p>
     * 1. Create an entity class corresponding to the Excel data structure. Refer to {@link DemoData}.
     * <p>
     * 2. Since Fesod reads Excel files row by row, you need to create a callback listener for each row.
     * <p>
     * 3. Directly read the file.
     */
    public static void simpleRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file: {}", fileName);

        // Specify the class to read the data, then read the first sheet.
        FesodSheet.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
                    for (DemoData demoData : dataList) {
                        log.info("Read a row of data: {}", JSON.toJSONString(demoData));
                    }
                }))
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
