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

package org.apache.fesod.sheet.examples.write.data;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ContentFontStyle;
import org.apache.fesod.sheet.annotation.write.style.ContentStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.annotation.write.style.HeadStyle;
import org.apache.fesod.sheet.enums.poi.FillPatternTypeEnum;

/**
 * Data model demonstrating annotation-based style customization.
 *
 * <h2>Class-Level Styles (Default for All Columns)</h2>
 * <ul>
 *   <li><b>Header:</b> Solid fill, color 10 (dark green), font size 20pt</li>
 *   <li><b>Content:</b> Solid fill, color 17 (light yellow), font size 20pt</li>
 * </ul>
 *
 * <h2>Field-Level Overrides ("String Title" Column Only)</h2>
 * <ul>
 *   <li><b>Header:</b> Solid fill, color 14 (dark teal), font size 30pt</li>
 *   <li><b>Content:</b> Solid fill, color 40 (light blue), font size 30pt</li>
 * </ul>
 *
 * <h2>Style Annotations Reference</h2>
 * <ul>
 *   <li>{@link HeadStyle} / {@link ContentStyle} — Cell background fill pattern and color.
 *       Use {@code fillPatternType = SOLID_FOREGROUND} with {@code fillForegroundColor} for
 *       solid background colors.</li>
 *   <li>{@link HeadFontStyle} / {@link ContentFontStyle} — Font properties like size,
 *       bold, italic, color, and font name.</li>
 *   <li>Field-level annotations override class-level annotations for that specific column.</li>
 * </ul>
 *
 * @see HeadStyle
 * @see HeadFontStyle
 * @see ContentStyle
 * @see ContentFontStyle
 * @see org.apache.fesod.sheet.examples.write.StyleWriteExample
 */
@Getter
@Setter
@EqualsAndHashCode
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
@HeadFontStyle(fontHeightInPoints = 20)
@ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 17)
@ContentFontStyle(fontHeightInPoints = 20)
public class DemoStyleData {
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 14)
    @HeadFontStyle(fontHeightInPoints = 30)
    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    @ContentFontStyle(fontHeightInPoints = 30)
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ExcelProperty("Number Title")
    private Double doubleData;
}
