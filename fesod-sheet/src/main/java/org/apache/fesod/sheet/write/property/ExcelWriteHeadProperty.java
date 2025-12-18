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

package org.apache.fesod.sheet.write.property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;
import org.apache.fesod.sheet.enums.HeadKindEnum;
import org.apache.fesod.sheet.enums.HeaderMergeStrategy;
import org.apache.fesod.sheet.metadata.CellRange;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.property.ColumnWidthProperty;
import org.apache.fesod.sheet.metadata.property.ExcelHeadProperty;
import org.apache.fesod.sheet.metadata.property.FontProperty;
import org.apache.fesod.sheet.metadata.property.LoopMergeProperty;
import org.apache.fesod.sheet.metadata.property.OnceAbsoluteMergeProperty;
import org.apache.fesod.sheet.metadata.property.RowHeightProperty;
import org.apache.fesod.sheet.metadata.property.StyleProperty;

/**
 * Define the header attribute of excel
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class ExcelWriteHeadProperty extends ExcelHeadProperty {

    private RowHeightProperty headRowHeightProperty;
    private RowHeightProperty contentRowHeightProperty;
    private OnceAbsoluteMergeProperty onceAbsoluteMergeProperty;

    public ExcelWriteHeadProperty(
            ConfigurationHolder configurationHolder, Class<?> headClazz, List<List<String>> head) {
        super(configurationHolder, headClazz, head);
        if (getHeadKind() != HeadKindEnum.CLASS) {
            return;
        }
        this.headRowHeightProperty = RowHeightProperty.build(headClazz.getAnnotation(HeadRowHeight.class));
        this.contentRowHeightProperty = RowHeightProperty.build(headClazz.getAnnotation(ContentRowHeight.class));
        this.onceAbsoluteMergeProperty =
                OnceAbsoluteMergeProperty.build(headClazz.getAnnotation(OnceAbsoluteMerge.class));

        ColumnWidth parentColumnWidth = headClazz.getAnnotation(ColumnWidth.class);
        HeadStyle parentHeadStyle = headClazz.getAnnotation(HeadStyle.class);
        HeadFontStyle parentHeadFontStyle = headClazz.getAnnotation(HeadFontStyle.class);

        for (Map.Entry<Integer, Head> entry : getHeadMap().entrySet()) {
            Head headData = entry.getValue();
            if (headData == null) {
                throw new IllegalArgumentException(
                        "Passing in the class and list the head, the two must be the same size.");
            }
            Field field = headData.getField();

            ColumnWidth columnWidth = field.getAnnotation(ColumnWidth.class);
            if (columnWidth == null) {
                columnWidth = parentColumnWidth;
            }
            headData.setColumnWidthProperty(ColumnWidthProperty.build(columnWidth));

            HeadStyle headStyle = field.getAnnotation(HeadStyle.class);
            if (headStyle == null) {
                headStyle = parentHeadStyle;
            }
            headData.setHeadStyleProperty(StyleProperty.build(headStyle));

            HeadFontStyle headFontStyle = field.getAnnotation(HeadFontStyle.class);
            if (headFontStyle == null) {
                headFontStyle = parentHeadFontStyle;
            }
            headData.setHeadFontProperty(FontProperty.build(headFontStyle));

            headData.setLoopMergeProperty(LoopMergeProperty.build(field.getAnnotation(ContentLoopMerge.class)));
        }
    }

    /**
     * Calculate all cells that need to be merged
     *
     * @return cells that need to be merged
     * @deprecated Use {@link #headCellRangeList(HeaderMergeStrategy)} instead
     */
    @Deprecated
    public List<CellRange> headCellRangeList() {
        return headCellRangeList(HeaderMergeStrategy.AUTO);
    }

    /**
     * Calculate all cells that need to be merged based on the merge strategy
     *
     * @param mergeStrategy The merge strategy to use
     * @return cells that need to be merged
     */
    public List<CellRange> headCellRangeList(HeaderMergeStrategy mergeStrategy) {
        if (mergeStrategy == null || mergeStrategy == HeaderMergeStrategy.NONE) {
            return new ArrayList<>();
        }

        List<CellRange> cellRangeList = new ArrayList<>();
        Set<String> alreadyRangeSet = new HashSet<>();
        List<Head> headList = new ArrayList<>(getHeadMap().values());

        for (int i = 0; i < headList.size(); i++) {
            Head head = headList.get(i);
            List<String> headNameList = head.getHeadNameList();
            for (int j = 0; j < headNameList.size(); j++) {
                if (alreadyRangeSet.contains(i + "-" + j)) {
                    continue;
                }
                alreadyRangeSet.add(i + "-" + j);
                String headName = headNameList.get(j);
                int lastCol = i;
                int lastRow = j;

                // Horizontal merge (if allowed by strategy)
                if (mergeStrategy == HeaderMergeStrategy.HORIZONTAL_ONLY
                        || mergeStrategy == HeaderMergeStrategy.FULL_RECTANGLE
                        || mergeStrategy == HeaderMergeStrategy.AUTO) {
                    for (int k = i + 1; k < headList.size(); k++) {
                        String key = k + "-" + j;
                        if (headList.get(k).getHeadNameList().size() > j
                                && Objects.equals(
                                        headList.get(k).getHeadNameList().get(j), headName)
                                && !alreadyRangeSet.contains(key)) {
                            alreadyRangeSet.add(key);
                            lastCol = k;
                        } else {
                            break;
                        }
                    }
                }

                // Vertical merge (if allowed by strategy)
                Set<String> tempAlreadyRangeSet = new HashSet<>();
                if (mergeStrategy == HeaderMergeStrategy.VERTICAL_ONLY
                        || mergeStrategy == HeaderMergeStrategy.FULL_RECTANGLE
                        || mergeStrategy == HeaderMergeStrategy.AUTO) {
                    outer:
                    for (int k = j + 1; k < headNameList.size(); k++) {
                        // For FULL_RECTANGLE and AUTO, verify all cells in the row
                        boolean canMerge = true;
                        for (int l = i; l <= lastCol; l++) {
                            String key = l + "-" + k;
                            if (headList.get(l).getHeadNameList().size() <= k
                                    || !Objects.equals(
                                            headList.get(l).getHeadNameList().get(k), headName)
                                    || alreadyRangeSet.contains(key)) {
                                canMerge = false;
                                break;
                            }
                        }

                        // For AUTO strategy, also check context consistency
                        if (canMerge && mergeStrategy == HeaderMergeStrategy.AUTO) {
                            canMerge = canMergeVertically(headList, j, k, i, lastCol);
                        }

                        if (canMerge) {
                            for (int l = i; l <= lastCol; l++) {
                                String key = l + "-" + k;
                                tempAlreadyRangeSet.add(key);
                            }
                            lastRow = k;
                        } else {
                            break outer;
                        }
                    }
                    alreadyRangeSet.addAll(tempAlreadyRangeSet);
                }

                // For FULL_RECTANGLE strategy, verify the entire rectangle is valid
                if (mergeStrategy == HeaderMergeStrategy.FULL_RECTANGLE) {
                    if (!isValidRectangleRegion(headList, j, lastRow, i, lastCol, headName)) {
                        // If rectangle is invalid, fall back to single cell (no merge)
                        continue;
                    }
                }

                // Add merge range if it's larger than a single cell
                if (j != lastRow || i != lastCol) {
                    cellRangeList.add(new CellRange(
                            j,
                            lastRow,
                            head.getColumnIndex(),
                            headList.get(lastCol).getColumnIndex()));
                }
            }
        }
        return cellRangeList;
    }

    /**
     * Check if two rows can be merged vertically based on context consistency
     *
     * @param headList    The list of heads
     * @param row1        First row index
     * @param row2        Second row index
     * @param startCol    Start column index
     * @param endCol      End column index
     * @return true if the rows can be merged
     */
    private boolean canMergeVertically(List<Head> headList, int row1, int row2, int startCol, int endCol) {
        // Check if there's a row above that provides context
        if (row1 > 0) {
            // Check if all cells in the range have the same context above
            for (int col = startCol; col <= endCol; col++) {
                boolean hasUpper1 = headList.get(col).getHeadNameList().size() > row1;
                boolean hasUpper2 = headList.get(col).getHeadNameList().size() > row2;

                // If one row has upper context but the other doesn't, don't merge
                if (hasUpper1 != hasUpper2) {
                    return false;
                }

                if (hasUpper1) {
                    String upper1 = headList.get(col).getHeadNameList().get(row1 - 1);
                    String upper2 = headList.get(col).getHeadNameList().get(row2 - 1);
                    // If context (upper cells) is different, don't merge
                    if (!Objects.equals(upper1, upper2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verify if a rectangle region is valid (all cells exist and have the same name)
     *
     * @param headList      The list of heads
     * @param startRow      Start row index
     * @param endRow        End row index
     * @param startCol      Start column index
     * @param endCol        End column index
     * @param expectedName  Expected cell name
     * @return true if the rectangle is valid
     */
    private boolean isValidRectangleRegion(
            List<Head> headList, int startRow, int endRow, int startCol, int endCol, String expectedName) {
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (headList.get(col).getHeadNameList().size() <= row) {
                    return false; // Cell doesn't exist
                }
                String cellName = headList.get(col).getHeadNameList().get(row);
                if (!Objects.equals(expectedName, cellName)) {
                    return false; // Cell name doesn't match
                }
            }
        }
        return true;
    }
}
