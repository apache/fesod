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

package org.apache.fesod.sheet.write.executor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Verifies that absolute cell coordinates of {@code 0} (row 1 / column A) are honored when writing
 * images, instead of falling back to the coordinates of the cell that carries the data.
 */
@Tag(Tags.WRITE)
class AbsoluteCellCoordinateWriteTest extends AbstractExcelTest {

    /** Row index of the cell carrying the images: one head row plus three data rows. */
    private static final int DATA_ROW_INDEX = 3;

    /** Column index of the cell carrying the images (column B). */
    private static final int DATA_COLUMN_INDEX = 1;

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void image_with_absolute_zero_coordinates_is_anchored_at_A1(ExcelFormat format) throws IOException {
        byte[] image = readImage();

        ImageData absoluteZero = new ImageData();
        absoluteZero.setImage(image);
        absoluteZero.setFirstRowIndex(0);
        absoluteZero.setFirstColumnIndex(0);
        absoluteZero.setLastRowIndex(0);
        absoluteZero.setLastColumnIndex(0);

        ImageData control = new ImageData();
        control.setImage(image);

        File file = writeImages(format, absoluteZero, control);

        List<ClientAnchor> anchors = readPictureAnchors(file);
        Assertions.assertEquals(2, anchors.size());
        assertAnchor(anchors.get(0), 0, 0, 1, 1);
        assertAnchor(anchors.get(1), DATA_ROW_INDEX, DATA_COLUMN_INDEX, DATA_ROW_INDEX + 1, DATA_COLUMN_INDEX + 1);
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void absolute_zero_takes_precedence_over_relative_coordinates(ExcelFormat format) throws IOException {
        ImageData imageData = new ImageData();
        imageData.setImage(readImage());
        imageData.setFirstRowIndex(0);
        imageData.setRelativeFirstRowIndex(1);
        imageData.setFirstColumnIndex(0);
        imageData.setRelativeFirstColumnIndex(1);
        imageData.setRelativeLastRowIndex(1);
        imageData.setRelativeLastColumnIndex(1);

        File file = writeImages(format, imageData);

        List<ClientAnchor> anchors = readPictureAnchors(file);
        Assertions.assertEquals(1, anchors.size());
        assertAnchor(anchors.get(0), 0, 0, DATA_ROW_INDEX + 2, DATA_COLUMN_INDEX + 2);
    }

    private File writeImages(ExcelFormat format, ImageData... images) throws IOException {
        WriteCellData<Void> cellData = new WriteCellData<>(CellDataTypeEnum.EMPTY);
        cellData.setImageDataList(new ArrayList<>(Arrays.asList(images)));

        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("row1", "value1"));
        data.add(Arrays.asList("row2", "value2"));
        data.add(Arrays.asList("row3", cellData));

        List<List<String>> head = Arrays.asList(Collections.singletonList("Name"), Collections.singletonList("Image"));

        File file = createTempFile("absolute-coordinate", format);
        FesodSheet.write(file)
                .excelType(format.toExcelTypeEnum())
                .head(head)
                .sheet()
                .doWrite(data);
        return file;
    }

    private static List<ClientAnchor> readPictureAnchors(File file) throws IOException {
        List<ClientAnchor> anchors = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Drawing<?> drawing = workbook.getSheetAt(0).getDrawingPatriarch();
            Assertions.assertNotNull(drawing, "the sheet should contain a drawing");
            for (Shape shape : drawing) {
                if (shape instanceof Picture) {
                    anchors.add(((Picture) shape).getClientAnchor());
                }
            }
        }
        return anchors;
    }

    private static void assertAnchor(ClientAnchor anchor, int row1, int col1, int row2, int col2) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(row1, anchor.getRow1(), "row1"),
                () -> Assertions.assertEquals(col1, anchor.getCol1(), "col1"),
                () -> Assertions.assertEquals(row2, anchor.getRow2(), "row2"),
                () -> Assertions.assertEquals(col2, anchor.getCol2(), "col2"));
    }

    private byte[] readImage() throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("images/fesod-logo-png.png")) {
            Assertions.assertNotNull(in, "test image resource is missing");
            return IOUtils.toByteArray(in);
        }
    }
}
