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

package org.apache.fesod.sheet.write.freezepane;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.FreezePane;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.style.SheetFreezePaneStrategy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.PaneInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag(Tags.WRITE)
class SheetFreezePaneTest {

    private File file07;
    private File file03;

    @BeforeEach
    void setup(@TempDir Path dir) {
        this.file07 = createTmpFile(dir, "writeFreezepane07.xlsx");
        this.file03 = createTmpFile(dir, "writeFreezepane03.xls");
    }

    private File createTmpFile(Path dir, String filename) {
        return new File(dir.resolve(filename).toString());
    }

    interface FreezePaneModel {

        void setStr1(String str1);

        String getStr1();

        void setStr2(String str2);

        String getStr2();
    }

    @Getter
    @Setter
    @FreezePane(colSplit = 2, rowSplit = 1)
    static class FreezeTwoColsOneRowData implements FreezePaneModel {
        @ExcelProperty("Str 1")
        private String str1;

        @ExcelProperty("Str 2")
        private String str2;
    }

    @Getter
    @Setter
    @FreezePane(rowSplit = 1)
    static class FreezeFirstRowOnlyData implements FreezePaneModel {
        @ExcelProperty("Str 1")
        private String str1;

        @ExcelProperty("Str 2")
        private String str2;
    }

    @Getter
    @Setter
    @FreezePane(colSplit = 1)
    static class FreezeFirstColOnlyData implements FreezePaneModel {
        @ExcelProperty("Str 1")
        private String str1;

        @ExcelProperty("Str 2")
        private String str2;
    }

    @Getter
    @Setter
    @FreezePane(colSplit = 2, rowSplit = 3, leftmostColumn = 5, topRow = 10)
    static class FreezePaneData implements FreezePaneModel {
        @ExcelProperty("Str 1")
        private String str1;

        @ExcelProperty("Str 2")
        private String str2;
    }

    @Getter
    @Setter
    static class NoFreezePaneData implements FreezePaneModel {
        @ExcelProperty("Str 1")
        private String str1;

        @ExcelProperty("Str 2")
        private String str2;
    }

    private <T extends FreezePaneModel> List<FreezePaneModel> buildData(Class<T> clazz, int rows) {
        try {
            List<FreezePaneModel> list = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                FreezePaneModel obj = clazz.getDeclaredConstructor().newInstance();
                obj.setStr1("String1-" + i);
                obj.setStr2("String2-" + i);
                list.add(obj);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFreezePane(
            File file, int expectedColSplit, int expectedRowSplit, int expectedLeftmost, int expectedTopRow)
            throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            PaneInformation pane = sheet.getPaneInformation();
            if (expectedColSplit == 0 && expectedRowSplit == 0) {
                Assertions.assertNull(pane, "Expected no freeze pane");
                return;
            }
            Assertions.assertNotNull(pane, "Expected freeze pane to be set");
            Assertions.assertEquals(
                    expectedColSplit, pane.getVerticalSplitPosition(), "Vertical split (colSplit) mismatch");
            Assertions.assertEquals(
                    expectedRowSplit, pane.getHorizontalSplitPosition(), "Horizontal split (rowSplit) mismatch");
            Assertions.assertEquals(expectedLeftmost, pane.getVerticalSplitLeftColumn(), "Leftmost column mismatch");
            Assertions.assertEquals(expectedTopRow, pane.getHorizontalSplitTopRow(), "Top row mismatch");
        }
    }

    @Test
    void should_freezeTwoColsOneRow_whenAnnotationColSplit2RowSplit1() throws Exception {
        runFreezeTwoColsOneRow(file07);
        runFreezeTwoColsOneRow(file03);
    }

    private void runFreezeTwoColsOneRow(File file) throws Exception {
        FesodSheet.write(file, FreezeTwoColsOneRowData.class)
                .sheet()
                .doWrite(buildData(FreezeTwoColsOneRowData.class, 5));
        assertFreezePane(file, 2, 1, 2, 1);
    }

    @Test
    void should_freezeHeaderRowOnly_whenAnnotationColSplit0RowSplit1() throws Exception {
        runFreezeHeaderRowOnly(file07);
        runFreezeHeaderRowOnly(file03);
    }

