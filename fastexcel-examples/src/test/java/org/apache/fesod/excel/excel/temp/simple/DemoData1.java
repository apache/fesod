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

package org.apache.fesod.excel.excel.temp.simple;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelIgnore;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.annotation.write.style.HeadStyle;
import org.apache.fesod.excel.enums.poi.FillPatternTypeEnum;

@Getter
@Setter
@EqualsAndHashCode
public class DemoData1 {
    @ExcelProperty("字符串标题")
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    private String string;

    @ExcelProperty("日期标题")
    private Date date;

    @ExcelProperty("数字标题")
    private Double doubleData;
    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;
}
