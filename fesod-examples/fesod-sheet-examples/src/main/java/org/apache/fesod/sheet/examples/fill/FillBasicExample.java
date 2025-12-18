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

package org.apache.fesod.sheet.examples.fill;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.fill.data.FillData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Basic example demonstrating how to fill data into an Excel template.
 */
public class FillBasicExample {

    public static void main(String[] args) {
        simpleFill();
    }

    /**
     * Simple fill using an object or a map.
     */
    public static void simpleFill() {
        String templateFileName = ExampleFileUtil.getExamplePath("templates" + File.separator + "simple.xlsx");

        // Option 1: Fill based on an object
        String fileName = ExampleFileUtil.getTempPath("simpleFill" + System.currentTimeMillis() + ".xlsx");
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(fillData);
        System.out.println("Successfully wrote file: " + fileName);

        // Option 2: Fill based on a Map
        fileName = ExampleFileUtil.getPath() + "simpleFillMap" + System.currentTimeMillis() + ".xlsx";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Zhang San");
        map.put("number", 5.2);
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(map);
        System.out.println("Successfully wrote file: " + fileName);
    }
}
