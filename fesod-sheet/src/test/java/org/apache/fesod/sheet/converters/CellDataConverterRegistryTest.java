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

package org.apache.fesod.sheet.converters;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.fesod.sheet.converters.string.StringStringConverter;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CellDataConverterRegistry}.
 */
@Tag(Tags.UNIT)
class CellDataConverterRegistryTest {

    @Test
    void shouldResolveCustomWriteConverterBeforeParentCustomBeforeDefault() {
        // resolution order: local custom > parent custom > default, for write lookups
        StubConverter defaultWrite = new StubConverter("default", String.class);
        CellDataConverterRegistry root = new CellDataConverterRegistry();
        addDefaultConverterTo(root, String.class, null, defaultWrite);

        StubConverter parentWrite = new StubConverter("parent", String.class);
        root.addCustomWriteConverter(parentWrite);
        CellDataConverterRegistry child = new CellDataConverterRegistry(root);

        Assertions.assertSame(parentWrite, root.findWriteConverter(String.class));
        Assertions.assertSame(parentWrite, child.findWriteConverter(String.class));

        StubConverter localWrite = new StubConverter("local", String.class);
        child.addCustomWriteConverter(localWrite);
        Assertions.assertSame(localWrite, child.findWriteConverter(String.class));
    }

    @Test
    void shouldResolveCustomReadConverterBeforeParentCustomBeforeDefault() {
        // resolution order: local custom > parent custom > default, for read lookups
        StubConverter defaultRead = new StubConverter("default", String.class, CellDataTypeEnum.NUMBER);
        CellDataConverterRegistry root = new CellDataConverterRegistry();
        addDefaultConverterTo(root, String.class, CellDataTypeEnum.NUMBER, defaultRead);

        StubConverter parentRead = new StubConverter("parent", String.class, CellDataTypeEnum.NUMBER);
        root.addCustomReadConverter(parentRead);
        CellDataConverterRegistry child = new CellDataConverterRegistry(root);

        Assertions.assertSame(parentRead, root.findReadConverter(String.class, CellDataTypeEnum.NUMBER));
        Assertions.assertSame(parentRead, child.findReadConverter(String.class, CellDataTypeEnum.NUMBER));

        StubConverter localRead = new StubConverter("local", String.class, CellDataTypeEnum.NUMBER);
        child.addCustomReadConverter(localRead);
        Assertions.assertSame(localRead, child.findReadConverter(String.class, CellDataTypeEnum.NUMBER));
    }

    @Test
    void shouldResolveCustomReadConverterOnlyForItsDeclaredExcelType() {
        // read converters are keyed by (javaType, excelType): the NUMBER custom must not shadow the STRING default
        StubConverter numberCustom = new StubConverter("number", String.class, CellDataTypeEnum.NUMBER);
        StubConverter stringDefault = new StubConverter("string", String.class, CellDataTypeEnum.STRING);
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        addDefaultConverterTo(registry, String.class, CellDataTypeEnum.STRING, stringDefault);
        registry.addCustomReadConverter(numberCustom);

        Assertions.assertSame(numberCustom, registry.findReadConverter(String.class, CellDataTypeEnum.NUMBER));
        Assertions.assertSame(stringDefault, registry.findReadConverter(String.class, CellDataTypeEnum.STRING));
    }

    @Test
    void shouldPreferMostRecentlyRegisteredConverterForSameKey() {
        // for the same key, the converter registered later takes precedence
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        registry.addCustomWriteConverter(new StubConverter("first", String.class));
        StubConverter second = new StubConverter("second", String.class);
        registry.addCustomWriteConverter(second);

        Assertions.assertSame(second, registry.findWriteConverter(String.class));
    }

    @Test
    void shouldResolveCustomWriteConverterEvenWhenDefaultingToString() {
        // the to-string default tier must never shadow a custom converter (no-bean fill invariant)
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        StubConverter custom = new StubConverter("custom", String.class);
        registry.addCustomWriteConverter(custom);

        Assertions.assertSame(custom, registry.findWriteConverter(String.class, null, true));
    }

    @Test
    void shouldReturnNullInsteadOfNaturalDefaultWhenDefaultingToString() {
        // types without a to-string sibling resolve to null rather than falling back to the natural default
        StubConverter natural = new StubConverter("natural", Object.class);
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        addDefaultConverterTo(registry, Object.class, null, natural);

        Assertions.assertSame(natural, registry.findWriteConverter(Object.class, null, false));
        Assertions.assertNull(registry.findWriteConverter(Object.class, null, true));
    }

