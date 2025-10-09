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

package org.apache.fesod.excel.fill.style;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.write.style.ContentFontStyle;
import org.apache.fesod.excel.annotation.write.style.ContentStyle;
import org.apache.fesod.excel.enums.BooleanEnum;
import org.apache.fesod.excel.enums.poi.FillPatternTypeEnum;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class FillStyleAnnotatedData {
    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 13)
    @ContentFontStyle(bold = BooleanEnum.TRUE, color = 19)
    private String name;

    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ContentFontStyle(bold = BooleanEnum.TRUE, color = 16)
    private Double number;

    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 17)
    @ContentFontStyle(bold = BooleanEnum.TRUE, color = 58)
    private Date date;

    @ContentStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 12)
    @ContentFontStyle(bold = BooleanEnum.TRUE, color = 18)
    private String empty;
}
