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

package org.apache.fesod.excel.demo.write;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.converters.ReadConverterContext;
import org.apache.fesod.excel.converters.WriteConverterContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.metadata.data.WriteCellData;

/**
 * converter to the original data record
 */
@Slf4j
public class RecordStringStringConverter implements Converter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadConverterContext<?> context) {
        return context.getReadCellData().getStringValue();
    }

    /**
     * 这里是写的时候会可以访问到原始数据，你可以进行其他业务处理，然后进行转换
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        // 获取原始数据
        RecordData record = context.getRecord();
        log.info("原始数据：{}", record);
        return new WriteCellData<>("自定义：" + context.getValue() + "-" + record.getDoubleData());
    }
}
