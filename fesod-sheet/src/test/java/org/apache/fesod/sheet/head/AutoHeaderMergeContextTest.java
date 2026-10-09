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

package org.apache.fesod.sheet.head;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The AUTO merge strategy must not join same-name header cells that live under different parent groups, and it must
 * keep merging equal cells vertically below the first header row.
 */
@Tag(Tags.ROUND_TRIP)
@Tag(Tags.WRITE)
public class AutoHeaderMergeContextTest extends AbstractExcelTest {

    @Test
    public void autoDoesNotMergeSameNameCellsUnderDifferentParents() throws Exception {
        File file = createTempFile("autoMergeIssue666", ExcelFormat.XLSX);

        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("head10"));
        head.add(Arrays.asList("head20", "head21"));
        head.add(Arrays.asList("head30", "head31"));
        head.add(Arrays.asList("head40", "head31"));
        head.add(Arrays.asList("head40", "head41"));
        FesodSheet.write(file).head(head).sheet().doWrite(dataRow());

        // "head31" appears in the columns below "head30" and below "head40": they describe different groups and must
        // stay separate (issue #666), while the plain first-row run "head40/head40" still merges.
        Assertions.assertFalse(
                containsRegion(file, 2, 3, 1, 1), "same-name cells under different parents must not merge");
        Assertions.assertTrue(containsRegion(file, 3, 4, 0, 0), "a first-row run of equal names still merges");
    }

    @Test
    public void autoMergesVerticalRunsBelowTheFirstHeaderRow() throws Exception {
        File file = createTempFile("autoMergeVertical", ExcelFormat.XLSX);

        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("G", "A", "A"));
        head.add(Arrays.asList("G", "B", "y"));
        head.add(Arrays.asList("G", "B", "z"));
        FesodSheet.write(file).head(head).sheet().doWrite(dataRow());

        // "A"/"A" share the parent "G" and must merge vertically; "B"/"B" share the merged parent "G" run and must
        // merge horizontally; the first-row "G" run merges as before.
        Assertions.assertTrue(
                containsRegion(file, 0, 0, 1, 2), "equal cells under the same parent must merge vertically");
        Assertions.assertTrue(
                containsRegion(file, 1, 2, 1, 1), "equal cells with equal parents still merge horizontally");
        Assertions.assertTrue(containsRegion(file, 0, 2, 0, 0), "a first-row run of equal names still merges");
    }

    private boolean containsRegion(File file, int firstCol, int lastCol, int firstRow, int lastRow) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstColumn() == firstCol
                        && region.getLastColumn() == lastCol
                        && region.getFirstRow() == firstRow
                        && region.getLastRow() == lastRow) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<Object>> dataRow() {
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("a", "b", "c", "d", "e"));
        return data;
    }
}
