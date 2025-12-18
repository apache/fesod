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

package org.apache.fesod.sheet.examples.write;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoStyleData;

/**
 * Example demonstrating how to set styles when writing an Excel file.
 */
public class StyleWriteExample {

    public static void main(String[] args) {
        styleWrite();
    }

    /**
     * Write with styles.
     */
    public static void styleWrite() {
        String fileName = ExampleFileUtil.getTempPath("styleWrite" + System.currentTimeMillis() + ".xlsx");

        FesodSheet.write(fileName, DemoStyleData.class).sheet("Template").doWrite(data());
        System.out.println("Successfully wrote file: " + fileName);
    }

    private static List<DemoStyleData> data() {
        List<DemoStyleData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoStyleData data = new DemoStyleData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
