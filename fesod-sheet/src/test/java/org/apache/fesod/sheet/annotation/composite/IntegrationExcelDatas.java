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

package org.apache.fesod.sheet.annotation.composite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.annotation.format.NumberFormat;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.FreezePane;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.annotation.write.style.OnceAbsoluteMerge;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;
import org.apache.fesod.sheet.enums.poi.HorizontalAlignmentEnum;
import org.apache.fesod.sheet.enums.poi.VerticalAlignmentEnum;

/**
 * All model objects (test data) for integration tests.
 * Each group provides a Composite (composable annotations) and Direct (direct annotations)
 * model class with equivalent annotation semantics, plus matching data generators.
 */
public class IntegrationExcelDatas {

    private IntegrationExcelDatas() {}

    private static final int ROW_COUNT = 5;

    private static Date dateOf(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

    // ====================================================================
    //  Field-Level Models
    // ====================================================================

    // ---- ExcelProperty ----

    public static class FieldExcelProperty {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- DateTimeFormat ----

    public static class FieldDateTimeFormat {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeDateTimeFormat
            private Date date;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @DateTimeFormat("yyyy-MM-dd")
            private Date date;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setDate(dateOf(2026, 1, i + 1));
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setDate(dateOf(2026, 1, i + 1));
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- NumberFormat ----

    public static class FieldNumberFormat {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeNumberFormat
            private BigDecimal amount;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @NumberFormat("#,##0.00")
            private BigDecimal amount;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setAmount(BigDecimal.valueOf(100.0 + i * 10.5));
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setAmount(BigDecimal.valueOf(100.0 + i * 10.5));
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ColumnWidth (field-level) ----

    public static class FieldColumnWidth {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeColumnWidth
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ColumnWidth(25)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadStyle (field-level) ----

    public static class FieldHeadStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeHeadStyle
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @HeadStyle(
                    horizontalAlignment = HorizontalAlignmentEnum.CENTER,
                    fillForegroundColor = 42,
                    fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadFontStyle (field-level) ----

    public static class FieldHeadFontStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeHeadFontStyle
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @HeadFontStyle(bold = BooleanEnum.TRUE, fontHeightInPoints = 14)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentStyle (field-level) ----

    public static class FieldContentStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentStyle
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentFontStyle (field-level) ----

    public static class FieldContentFontStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentFontStyle
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentFontStyle(italic = BooleanEnum.TRUE, fontName = "Arial")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentLoopMerge (field-level) ----

    public static class FieldContentLoopMerge {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentLoopMerge
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentLoopMerge(eachRow = 2, columnExtend = 1)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ====================================================================
    //  Class-Level Models
    // ====================================================================

    // ---- ColumnWidth (class-level) ----

    public static class ClassColumnWidth {
        @IntegrationAnnotations.CompositeColumnWidth
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @ColumnWidth(25)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadStyle (class-level) ----

    public static class ClassHeadStyle {
        @IntegrationAnnotations.CompositeHeadStyle
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @HeadStyle(
                horizontalAlignment = HorizontalAlignmentEnum.CENTER,
                fillForegroundColor = 42,
                fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadFontStyle (class-level) ----

    public static class ClassHeadFontStyle {
        @IntegrationAnnotations.CompositeHeadFontStyle
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @HeadFontStyle(bold = BooleanEnum.TRUE, fontHeightInPoints = 14)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentStyle (class-level) ----

    public static class ClassContentStyle {
        @IntegrationAnnotations.CompositeContentStyle
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentFontStyle (class-level) ----

    public static class ClassContentFontStyle {
        @IntegrationAnnotations.CompositeContentFontStyle
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @ContentFontStyle(italic = BooleanEnum.TRUE, fontName = "Arial")
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadRowHeight (class-level) ----

    public static class ClassHeadRowHeight {
        @IntegrationAnnotations.CompositeHeadRowHeight
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @HeadRowHeight(40)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentRowHeight (class-level) ----

    public static class ClassContentRowHeight {
        @IntegrationAnnotations.CompositeContentRowHeight
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @ContentRowHeight(30)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- OnceAbsoluteMerge (class-level) ----

    public static class ClassOnceAbsoluteMerge {
        @IntegrationAnnotations.CompositeOnceAbsoluteMerge
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;

            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            private String value;
        }

        @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 0, firstColumnIndex = 0, lastColumnIndex = 1)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;

            @ExcelProperty("Value")
            private String value;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        c.setValue("Value" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        d.setValue("Value" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- FreezePane (class-level) ----

    public static class ClassFreezePane {
        @IntegrationAnnotations.CompositeFreezePane
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;

            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            private String value;
        }

        @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;

            @ExcelProperty("Value")
            private String value;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        c.setValue("Value" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        d.setValue("Value" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ====================================================================
    //  @AliasFor Models
    // ====================================================================

    // ---- ExcelProperty via AliasFor (same attribute name: value → value) ----

    public static class AliasForExcelProperty {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Custom Name")
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Custom Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ColumnWidth via AliasFor (different name: width → value) ----

    public static class AliasForColumnWidth {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeColumnWidthAliasFor(width = 20)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ColumnWidth(20)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- DateTimeFormat via AliasFor (different name: pattern → value) ----

    public static class AliasForDateTimeFormat {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeDateTimeFormatAliasFor(pattern = "yyyy/MM/dd")
            private Date date;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @DateTimeFormat("yyyy/MM/dd")
            private Date date;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setDate(dateOf(2026, 1, i + 1));
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setDate(dateOf(2026, 1, i + 1));
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- NumberFormat via AliasFor (different name: pattern → value) ----

    public static class AliasForNumberFormat {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeNumberFormatAliasFor(pattern = "0.00%")
            private BigDecimal amount;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @NumberFormat("0.00%")
            private BigDecimal amount;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setAmount(BigDecimal.valueOf(0.5 + i * 0.1));
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setAmount(BigDecimal.valueOf(0.5 + i * 0.1));
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadFontStyle via multiple AliasFor (fontSize → fontHeightInPoints, fontColor → color) ----

    public static class AliasForHeadFontStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeHeadFontStyleAliasFor(fontSize = 16, fontColor = 10)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @HeadFontStyle(fontHeightInPoints = 16, color = 10)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadStyle via AliasFor (alignment → horizontalAlignment, bgColor → fillForegroundColor) ----

    public static class AliasForHeadStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeHeadStyleAliasFor(alignment = HorizontalAlignmentEnum.RIGHT, bgColor = 13)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.RIGHT, fillForegroundColor = 13)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentStyle via AliasFor (wrap → wrapped, vAlign → verticalAlignment) ----

    public static class AliasForContentStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentStyleAliasFor(
                    wrap = BooleanEnum.TRUE,
                    vAlign = VerticalAlignmentEnum.CENTER)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentStyle(wrapped = BooleanEnum.TRUE, verticalAlignment = VerticalAlignmentEnum.CENTER)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentFontStyle via AliasFor (font → fontName, size → fontHeightInPoints) ----

    public static class AliasForContentFontStyle {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentFontStyleAliasFor(font = "Courier New", size = 18)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentFontStyle(fontName = "Courier New", fontHeightInPoints = 18)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentLoopMerge via AliasFor (rows → eachRow, cols → columnExtend) ----

    public static class AliasForContentLoopMerge {
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeContentLoopMergeAliasFor(rows = 3, cols = 1)
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ContentLoopMerge(eachRow = 3, columnExtend = 1)
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- HeadRowHeight via AliasFor (height → value) ----

    public static class AliasForHeadRowHeight {
        @IntegrationAnnotations.CompositeHeadRowHeightAliasFor(height = 50)
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @HeadRowHeight(50)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- ContentRowHeight via AliasFor (height → value) ----

    public static class AliasForContentRowHeight {
        @IntegrationAnnotations.CompositeContentRowHeightAliasFor(height = 35)
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;
        }

        @ContentRowHeight(35)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- OnceAbsoluteMerge via AliasFor (startRow/endRow/startCol/endCol → 4 inner attrs) ----

    public static class AliasForOnceAbsoluteMerge {
        @IntegrationAnnotations.CompositeOnceAbsoluteMergeAliasFor(startRow = 0, endRow = 1, startCol = 0, endCol = 1)
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;

            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            private String value;
        }

        @OnceAbsoluteMerge(firstRowIndex = 0, lastRowIndex = 1, firstColumnIndex = 0, lastColumnIndex = 1)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;

            @ExcelProperty("Value")
            private String value;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        c.setValue("Value" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        d.setValue("Value" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ---- FreezePane via AliasFor (colSplit/rowSplit/leftmostColumn/topRow → 4 inner attrs) ----

    public static class AliasForFreezePane {
        @IntegrationAnnotations.CompositeFreezePaneAliasFor(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            private String name;

            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            private String value;
        }

        @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 3, topRow = 5)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            private String name;

            @ExcelProperty("Value")
            private String value;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        c.setValue("Value" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        d.setValue("Value" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    // ====================================================================
    //  Mixed-Level Model
    // ====================================================================

    // ---- Direct annotation overrides composite at same level ----

    public static class PriorityDirectOverComposite {
        /**
         * Field has BOTH composite and direct annotations.
         * Direct {@code @ExcelProperty("Final Value")} should win over
         * composite's aliased value "Value".
         */
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            @ExcelProperty(value = "Final Value")
            private String name;
        }

        @Data
        public static class Direct {
            @ExcelProperty(value = "Final Value")
            private String name;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }

    public static class MixedAll {
        @IntegrationAnnotations.CompositeHeadRowHeight
        @IntegrationAnnotations.CompositeContentRowHeight
        @Data
        public static class Composite {
            @IntegrationAnnotations.CompositeExcelProperty
            @IntegrationAnnotations.CompositeColumnWidth
            @IntegrationAnnotations.CompositeHeadStyle
            private String name;

            @IntegrationAnnotations.CompositeExcelPropertyAliasFor(value = "Value")
            @IntegrationAnnotations.CompositeContentFontStyle
            private String value;
        }

        @HeadRowHeight(40)
        @ContentRowHeight(30)
        @Data
        public static class Direct {
            @ExcelProperty("Name")
            @ColumnWidth(25)
            @HeadStyle(
                    horizontalAlignment = HorizontalAlignmentEnum.CENTER,
                    fillForegroundColor = 42,
                    fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND)
            private String name;

            @ExcelProperty("Value")
            @ContentFontStyle(italic = BooleanEnum.TRUE, fontName = "Arial")
            private String value;
        }

        public static List<Composite> compositeData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Composite c = new Composite();
                        c.setName("Name" + i);
                        c.setValue("Value" + i);
                        return c;
                    })
                    .collect(Collectors.toList());
        }

        public static List<Direct> directData() {
            return IntStream.range(0, ROW_COUNT)
                    .mapToObj(i -> {
                        Direct d = new Direct();
                        d.setName("Name" + i);
                        d.setValue("Value" + i);
                        return d;
                    })
                    .collect(Collectors.toList());
        }
    }
}
