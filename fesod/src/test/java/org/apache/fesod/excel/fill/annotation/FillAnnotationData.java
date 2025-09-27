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

package org.apache.fesod.excel.fill.annotation;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.annotation.format.DateTimeFormat;
import org.apache.fesod.excel.annotation.format.NumberFormat;
import org.apache.fesod.excel.annotation.write.style.ContentLoopMerge;
import org.apache.fesod.excel.annotation.write.style.ContentRowHeight;
import org.apache.fesod.excel.converters.string.StringImageConverter;

/**
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(100)
public class FillAnnotationData {
    @ExcelProperty("Date")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date date;

    @ExcelProperty(value = "Number")
    @NumberFormat("#.##%")
    private Double number;

    @ContentLoopMerge(columnExtend = 2)
    @ExcelProperty("String 1")
    private String string1;

    @ExcelProperty("String 2")
    private String string2;

    @ExcelProperty(value = "Image", converter = StringImageConverter.class)
    private String image;
}
