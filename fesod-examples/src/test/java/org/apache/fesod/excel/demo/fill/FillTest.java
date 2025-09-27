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

package org.apache.fesod.excel.demo.fill;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.enums.WriteDirectionEnum;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.MapUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.handler.SheetWriteHandler;
import org.apache.fesod.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.fill.FillConfig;
import org.apache.fesod.excel.write.metadata.fill.FillWrapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;

/**
 * Example of writing and filling data into Excel
 *
 * @since 2.1.1
 */
public class FillTest {
    /**
     * Simplest example of filling data
     *
     * @since 2.1.1
     */
    @Test
    public void simpleFill() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "simple.xlsx";

        // Option 1: Fill based on an object
        String fileName = TestFileUtil.getPath() + "simpleFill" + System.currentTimeMillis() + ".xlsx";
        // This will fill the first sheet, and the file stream will be automatically closed.
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FastExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(fillData);

        // Option 2: Fill based on a Map
        fileName = TestFileUtil.getPath() + "simpleFill" + System.currentTimeMillis() + ".xlsx";
        // This will fill the first sheet, and the file stream will be automatically closed.
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("name", "Zhang San");
        map.put("number", 5.2);
        FastExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(map);
    }

    /**
     * Example of filling a list
     *
     * @since 2.1.1
     */
    @Test
    public void listFill() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        // When filling a list, note that {.} in the template indicates a list.
        // If the object filling the list is a Map, it must contain all keys of the list, even if the data is null. Use
        // map.put(key, null).
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "list.xlsx";

        // Option 1: Load all data into memory at once and fill
        String fileName = TestFileUtil.getPath() + "listFill" + System.currentTimeMillis() + ".xlsx";
        // This will fill the first sheet, and the file stream will be automatically closed.
        FastExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(data());

        // Option 2: Fill in multiple passes, using file caching (saves memory)
        fileName = TestFileUtil.getPath() + "listFill" + System.currentTimeMillis() + ".xlsx";
        try (ExcelWriter excelWriter =
                FastExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            excelWriter.fill(data(), writeSheet);
            excelWriter.fill(data(), writeSheet);
        }
    }

    /**
     * Example of complex filling
     *
     * @since 2.1.1
     */
    @Test
    public void complexFill() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        // {} represents a normal variable, {.} represents a list variable.
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "complex.xlsx";

        String fileName = TestFileUtil.getPath() + "complexFill" + System.currentTimeMillis() + ".xlsx";
        // Option 1
        try (ExcelWriter excelWriter =
                FastExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            // Note: The forceNewRow parameter is used here. When writing a list, it will always create a new row, and
            // the data below will be shifted down. Default is false, which will use the next row if available,
            // otherwise create a new one.
            // forceNewRow: If set to true, it will load all data into memory, so use it with caution.
            // In short, if your template has a list and the list is not the last row, and there is data below that
            // needs to be filled, you must set forceNewRow=true. However, this will consume a lot of memory.
            // For large datasets where the list is not the last row, refer to the next example.
            FillConfig fillConfig =
                    FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(data(), fillConfig, writeSheet);
            excelWriter.fill(data(), fillConfig, writeSheet);
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("date", "2019-10-09 13:28:28");
            map.put("total", 1000);
            excelWriter.fill(map, writeSheet);
        }
    }

    /**
     * Example of complex filling with large datasets
     * <p>
     * The solution here is to ensure that the list in the template is the last row, and then append a table. For Excel 2003, there is no solution other than increasing memory.
     *
     * @since 2.1.1
     */
    @Test
    public void complexFillWithTable() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        // {} represents a normal variable, {.} represents a list variable.
        // Here, the template deletes the data after the list, i.e., the summary row.
        String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator
                + "complexFillWithTable.xlsx";

        String fileName = TestFileUtil.getPath() + "complexFillWithTable" + System.currentTimeMillis() + ".xlsx";

        // Option 1
        try (ExcelWriter excelWriter =
                FastExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            // Directly write data
            excelWriter.fill(data(), writeSheet);
            excelWriter.fill(data(), writeSheet);

            // Write data before the list
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", "2019-10-09 13:28:28");
            excelWriter.fill(map, writeSheet);

            // There is a summary after the list, which needs to be written manually.
            // Here, we use a list for simplicity. You can also use an object.
            List<List<String>> totalListList = ListUtils.newArrayList();
            List<String> totalList = ListUtils.newArrayList();
            totalListList.add(totalList);
            totalList.add(null);
            totalList.add(null);
            totalList.add(null);
            // Fourth column
            totalList.add("Total:1000");
            // Note: Use write here, not fill.
            excelWriter.write(totalListList, writeSheet);
            // Overall, the writing is complex, but there is no better solution. Asynchronous writing to Excel does not
            // support row deletion or movement, nor does it support writing comments, so this approach is used.
            // The idea is to create a new sheet and copy data bit by bit. However, when adding rows to the list, the
            // data in the columns below cannot be shifted. A better solution will be explored in the future.
        }
    }

    /**
     * Example of horizontal filling
     *
     * @since 2.1.1
     */
    @Test
    public void horizontalFill() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        // {} represents a normal variable, {.} represents a list variable.
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "horizontal.xlsx";

        String fileName = TestFileUtil.getPath() + "horizontalFill" + System.currentTimeMillis() + ".xlsx";
        // Option 1
        try (ExcelWriter excelWriter =
                FastExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder()
                    .direction(WriteDirectionEnum.HORIZONTAL)
                    .build();
            excelWriter.fill(data(), fillConfig, writeSheet);
            excelWriter.fill(data(), fillConfig, writeSheet);

            Map<String, Object> map = new HashMap<>();
            map.put("date", "2019-10-09 13:28:28");
            excelWriter.fill(map, writeSheet);
        }
    }

    /**
     * Example of composite filling with multiple lists
     *
     * @since 2.2.0-beta1
     */
    @Test
    public void compositeFill() {
        // Template note: Use {} to indicate variables. If there are existing "{", "}" characters, use "\{", "\}"
        // instead.
        // {} represents a normal variable, {.} represents a list variable, {prefix.} prefix can distinguish different
        // lists.
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "composite.xlsx";

        String fileName = TestFileUtil.getPath() + "compositeFill" + System.currentTimeMillis() + ".xlsx";

        // Option 1
        try (ExcelWriter excelWriter =
                FastExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = FastExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder()
                    .direction(WriteDirectionEnum.HORIZONTAL)
                    .build();
            // If there are multiple lists, the template must have {prefix.}. Here, the prefix is data1, and multiple
            // lists must be wrapped with FillWrapper.
            excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
            excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("date", new Date());

            excelWriter.fill(map, writeSheet);
        }
    }

    /**
     * Example of filling an Excel template with date formatting.
     * <p>
     * This method demonstrates how to fill an Excel template where date fields
     * are already formatted in the template. The written data will automatically
     * follow the predefined date format in the template.
     */
    @Test
    public void dateFormatFill() {
        // Define the path to the template file.
        // The template should have predefined date formatting.
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "dateFormat.xlsx";

        // Generate a new output file name with a timestamp to avoid overwriting.
        String fileName = TestFileUtil.getPath() + "dateFormatFill" + System.currentTimeMillis() + ".xlsx";

        // Fill the template with data.
        // The dates in the data will be formatted according to the template's settings.
        FastExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(data());
    }

    @Test
    public void testFillSheetDispose() {
        String templateFileName =
                TestFileUtil.getPath() + "demo" + File.separator + "fill" + File.separator + "simple.xlsx";
        String fileName = TestFileUtil.getPath() + "simpleFill" + System.currentTimeMillis() + ".xlsx";
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FastExcel.write(fileName)
                .withTemplate(templateFileName)
                .sheet()
                .registerWriteHandler(new SheetWriteHandler() {
                    @Override
                    public void afterSheetDispose(SheetWriteHandlerContext context) {
                        Sheet sheet = context.getWriteSheetHolder().getSheet();
                        sheet.addMergedRegionUnsafe(new CellRangeAddress(1, 2, 0, 1));
                    }
                })
                .doFill(fillData);
    }

    private List<FillData> data() {
        List<FillData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            FillData fillData = new FillData();
            list.add(fillData);
            fillData.setName("Zhang San");
            fillData.setNumber(5.2);
            fillData.setDate(new Date());
        }
        return list;
    }
}