    private void runFreezeHeaderRowOnly(File file) throws Exception {
        FesodSheet.write(file, FreezeFirstRowOnlyData.class)
                .sheet()
                .doWrite(buildData(FreezeFirstRowOnlyData.class, 5));
        assertFreezePane(file, 0, 1, 0, 1);
    }

    @Test
    void should_freezeFirstColOnly_whenAnnotationColSplit1RowSplit0() throws Exception {
        runFreezeFirstColOnly(file07);
        runFreezeFirstColOnly(file03);
    }

    private void runFreezeFirstColOnly(File file) throws Exception {
        FesodSheet.write(file, FreezeFirstColOnlyData.class)
                .sheet()
                .doWrite(buildData(FreezeFirstColOnlyData.class, 5));
        assertFreezePane(file, 1, 0, 1, 0);
    }

    @Test
    void should_useExplicitPanePositions_whenAnnotationSetsLeftmostAndTopRow() throws Exception {
        runUseExplicitPanePositions(file07);
        runUseExplicitPanePositions(file03);
    }

    private void runUseExplicitPanePositions(File file) throws Exception {
        FesodSheet.write(file, FreezePaneData.class).sheet().doWrite(buildData(FreezePaneData.class, 5));
        assertFreezePane(file, 2, 3, 5, 10);
    }

    @Test
    void should_notSetFreezePane_whenAnnotationAbsent() throws Exception {
        runNotSetFreezePane(file07);
        runNotSetFreezePane(file03);
    }

    private void runNotSetFreezePane(File file) throws Exception {
        FesodSheet.write(file, NoFreezePaneData.class).sheet().doWrite(buildData(NoFreezePaneData.class, 5));
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Assertions.assertNull(sheet.getPaneInformation());
        }
    }

    @Test
    void should_applyFreezePane_whenRegisteredViaWriteHandler() throws Exception {
        runApplyFreezePaneStrategy(file07, false);
        runApplyFreezePaneStrategy(file03, false);
        runApplyFreezePaneStrategy(file07, true);
        runApplyFreezePaneStrategy(file03, true);
    }

    private void runApplyFreezePaneStrategy(File file, boolean useSimplify) throws Exception {
        SheetFreezePaneStrategy strategy;
        if (useSimplify) {
            strategy = new SheetFreezePaneStrategy(1, 1);
        } else {
            strategy = new SheetFreezePaneStrategy(1, 1, 1, 1);
        }

        FesodSheet.write(file, NoFreezePaneData.class)
                .registerWriteHandler(strategy)
                .sheet()
                .doWrite(buildData(NoFreezePaneData.class, 5));
        assertFreezePane(file, 1, 1, 1, 1);
    }

    @Test
    void should_applyDifferentFreezePanes_whenMultipleSheets() throws Exception {
        runApplyDifferentFreezePanes(file07);
        runApplyDifferentFreezePanes(file03);
    }

    private void runApplyDifferentFreezePanes(File file) throws Exception {
        try (ExcelWriter excelWriter = FesodSheet.write(file).build()) {
            excelWriter.write(
                    buildData(FreezeTwoColsOneRowData.class, 3),
                    FesodSheet.writerSheet(0)
                            .head(FreezeTwoColsOneRowData.class)
                            .build());

            excelWriter.write(
                    buildData(FreezeFirstRowOnlyData.class, 3),
                    FesodSheet.writerSheet(1).head(FreezeFirstRowOnlyData.class).build());
        }

        try (Workbook workbook = WorkbookFactory.create(file)) {
            // Sheet 0: colSplit=2, rowSplit=1
            Sheet sheet0 = workbook.getSheetAt(0);
            PaneInformation pane0 = sheet0.getPaneInformation();
            Assertions.assertNotNull(pane0);
            Assertions.assertEquals(2, pane0.getVerticalSplitPosition());
            Assertions.assertEquals(1, pane0.getHorizontalSplitPosition());

            // Sheet 1: colSplit=0, rowSplit=1
            Sheet sheet1 = workbook.getSheetAt(1);
            PaneInformation pane1 = sheet1.getPaneInformation();
            Assertions.assertNotNull(pane1);
            Assertions.assertEquals(0, pane1.getVerticalSplitPosition());
            Assertions.assertEquals(1, pane1.getHorizontalSplitPosition());
        }
    }
}
