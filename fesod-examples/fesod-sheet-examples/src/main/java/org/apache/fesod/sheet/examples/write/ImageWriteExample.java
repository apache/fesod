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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.examples.write;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.ImageDemoData;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.util.FileUtils;

/**
 * Demonstrates exporting images to Excel cells using five different source types.
 *
 * <h2>Scenario</h2>
 * <p>You need to generate an Excel report containing images — for example, product photos
 * in a catalog, user avatars in a report, or QR codes in an inventory sheet.</p>
 *
 * <h2>Five Image Source Types</h2>
 * <pre>
 * Type          | Field Type          | Use Case
 * ──────────────|─────────────────────|──────────────────────────────────────────
 * File          | File                | Local file on disk
 * InputStream   | InputStream         | Classpath resource, servlet upload, etc.
 * String path   | String              | File path (requires StringImageConverter)
 * byte[]        | byte[]              | Pre-loaded binary data, generated images
 * URL           | URL                 | Remote image (downloaded at write time)
 * </pre>
 *
 * <h2>Advanced: Multiple Images per Cell ({@link WriteCellData})</h2>
 * <p>Using {@code WriteCellData<Void>} you can:</p>
 * <ul>
 *   <li>Add multiple images to a single cell</li>
 *   <li>Combine text with images in the same cell</li>
 *   <li>Control image positioning with margins (top, right, bottom, left)</li>
 *   <li>Span images across adjacent cells using {@code relativeLastColumnIndex}</li>
 * </ul>
 *
 * <h2>Memory Considerations</h2>
 * <p><b>Warning:</b> All images are loaded into memory during write. For large volumes:</p>
 * <ul>
 *   <li>Upload images to cloud storage (e.g., AWS S3) and reference by URL.</li>
 *   <li>Compress images before embedding.</li>
 *   <li>Consider writing fewer images per batch.</li>
 * </ul>
 *
 * <h2>Expected Output</h2>
 * <p>An Excel file with one row containing 6 columns, each showing the same image
 * loaded from a different source. The last column contains two images with text,
 * spanning into the adjacent cell.</p>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicWriteExample} — Simple data write.</li>
 *   <li>{@link StyleWriteExample} — Style customization.</li>
 * </ul>
 *
 * @see ImageDemoData
 * @see WriteCellData
 * @see ImageData
 */
@Slf4j
public class ImageWriteExample {

    public static void main(String[] args) throws Exception {
        imageWrite();
    }

    /**
     * Writes images to Excel using all five source types plus advanced multi-image positioning.
     *
     * <p>The method demonstrates:
     * <ol>
     *   <li>Setting up five different image sources (File, InputStream, String, byte[], URL).</li>
     *   <li>Creating a {@link WriteCellData} with text + two images at custom positions.</li>
     *   <li>Using {@code relativeLastColumnIndex} to span an image across cells.</li>
     * </ol>
     *
     * <p><b>Troubleshooting:</b> If image resources are inaccessible, XLSX format may error with
     * "SXSSFWorkbook - Failed to dispose sheet". In that case, try XLS format instead.</p>
     *
     * @throws Exception if file operations fail
     */
    public static void imageWrite() throws Exception {
        String fileName = ExampleFileUtil.getTempPath("imageWrite" + System.currentTimeMillis() + ".xlsx");

        // Note: All images will be loaded into memory. For large volumes, consider:
        // 1. Upload images to cloud storage (e.g., https://www.aliyun.com/product/oss) and use URLs
        // 2. Use image compression tools like: https://github.com/coobird/thumbnailator

        String imagePath = ExampleFileUtil.getPath() + "example/sample-data" + File.separator + "img.jpg";
        try (InputStream inputStream = FileUtils.openInputStream(new File(imagePath))) {
            List<ImageDemoData> list = new ArrayList<>();
            ImageDemoData imageDemoData = new ImageDemoData();
            list.add(imageDemoData);

            // Five types of image export - in practice, choose only one method
            imageDemoData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
            imageDemoData.setFile(new File(imagePath));
            imageDemoData.setString(imagePath);
            imageDemoData.setInputStream(inputStream);
            imageDemoData.setUrl(new URL("https://poi.apache.org/images/project-header.png"));

            // Advanced example demonstrating:
            // - Adding text to the cell in addition to images
            // - Adding 2 images to the same cell
            // - First image aligned to the left
            // - Second image aligned to the right and spanning into adjacent cells
            WriteCellData<Void> writeCellData = new WriteCellData<>();
            imageDemoData.setWriteCellDataFile(writeCellData);
            // Can be set to EMPTY if no additional data is needed
            writeCellData.setType(CellDataTypeEnum.STRING);
            writeCellData.setStringValue("Additional text content");

            // Can add multiple images to a single cell
            List<ImageData> imageDataList = new ArrayList<>();
            ImageData imageData = new ImageData();
            imageDataList.add(imageData);
            writeCellData.setImageDataList(imageDataList);
            // Set image as binary data
            imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
            // Set image type
            imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
            // Top, Right, Bottom, Left margins
            // Similar to CSS margin
            // Note: Setting values too large (exceeding cell size) may cause repair prompts when opening.
            // No perfect solution found yet.
            imageData.setTop(5);
            imageData.setRight(40);
            imageData.setBottom(5);
            imageData.setLeft(5);

            // Add second image
            imageData = new ImageData();
            imageDataList.add(imageData);
            writeCellData.setImageDataList(imageDataList);
            imageData.setImage(FileUtils.readFileToByteArray(new File(imagePath)));
            imageData.setImageType(ImageData.ImageType.PICTURE_TYPE_PNG);
            imageData.setTop(5);
            imageData.setRight(5);
            imageData.setBottom(5);
            imageData.setLeft(50);
            // Position the image to span from current cell to the cell on its right
            // Starting point is relative to current cell (0 - can be omitted)
            imageData.setRelativeFirstRowIndex(0);
            imageData.setRelativeFirstColumnIndex(0);
            imageData.setRelativeLastRowIndex(0);
            // The first 3 can be omitted. This one must be set - the ending position
            // needs to move one column to the right relative to the current cell
            // This means the image will cover the current cell and the next cell to its right
            imageData.setRelativeLastColumnIndex(1);

            // Write data
            FesodSheet.write(fileName, ImageDemoData.class).sheet().doWrite(list);
            log.info("Successfully wrote image file: {}", fileName);
            // If image resources are inaccessible, XLSX format may error: SXSSFWorkbook - Failed to dispose sheet
            // Consider declaring as XLS format in such cases:
            // FesodSheet.write(fileName, ImageDemoData.class).excelType(ExcelTypeEnum.XLS).sheet().doWrite(list);
        }
    }
}
