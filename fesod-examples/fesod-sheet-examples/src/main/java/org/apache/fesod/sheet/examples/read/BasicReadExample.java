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

package org.apache.fesod.sheet.examples.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.read.data.DemoData;
import org.apache.fesod.sheet.examples.read.listeners.DemoDataListener;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Basic example demonstrating how to read an Excel file.
 */
@Slf4j
public class BasicReadExample {

    public static void main(String[] args) {
        basicRead();
    }

    /**
     * Basic read using a listener.
     */
    public static void basicRead() {
        String fileName = ExampleFileUtil.getExamplePath("demo.xlsx");
        log.info("Reading file: {}", fileName);

        // Specify the class to read the data, then read the first sheet.
        FesodSheet.read(fileName, DemoData.class, new DemoDataListener())
                .sheet()
                .doRead();

        log.info("Successfully read file: {}", fileName);
    }
}
