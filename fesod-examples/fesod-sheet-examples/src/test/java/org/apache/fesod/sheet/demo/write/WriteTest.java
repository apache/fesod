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

package org.apache.fesod.sheet.demo.write;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.fesod.common.util.BooleanUtils;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.data.CommentData;
import org.apache.fesod.sheet.metadata.data.FormulaData;
import org.apache.fesod.sheet.metadata.data.HyperlinkData;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.apache.fesod.sheet.metadata.data.RichTextStringData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.EscapeHexCellWriteHandler;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.sheet.write.merge.LoopMergeStrategy;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.metadata.style.WriteFont;
import org.apache.fesod.sheet.write.style.HorizontalCellStyleStrategy;
import org.apache.fesod.sheet.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.junit.jupiter.api.Test;

/**
 * Common writing examples
 *
 *
 */
public class WriteTest {

    /**
     * Simple write
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Write directly
     */
    @Test
    public void simpleWrite() {
        // Note: simpleWrite can be used when the data volume is not large (within 5000, depending on the actual
        // situation). For large data volumes, refer to repeated writes.

        // Method 1 JDK8+
        // since: 3.0.0-beta1
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        // If you want to use Excel 03, pass the excelType parameter.
        FesodSheet.write(fileName, DemoData.class).sheet("Template").doWrite(() -> {
            // Paging query data
            return data();
        });

        // Method 2
        fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        // If you want to use Excel 03, pass the excelType parameter.
        FesodSheet.write(fileName, DemoData.class).sheet("Template").doWrite(data());

        // Method 3
        fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName, DemoData.class).build()) {
            WriteSheet writeSheet = FesodSheet.writerSheet("Template").build();
            excelWriter.write(data(), writeSheet);
        }
    }

    @Test
    public void testEscapeHex() {
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        FesodSheet.write(fileName, DemoData.class)
                .sheet("Template")
                .registerWriteHandler(new EscapeHexCellWriteHandler())
                .doWrite(() -> {
                    return dataHex();
                });
    }

    /**
     * Export only specified columns based on parameters
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Include or exclude columns as needed
     * <p>
     * 3. Write directly
     */
    @Test
    public void excludeOrIncludeWrite() {
        String fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // Note: When using the ExcelProperty annotation, if you want to avoid empty columns, you need to use the
        // 'order' field instead of 'index'. 'order' will ignore empty columns and continue sequentially, while 'index'
        // will not ignore empty columns (it stays in the specified column).

        // Based on user input fields, assuming we want to ignore 'date'
        Set<String> excludeColumnFieldNames = new HashSet<>();
        excludeColumnFieldNames.add("date");
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoData.class)
                .excludeColumnFieldNames(excludeColumnFieldNames)
                .sheet("Template")
                .doWrite(data());

        fileName = TestFileUtil.getPath() + "excludeOrIncludeWrite" + System.currentTimeMillis() + ".xlsx";
        // Based on user input fields, assuming we only want to export 'date'
        Set<String> includeColumnFieldNames = new HashSet<>();
        includeColumnFieldNames.add("date");
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoData.class)
                .includeColumnFieldNames(includeColumnFieldNames)
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Specify columns to write
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link IndexData}
     * <p>
     * 2. Use {@link ExcelProperty} annotation to specify columns to write
     * <p>
     * 3. Write directly
     */
    @Test
    public void indexWrite() {
        String fileName = TestFileUtil.getPath() + "indexWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, IndexData.class).sheet("Template").doWrite(data());
    }

    /**
     * Complex header writing
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link ComplexHeadData}
     * <p>
     * 2. Use {@link ExcelProperty} annotation to specify complex headers
     * <p>
     * 3. Write directly
     */
    @Test
    public void complexHeadWrite() {
        String fileName = TestFileUtil.getPath() + "complexHeadWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, ComplexHeadData.class).sheet("Template").doWrite(data());
    }

    /**
     * Repeated writes
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link ComplexHeadData}
     * <p>
     * 2. Use {@link ExcelProperty} annotation to specify complex headers
     * <p>
     * 3. Call write multiple times
     */
    @Test
    public void repeatedWrite() {
        // Method 1: Writing to the same sheet
        String fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName, DemoData.class).build()) {
            // Note: Create the writeSheet only once if writing to the same sheet
            WriteSheet writeSheet = FesodSheet.writerSheet("Template").build();
            // Call write. Here I called it five times. In actual use, loop based on the total number of pages in the
            // database query.
            for (int i = 0; i < 5; i++) {
                // Paging query data from the database. Here you can query the data for each page.
                List<DemoData> data = data();
                excelWriter.write(data, writeSheet);
            }
        }

        // Method 2: Writing to different sheets with the same object
        fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify file
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName, DemoData.class).build()) {
            // Call write. Here I called it five times. In actual use, loop based on the total number of pages in the
            // database query. Eventually it will be written to 5 sheets.
            for (int i = 0; i < 5; i++) {
                // Create writeSheet every time. Note that sheetNo must be specified and sheetName must be different.
                WriteSheet writeSheet =
                        FesodSheet.writerSheet(i, "Template" + i).build();
                // Paging query data from the database. Here you can query the data for each page.
                List<DemoData> data = data();
                excelWriter.write(data, writeSheet);
            }
        }

        // Method 3: Writing to different sheets with different objects
        fileName = TestFileUtil.getPath() + "repeatedWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify file
        try (ExcelWriter excelWriter = FesodSheet.write(fileName).build()) {
            // Call write. Here I called it five times. In actual use, loop based on the total number of pages in the
            // database query. Eventually it will be written to 5 sheets.
            for (int i = 0; i < 5; i++) {
                // Create writeSheet every time. Note that sheetNo must be specified and sheetName must be different.
                // Note that DemoData.class can change each time; I used the same class here for convenience.
                // In reality, it can change every time.
                WriteSheet writeSheet = FesodSheet.writerSheet(i, "Template" + i)
                        .head(DemoData.class)
                        .build();
                // Paging query data from the database. Here you can query the data for each page.
                List<DemoData> data = data();
                excelWriter.write(data, writeSheet);
            }
        }
    }

    /**
     * Date, number, or custom format conversion
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link ConverterData}
     * <p>
     * 2. Use {@link ExcelProperty} with annotations {@link DateTimeFormat}, {@link NumberFormat}, or custom annotations
     * <p>
     * 3. Write directly
     */
    @Test
    public void converterWrite() {
        String fileName = TestFileUtil.getPath() + "converterWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, ConverterData.class).sheet("Template").doWrite(data());
    }

    /**
     * Image export
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link ImageDemoData}
     * <p>
     * 2. Write directly
     */
    @Test
    public void imageWrite() throws Exception {
        String fileName = TestFileUtil.getPath() + "imageWrite" + System.currentTimeMillis() + ".xlsx";

        // Note: All images will be loaded into memory. There is no good solution for now. For large numbers of images,
        // it is recommended to:
        // 1. Upload images to OSS or other storage sites: https://www.aliyun.com/product/oss, then verify the link
        // directly
        // 2. Use: https://github.com/coobird/thumbnailator or other tools to compress images

        String imagePath = TestFileUtil.getPath() + "converter" + File.separator + "img.jpg";
        try (InputStream inputStream = FileUtils.openInputStream(new File(imagePath))) {
            List<ImageDemoData> list = ListUtils.newArrayList();
            ImageDemoData imageDemoData = new ImageDemoData();
            list.add(imageDemoData);
            // Put five types of images. In actual use, just choose one.
            imageDemoData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
            imageDemoData.setFile(new File(imagePath));
            imageDemoData.setString(imagePath);
            imageDemoData.setInputStream(inputStream);
            imageDemoData.setUrl(new URL("https://poi.apache.org/images/project-header.png"));

            // Demonstration
            // Need to add extra text
            // And need to add 2 images
            // The first image is on the left
            // The second is on the right and occupies the cell behind it
            WriteCellData<Void> writeCellData = new WriteCellData<>();
            imageDemoData.setWriteCellDataFile(writeCellData);
            // Set to EMPTY to indicate no other data is needed
            writeCellData.setType(CellDataTypeEnum.STRING);
            writeCellData.setStringValue("Extra text");

            // Can put multiple images
            List<ImageData> imageDataList = new ArrayList<>();
            ImageData imageData = new ImageData();
            imageDataList.add(imageData);
            writeCellData.setImageDataList(imageDataList);
            // Put binary image
            imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
            // Image type
            imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
            // Top, Right, Bottom, Left need padding
            // Similar to CSS margin
            // Tested: cannot set too large. If it exceeds the original cell size, opening it will prompt repair. No
            // good solution found yet.
            imageData.setTop(5);
            imageData.setRight(40);
            imageData.setBottom(5);
            imageData.setLeft(5);

            // Put second image
            imageData = new ImageData();
            imageDataList.add(imageData);
            writeCellData.setImageDataList(imageDataList);
            imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
            imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
            imageData.setTop(5);
            imageData.setRight(5);
            imageData.setBottom(5);
            imageData.setLeft(50);
            // Set image position. Assume the target is to cover the current cell and the cell to the right.
            // Start point relative to current cell is 0. Can be omitted.
            imageData.setRelativeFirstRowIndex(0);
            imageData.setRelativeFirstColumnIndex(0);
            imageData.setRelativeLastRowIndex(0);
            // First 3 can be omitted. The following one needs to be written, meaning the end needs to move one cell to
            // the right relative to the current cell.
            // This image will cover the current cell and the next one.
            imageData.setRelativeLastColumnIndex(1);

            // Write data
            FesodSheet.write(fileName, ImageDemoData.class).sheet().doWrite(list);
            // If image resource is inaccessible, XLSX format will error: SXSSFWorkbook - Failed to dispose sheet
            // Can consider declaring as XLS format
            // FesodSheet.write(fileName, ImageDemoData.class).excelType(ExcelTypeEnum.XLS).sheet().doWrite(list);
        }
    }

    /**
     * Hyperlinks, comments, formulas, single cell styling, multiple styles in a single cell
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link WriteCellDemoData}
     * <p>
     * 2. Write directly
     */
    @Test
    public void writeCellDataWrite() {
        String fileName = TestFileUtil.getPath() + "writeCellDataWrite" + System.currentTimeMillis() + ".xlsx";
        WriteCellDemoData writeCellDemoData = new WriteCellDemoData();

        // Set hyperlink
        WriteCellData<String> hyperlink = new WriteCellData<>("Official Website");
        writeCellDemoData.setHyperlink(hyperlink);
        HyperlinkData hyperlinkData = new HyperlinkData();
        hyperlink.setHyperlinkData(hyperlinkData);
        hyperlinkData.setAddress("https://github.com/fast-excel/fastexcel");
        hyperlinkData.setHyperlinkType(HyperlinkData.HyperlinkType.URL);

        // Set comment
        WriteCellData<String> comment = new WriteCellData<>("Comment cell info");
        writeCellDemoData.setCommentData(comment);
        CommentData commentData = new CommentData();
        comment.setCommentData(commentData);
        commentData.setAuthor("Jiaju Zhuang");
        commentData.setRichTextStringData(new RichTextStringData("This is a comment"));
        // The default size of the comment is the size of the cell. Here we want to adjust it to the size of 4 cells, so
        // we occupy one extra cell to the right and one extra cell down.
        commentData.setRelativeLastColumnIndex(1);
        commentData.setRelativeLastRowIndex(1);

        // Set formula
        WriteCellData<String> formula = new WriteCellData<>();
        writeCellDemoData.setFormulaData(formula);
        FormulaData formulaData = new FormulaData();
        formula.setFormulaData(formulaData);
        // Replace the first digit in 123456789 with 2
        // This is just an example. If it involves formulas, try to calculate them in memory if possible. Avoid using
        // formulas if possible.
        formulaData.setFormulaValue("REPLACE(123456789,1,1,2)");

        // Set style for a single cell. If there are many styles, you can use annotations.
        WriteCellData<String> writeCellStyle = new WriteCellData<>("Cell Style");
        writeCellStyle.setType(CellDataTypeEnum.STRING);
        writeCellDemoData.setWriteCellStyle(writeCellStyle);
        WriteCellStyle writeCellStyleData = new WriteCellStyle();
        writeCellStyle.setWriteCellStyle(writeCellStyleData);
        // Need to specify FillPatternType as FillPatternType.SOLID_FOREGROUND, otherwise background color will not be
        // displayed.
        writeCellStyleData.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // Green background
        writeCellStyleData.setFillForegroundColor(IndexedColors.GREEN.getIndex());

        // Set multiple styles in a single cell
        // Need to set inMemory=true, otherwise multiple styles in a single cell cannot be displayed. Use with caution.
        WriteCellData<String> richTest = new WriteCellData<>();
        richTest.setType(CellDataTypeEnum.RICH_TEXT_STRING);
        writeCellDemoData.setRichText(richTest);
        RichTextStringData richTextStringData = new RichTextStringData();
        richTest.setRichTextStringDataValue(richTextStringData);
        richTextStringData.setTextString("Red Green Default");
        // First 3 characters are red
        WriteFont writeFont = new WriteFont();
        writeFont.setColor(IndexedColors.RED.getIndex());
        richTextStringData.applyFont(0, 3, writeFont);
        // Next 5 characters are green
        writeFont = new WriteFont();
        writeFont.setColor(IndexedColors.GREEN.getIndex());
        richTextStringData.applyFont(4, 9, writeFont);

        List<WriteCellDemoData> data = new ArrayList<>();
        data.add(writeCellDemoData);
        FesodSheet.write(fileName, WriteCellDemoData.class)
                .inMemory(true)
                .sheet("Template")
                .doWrite(data);
    }

    /**
     * Write according to template
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link IndexData}
     * <p>
     * 2. Use {@link ExcelProperty} annotation to specify columns to write
     * <p>
     * 3. Use withTemplate to read template
     * <p>
     * 4. Write directly
     */
    @Test
    public void templateWrite() {
        String templateFileName = TestFileUtil.getPath() + "demo" + File.separator + "demo.xlsx";
        String fileName = TestFileUtil.getPath() + "templateWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        // Note: withTemplate will store the entire template file in memory, so try not to use it for appending files.
        // If the template file is too large, it will cause OOM.
        // If you want to append to a file (cannot be processed in one thread, refer to the repeated writing demo
        // recommended for one thread), it is recommended to store temporarily in the database or disk cache (ehcache)
        // and then write all at once.
        FesodSheet.write(fileName, DemoData.class)
                .withTemplate(templateFileName)
                .sheet()
                .doWrite(data());
    }

    /**
     * Column width and row height
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link WidthAndHeightData }
     * <p>
     * 2. Use annotations {@link ColumnWidth}, {@link HeadRowHeight}, {@link ContentRowHeight} to specify width or height
     * <p>
     * 3. Write directly
     */
    @Test
    public void widthAndHeightWrite() {
        String fileName = TestFileUtil.getPath() + "widthAndHeightWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, WidthAndHeightData.class).sheet("Template").doWrite(data());
    }

    /**
     * Custom style via annotations
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoStyleData}
     * <p>
     * 3. Write directly
     */
    @Test
    public void annotationStyleWrite() {
        String fileName = TestFileUtil.getPath() + "annotationStyleWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoStyleData.class).sheet("Template").doWrite(data());
    }

    /**
     * Custom style via handlers
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Create a style strategy and register it
     * <p>
     * 3. Write directly
     */
    @Test
    public void handlerStyleWrite() {
        // Method 1: Use existing strategies (Recommended)
        // HorizontalCellStyleStrategy: styles are the same for each row or alternating rows
        // AbstractVerticalCellStyleStrategy: styles are the same for each column. Need to subclass and implement.
        String fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
        // Head strategy
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // Background red
        headWriteCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 20);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // Content strategy
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // Need to specify FillPatternType as SOLID_FOREGROUND. The head defaults to FillPatternType so it can be
        // omitted.
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // Background green
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // Font size
        contentWriteFont.setFontHeightInPoints((short) 20);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // This strategy separates head style and content style. You can implement other strategies yourself.
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .sheet("Template")
                .doWrite(data());

        // Method 2: Write your own handler using Fesod API. Not recommended. Try to use existing strategies.
        fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
        FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(new CellWriteHandler() {
                    @Override
                    public void afterCellDispose(CellWriteHandlerContext context) {
                        // This event is called after data is set into the POI cell
                        // Check if it's not a head. If it's fill, this will be null, so use not true.
                        if (BooleanUtils.isNotTrue(context.getHead())) {
                            // First cell
                            // As long as it's not a head, there will be data. Of course in fill scenarios, use
                            // context.getCellDataList(). Depending on the template, a cell may have multiple
                            // WriteCellData.
                            WriteCellData<?> cellData = context.getFirstCellData();
                            // Need to get style from cellData
                            // A very important reason is that WriteCellStyle is bound to dataFormatData. For example,
                            // if you add DateTimeFormat,
                            // the dataFormatData in writeCellStyle has been changed. If you new a WriteCellStyle
                            // yourself, the annotation style may be lost.
                            // getOrCreateStyle returns a style, creating one if it's null.
                            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                            writeCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                            // Need to specify FillPatternType as SOLID_FOREGROUND
                            writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);

                            // The style is set. There is a FillStyleCellWriteHandler later that will default set
                            // WriteCellStyle to the cell, so you don't need to worry about it.
                        }
                    }
                })
                .sheet("Template")
                .doWrite(data());

        // Method 3: Use POI styles directly. Not recommended.
        // Pitfall 1: Style contains dataformat for formatting data, so setting it yourself may cause formatting
        // annotations to fail.
        // Pitfall 2: Don't keep creating styles. Remember to cache them. Creating more than 60,000 will crash.
        fileName = TestFileUtil.getPath() + "handlerStyleWrite" + System.currentTimeMillis() + ".xlsx";
        FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(new CellWriteHandler() {
                    @Override
                    public void afterCellDispose(CellWriteHandlerContext context) {
                        // This event is called after data is set into the POI cell
                        // Check if it's not a head. If it's fill, this will be null, so use not true.
                        if (BooleanUtils.isNotTrue(context.getHead())) {
                            Cell cell = context.getCell();
                            // Get POI workbook
                            Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                            // Remember to cache reusable parts. A table can have at most 60,000 styles.
                            // Try to pass the same cellStyle for different cells
                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                            // Need to specify FillPatternType as SOLID_FOREGROUND
                            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cell.setCellStyle(cellStyle);

                            // Since dataformat is not specified here, the displayed data format may be incorrect.

                            // Clear the style of WriteCellData. Otherwise, FillStyleCellWriteHandler will override your
                            // settings.
                            context.getFirstCellData().setWriteCellStyle(null);
                        }
                    }
                })
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Merge cells
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData} {@link DemoMergeData}
     * <p>
     * 2. Create a merge strategy and register it
     * <p>
     * 3. Write directly
     */
    @Test
    public void mergeWrite() {
        // Method 1: Annotation
        String fileName = TestFileUtil.getPath() + "mergeWrite" + System.currentTimeMillis() + ".xlsx";
        // Add ContentLoopMerge annotation in DemoStyleData
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoMergeData.class).sheet("Template").doWrite(data());

        // Method 2: Custom merge strategy
        fileName = TestFileUtil.getPath() + "mergeWrite" + System.currentTimeMillis() + ".xlsx";
        // Merge every 2 rows. Set eachColumn to 3 (length of our data), so only the first column will merge. Other
        // merge strategies can be implemented.
        LoopMergeStrategy loopMergeStrategy = new LoopMergeStrategy(2, 0);
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(loopMergeStrategy)
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Write using table
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Then write to the table
     */
    @Test
    public void tableWrite() {
        String fileName = TestFileUtil.getPath() + "tableWrite" + System.currentTimeMillis() + ".xlsx";
        // Method 1: Writing multiple tables here. If there is only one, it can be done in one line.
        // Specify the class to use for writing
        try (ExcelWriter excelWriter =
                FesodSheet.write(fileName, DemoData.class).build()) {
            // Set sheet to not need head, otherwise it will output sheet head, looking like the first table has 2
            // heads.
            WriteSheet writeSheet =
                    FesodSheet.writerSheet("Template").needHead(Boolean.FALSE).build();
            // Must specify need head here. Table inherits sheet configuration. If sheet is configured not to need it,
            // table defaults to not needing it.
            WriteTable writeTable0 =
                    FesodSheet.writerTable(0).needHead(Boolean.TRUE).build();
            WriteTable writeTable1 =
                    FesodSheet.writerTable(1).needHead(Boolean.TRUE).build();
            // First write will create head
            excelWriter.write(data(), writeSheet, writeTable0);
            // Second write will also create head, writing data after the first one.
            excelWriter.write(data(), writeSheet, writeTable1);
        }
    }

    /**
     * Dynamic header writing
     * <p>
     * The idea is to first create a sheet with List<String> head format, writing only the head, then write data via table without writing head.
     *
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Then write to the table
     */
    @Test
    public void dynamicHeadWrite() {
        String fileName = TestFileUtil.getPath() + "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";
        FesodSheet.write(fileName)
                // Put dynamic head here
                .head(head())
                .sheet("Template")
                // Of course data can also be passed as List<List<String>>
                .doWrite(data());
    }

    /**
     * Auto column width (not very precise)
     * <p>
     * This is not very easy to use currently. For example, numbers will cause line breaks. And the length is not exactly consistent with actual length. Use with caution if precise column width is needed. You can also re-implement referencing {@link LongestMatchColumnWidthStyleStrategy}.
     * <p>
     * POI's built-in {@link SXSSFSheet#autoSizeColumn(int)} also doesn't support Chinese very well. No good algorithm found yet.
     *
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link LongestMatchColumnWidthData}
     * <p>
     * 2. Register strategy {@link LongestMatchColumnWidthStyleStrategy}
     * <p>
     * 3. Write directly
     */
    @Test
    public void longestMatchColumnWidthWrite() {
        String fileName =
                TestFileUtil.getPath() + "longestMatchColumnWidthWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, LongestMatchColumnWidthData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("Template")
                .doWrite(dataLong());
    }

    /**
     * Custom handlers for dropdowns, hyperlinks, etc. (Refer to this for operations that don't fit above points but need to manipulate cells)
     * <p>
     * Demo implements 2 points: 1. Hyperlink the head of the first row and first column to URL. 2. Add dropdown box for data in the first column, first and second rows, displaying "Test1", "Test2".
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Register handlers {@link CustomCellWriteHandler} {@link CustomSheetWriteHandler}
     * <p>
     * 2. Write directly
     */
    @Test
    public void customHandlerWrite() {
        String fileName = TestFileUtil.getPath() + "customHandlerWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, DemoData.class)
                .registerWriteHandler(new CustomSheetWriteHandler())
                .registerWriteHandler(new CustomCellWriteHandler())
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Insert comment
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DemoData}
     * <p>
     * 2. Register handler {@link CommentWriteHandler}
     * <p>
     * 2. Write directly
     */
    @Test
    public void commentWrite() {
        String fileName = TestFileUtil.getPath() + "commentWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        // Note that inMemory must be set to true to support comments. Currently there is no good way to handle comments
        // without being in memory.
        FesodSheet.write(fileName, DemoData.class)
                .inMemory(Boolean.TRUE)
                .registerWriteHandler(new CommentWriteHandler())
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Variable title handling (including title internationalization, etc.)
     * <p>
     * Simply put, use List<List<String>> for titles but still support annotations
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link ConverterData}
     * <p>
     * 2. Write directly
     */
    @Test
    public void variableTitleWrite() {
        // Method 1
        String fileName = TestFileUtil.getPath() + "variableTitleWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName, ConverterData.class)
                .head(variableTitleHead())
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Write without creating objects
     */
    @Test
    public void noModelWrite() {
        // Method 1
        String fileName = TestFileUtil.getPath() + "noModelWrite" + System.currentTimeMillis() + ".xlsx";
        // Specify the class to use for writing, then write to the first sheet with the name "Template". The file stream
        // will be automatically closed.
        FesodSheet.write(fileName).head(head()).sheet("Template").doWrite(dataList());
    }

    @Test
    public void sheetDisposeTest() {
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        FesodSheet.write(fileName, DemoData.class)
                .sheet("Template")
                .registerWriteHandler(new SheetWriteHandler() {
                    @Override
                    public void afterSheetDispose(SheetWriteHandlerContext context) {
                        Sheet sheet = context.getWriteSheetHolder().getSheet();
                        // Merge region cells
                        sheet.addMergedRegionUnsafe(new CellRangeAddress(1, 10, 2, 2));
                    }
                })
                .doWrite(this::data);
        System.out.println(fileName);
    }

    private List<LongestMatchColumnWidthData> dataLong() {
        List<LongestMatchColumnWidthData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            LongestMatchColumnWidthData data = new LongestMatchColumnWidthData();
            data.setString("Testing very long string Testing very long string Testing very long string" + i);
            data.setDate(new Date());
            data.setDoubleData(1000000000000.0);
            list.add(data);
        }
        return list;
    }

    private List<List<String>> variableTitleHead() {
        List<List<String>> list = ListUtils.newArrayList();
        List<String> head0 = ListUtils.newArrayList();
        head0.add("string" + System.currentTimeMillis());
        List<String> head1 = ListUtils.newArrayList();
        head1.add("number" + System.currentTimeMillis());
        List<String> head2 = ListUtils.newArrayList();
        head2.add("date" + System.currentTimeMillis());
        list.add(head0);
        list.add(head1);
        list.add(head2);
        return list;
    }

    private List<List<String>> head() {
        List<List<String>> list = ListUtils.newArrayList();
        List<String> head0 = ListUtils.newArrayList();
        head0.add("String" + System.currentTimeMillis());
        List<String> head1 = ListUtils.newArrayList();
        head1.add("Double" + System.currentTimeMillis());
        List<String> head2 = ListUtils.newArrayList();
        head2.add("Date" + System.currentTimeMillis());
        list.add(head0);
        list.add(head1);
        list.add(head2);
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            List<Object> data = ListUtils.newArrayList();
            data.add("String" + i);
            data.add(0.56);
            data.add(new Date());
            list.add(data);
        }
        return list;
    }

    private List<DemoData> data() {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("STRING" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }

    private List<DemoData> dataHex() {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("_xB9f0_");
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