    @Test
    void shouldDefaultToBuiltInStringConverterFamily() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        Assertions.assertInstanceOf(StringStringConverter.class, registry.findWriteConverter(String.class, 0, true));
    }

    @Test
    void shouldPreferColumnBoundConverterOverGlobalOnlyForItsBoundColumn() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        StubConverter global = new StubConverter("global", String.class);
        StubColumnConverter bound = new StubColumnConverter("bound", 1);
        registry.addCustomWriteConverter(global);
        registry.addCustomWriteConverter(bound);

        Assertions.assertSame(bound, registry.findWriteConverter(String.class, 1));
        Assertions.assertSame(global, registry.findWriteConverter(String.class, 2));
        // a column-less lookup must never pick up a column-bound converter
        Assertions.assertSame(global, registry.findWriteConverter(String.class));
    }

    @Test
    void shouldNotTreatColumnBoundConverterWithoutIndexesAsGlobalFallback() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        registry.addCustomWriteConverter(new StubColumnConverter("orphan"));

        Assertions.assertNull(registry.findWriteConverter(String.class, 1));
        Assertions.assertNull(registry.findWriteConverter(String.class));
    }

    @Test
    void shouldRefreshCachedWriteLookupsAfterConverterRegistration() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        Assertions.assertNull(registry.findWriteConverter(String.class, 3));

        StubConverter late = new StubConverter("late", String.class);
        registry.addCustomWriteConverter(late);
        Assertions.assertSame(late, registry.findWriteConverter(String.class, 3));

        StubConverter later = new StubConverter("later", String.class);
        registry.addCustomWriteConverter(later);
        Assertions.assertSame(later, registry.findWriteConverter(String.class, 3));
    }

    @Test
    void shouldRefreshCachedReadLookupsAfterConverterRegistration() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        Assertions.assertNull(registry.findReadConverter(String.class, CellDataTypeEnum.NUMBER, 5));

        StubConverter late = new StubConverter("late", String.class, CellDataTypeEnum.NUMBER);
        registry.addCustomReadConverter(late);
        Assertions.assertSame(late, registry.findReadConverter(String.class, CellDataTypeEnum.NUMBER, 5));
    }

    @Test
    void shouldMatchPrimitiveAndBoxedKeysInterchangeably() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();
        StubConverter intWrite = new StubConverter("int", int.class);
        registry.addCustomWriteConverter(intWrite);
        Assertions.assertSame(intWrite, registry.findWriteConverter(int.class));
        Assertions.assertSame(intWrite, registry.findWriteConverter(Integer.class));

        StubConverter longRead = new StubConverter("long", long.class, CellDataTypeEnum.NUMBER);
        registry.addCustomReadConverter(longRead);
        Assertions.assertSame(longRead, registry.findReadConverter(Long.class, CellDataTypeEnum.NUMBER));
    }

    @Test
    void shouldRejectConverterMissingRequiredSupportKeys() {
        CellDataConverterRegistry registry = new CellDataConverterRegistry();

        Assertions.assertThrows(NullPointerException.class, () -> registry.addCustomWriteConverter(null));
        Assertions.assertThrows(
                NullPointerException.class, () -> registry.addCustomWriteConverter(new StubConverter("noKey", null)));
        Assertions.assertThrows(NullPointerException.class, () -> registry.addCustomReadConverter(null));
        Assertions.assertThrows(
                NullPointerException.class,
                () -> registry.addCustomReadConverter(new StubConverter("noExcelType", String.class, null)));
    }

    private static void addDefaultConverterTo(
            CellDataConverterRegistry targetRegistry,
            Class<?> javaType,
            CellDataTypeEnum cellDataType,
            Converter<?> converter) {
        targetRegistry.addDefaultConverters(
                Collections.singletonMap(ConverterKeyBuild.buildKey(javaType, cellDataType), converter));
    }

    private static class StubConverter implements Converter<Object> {
        private final String name;
        private final Class<?> javaTypeKey;
        private final CellDataTypeEnum excelTypeKey;

        StubConverter(String name, Class<?> javaTypeKey) {
            this(name, javaTypeKey, null);
        }

        StubConverter(String name, Class<?> javaTypeKey, CellDataTypeEnum excelTypeKey) {
            this.name = name;
            this.javaTypeKey = javaTypeKey;
            this.excelTypeKey = excelTypeKey;
        }

        @Override
        public Class<?> supportJavaTypeKey() {
            return javaTypeKey;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return excelTypeKey;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final class StubColumnConverter extends StubConverter implements ColumnBinding {
        private final Set<Integer> columnIndexes;

        StubColumnConverter(String name, Integer... columnIndexes) {
            super(name, String.class);
            this.columnIndexes = new HashSet<>(Arrays.asList(columnIndexes));
        }

        @Override
        public Set<Integer> columnIndexes() {
            return columnIndexes;
        }
    }
}
