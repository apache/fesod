/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.idev.excel.test.core.converter;

import cn.idev.excel.annotation.ExcelProperty;
import java.sql.Timestamp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class CustomConverterWriteData {
    @ExcelProperty(value = "时间戳-字符串", converter = TimestampStringConverter.class)
    private Timestamp timestampStringData;

    @ExcelProperty(value = "时间戳-数字", converter = TimestampNumberConverter.class)
    private Timestamp timestampNumberData;
}
