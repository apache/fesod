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
 * Example demonstrating how to export images to an Excel file.
 *
 * <p>This example shows:
 * <ul>
 *   <li>Multiple ways to add images: File, InputStream, String path, byte[], URL</li>
 *   <li>Advanced image positioning with WriteCellData</li>
 *   <li>Adding multiple images to a single cell with custom positioning</li>
 *   <li>Combining images with text in the same cell</li>
 * </ul>
 *
 * <p><b>Important Notes:</b>
 * <ul>
 *   <li>All images will be loaded into memory. For large numbers of images, consider:
 *     <ol>
 *       <li>Uploading images to cloud storage (e.g., AWS S3, Aliyun OSS) and using URLs</li>
 *       <li>Using image compression tools like <a href="https://github.com/coobird/thumbnailator">Thumbnailator</a></li>
 *     </ol>
 *   </li>
 * </ul>
 */
@Slf4j
public class ImageWriteExample {

    public static void main(String[] args) throws Exception {
        imageWrite();
    }

    /**
     * Demonstrates how to write images to Excel in various formats.
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
