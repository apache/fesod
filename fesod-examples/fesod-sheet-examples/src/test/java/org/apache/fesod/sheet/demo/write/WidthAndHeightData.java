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
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.annotation.write.style.HeadRowHeight;

/**
 * Basic data class
 *
 *
 **/
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(10)
@HeadRowHeight(20)
@ColumnWidth(25)
public class WidthAndHeightData {
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;
    /**
     * Width is 50
     */
    @ColumnWidth(50)
    @ExcelProperty("Double Title")
    private Double doubleData;
}
