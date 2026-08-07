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

package org.apache.fesod.sheet.examples.read.converters;

import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ReadConverterContext;
import org.apache.fesod.sheet.converters.WriteConverterContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.data.WriteCellData;

/**
 * Custom converter that transforms string cell values by prepending a prefix.
 *
 * <h2>Scenario</h2>
 * <p>You need custom transformation logic that goes beyond simple formatting —
 * for example, adding prefixes, decrypting values, or looking up reference data.
 * Implement the {@link Converter} interface to create a reusable converter.</p>
 *
 * <h2>How It Works</h2>
 * <ul>
 *   <li><b>Read:</b> Excel cell "Hello" → Java string "Custom：Hello"</li>
 *   <li><b>Write:</b> Java string "Hello" → Excel cell "Hello" (passthrough)</li>
 * </ul>
 *
 * <h2>Registration</h2>
 * <p>Converters can be registered in two ways:</p>
 * <ol>
 *   <li><b>Per-field:</b> {@code @ExcelProperty(converter = CustomStringStringConverter.class)}
 *       on a specific field (see {@link org.apache.fesod.sheet.examples.read.data.ConverterData}).</li>
 *   <li><b>Global:</b> {@code .registerConverter(new CustomStringStringConverter())} on the builder
 *       (see {@link org.apache.fesod.sheet.examples.advanced.CustomConverterExample}).
 *       Applies to all fields matching the Java type + Excel type key.</li>
 * </ol>
 *
 * <h2>Type Keys</h2>
 * <p>{@link #supportJavaTypeKey()} returns {@code String.class} and
 * {@link #supportExcelTypeKey()} returns {@code CellDataTypeEnum.STRING},
 * meaning this converter handles String↔String conversions only.</p>
 *
 * @see Converter
 * @see org.apache.fesod.sheet.annotation.ExcelProperty#converter()
 */
public class CustomStringStringConverter implements Converter<String> {

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
        return "Custom：" + context.getReadCellData().getStringValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        return new WriteCellData<>(context.getValue());
    }
}
