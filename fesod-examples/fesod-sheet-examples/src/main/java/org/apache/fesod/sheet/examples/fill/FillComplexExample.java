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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.fill.data.FillData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.write.metadata.WriteSheet;

/**
 * Complex example demonstrating how to fill a list of data into an Excel template.
 */
public class FillComplexExample {

    public static void main(String[] args) {
        listFill();
    }

    /**
     * Fill a list of data.
     */
    public static void listFill() {
        String templateFileName = ExampleFileUtil.getExamplePath("templates" + File.separator + "list.xlsx");

        // Option 1: Load all data into memory at once and fill
        String fileName = ExampleFileUtil.getTempPath("listFill" + System.currentTimeMillis() + ".xlsx");
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(data());
        System.out.println("Successfully wrote file: " + fileName);

        // Option 2: Fill in multiple passes, using file caching (saves memory)
        fileName = ExampleFileUtil.getPath() + "listFillMultiple" + System.currentTimeMillis() + ".xlsx";
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet().build();
            excelWriter.fill(data(), writeSheet);
            excelWriter.fill(data(), writeSheet);
        }
        System.out.println("Successfully wrote file: " + fileName);
    }

    private static List<FillData> data() {
        List<FillData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FillData fillData = new FillData();
            fillData.setName("Zhang San" + i);
            fillData.setNumber(5.2);
            fillData.setDate(new Date());
            list.add(fillData);
        }
        return list;
    }
}
