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
 * Style data class
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
// Head background red
@HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
// Head font size 20
@HeadFontStyle(fontHeightInPoints = 20)
// Content background green
@ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 17)
// Content font size 20
@ContentFontStyle(fontHeightInPoints = 20)
public class DemoStyleData {
    // String head background pink
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 14)
    // String head font size 30
    @HeadFontStyle(fontHeightInPoints = 30)
    // String content background sky blue
    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    // String content font size 30
    @ContentFontStyle(fontHeightInPoints = 30)
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ExcelProperty("Double Title")
    private Double doubleData;
}
