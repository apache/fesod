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

package org.apache.fesod.sheet.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.ConverterKeyBuild.ConverterKey;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.metadata.ConfigurationHolder;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link SheetConverterResolver}: memoized results must be identical to the direct lookups
 * the per-cell path performed, and the memo must engage (no repeated probes).
 */
@Tag(Tags.UNIT)
@ExtendWith(MockitoExtension.class)
class SheetConverterResolverTest {

    /**
     * Counts probes so the tests can observe that the memo engages.
     */
    private static final class CountingConverterMap extends HashMap<ConverterKey, Converter<?>> {

        private int probes;

        @Override
        public Converter<?> get(Object key) {
            probes++;
            return super.get(key);
        }
    }

    @Getter
    @Setter
    private static class Model {
        @ExcelProperty("Name")
        private String name;
    }

    @Mock
    private ConfigurationHolder configurationHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    private CountingConverterMap converterMap;

    private SheetConverterResolver resolver;

    @BeforeEach
    void setUp() {
        // Only the contentProperty tests reach ClassUtils; the converter tests must not trip
        // strict-stub checks on these.
        Mockito.lenient().when(configurationHolder.globalConfiguration()).thenReturn(globalConfiguration);
        Mockito.lenient().when(globalConfiguration.getFiledCacheLocation()).thenReturn(CacheLocationEnum.NONE);
        converterMap = new CountingConverterMap();
        resolver = new SheetConverterResolver(configurationHolder, Model.class, converterMap);
    }

    @Test
    void converterReturnsTheDirectProbeResult() {
        Converter<?> stringConverter = Mockito.mock(Converter.class);
        Converter<?> numberConverter = Mockito.mock(Converter.class);
        converterMap.put(ConverterKeyBuild.buildKey(String.class, CellDataTypeEnum.STRING), stringConverter);
        converterMap.put(ConverterKeyBuild.buildKey(BigDecimal.class, CellDataTypeEnum.NUMBER), numberConverter);

        Assertions.assertSame(stringConverter, resolver.converter(String.class, CellDataTypeEnum.STRING));
        Assertions.assertSame(numberConverter, resolver.converter(BigDecimal.class, CellDataTypeEnum.NUMBER));
        // A pair with no registered converter returns null, exactly as the direct probe would.
        Assertions.assertNull(resolver.converter(String.class, CellDataTypeEnum.BOOLEAN));
    }

    @Test
    void converterMemoizesPerClassAndCellType() {
        Converter<?> stringConverter = Mockito.mock(Converter.class);
        converterMap.put(ConverterKeyBuild.buildKey(String.class, CellDataTypeEnum.STRING), stringConverter);

        Assertions.assertSame(stringConverter, resolver.converter(String.class, CellDataTypeEnum.STRING));
        Assertions.assertSame(stringConverter, resolver.converter(String.class, CellDataTypeEnum.STRING));
        Assertions.assertEquals(1, converterMap.probes);

        // A different cell type of the same class is a separate memo entry.
        Assertions.assertNull(resolver.converter(String.class, CellDataTypeEnum.NUMBER));
        Assertions.assertEquals(2, converterMap.probes);
    }

    @Test
    void contentPropertyDelegatesAndMemoizes() throws Exception {
        ExcelContentProperty first = resolver.contentProperty(null, "name");
        ExcelContentProperty second = resolver.contentProperty(null, "name");

        Assertions.assertSame(first, second);
        Field expectedField = Model.class.getDeclaredField("name");
        Assertions.assertEquals(expectedField, first.getField());

        // An unmapped field name resolves to an empty property and is memoized as well.
        ExcelContentProperty missing = resolver.contentProperty(null, "missing");
        Assertions.assertSame(missing, resolver.contentProperty(null, "missing"));
        Assertions.assertNull(missing.getField());
    }
}
