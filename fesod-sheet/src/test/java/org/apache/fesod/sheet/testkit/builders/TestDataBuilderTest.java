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

package org.apache.fesod.sheet.testkit.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import org.apache.fesod.sheet.converter.ConverterWriteData;
import org.apache.fesod.sheet.style.FillAnnotationData;
import org.apache.fesod.sheet.style.FillStyleAnnotatedData;
import org.apache.fesod.sheet.style.FillStyleData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.TestUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class TestDataBuilderTest {

    @Test
    void simpleDataCountAccuracy() {
        List<SimpleData> data = TestDataBuilder.simpleData(10);
        assertEquals(10, data.size());
        for (SimpleData d : data) {
            assertNotNull(d);
            assertNotNull(d.getName());
        }
    }

    @Test
    void simpleDataFieldValues() {
        List<SimpleData> data = TestDataBuilder.simpleData(3);
        assertEquals("Name0", data.get(0).getName());
        assertEquals("Name1", data.get(1).getName());
        assertEquals("Name2", data.get(2).getName());
    }

    @Test
    void simpleDataSingleElement() {
        List<SimpleData> data = TestDataBuilder.simpleData(1);
        assertEquals(1, data.size());
        assertEquals("Name0", data.get(0).getName());
    }

    @Test
    void converterWriteDataNotEmpty() {
        List<ConverterWriteData> data = TestDataBuilder.converterWriteData();
        assertFalse(data.isEmpty());
        assertEquals(1, data.size());
    }

    @Test
    void converterWriteDataFieldValues() {
        ConverterWriteData data = TestDataBuilder.converterWriteData().get(0);
        assertEquals(TestUtil.TEST_DATE, data.getDate());
        assertEquals(TestUtil.TEST_LOCAL_DATE, data.getLocalDate());
        assertEquals(TestUtil.TEST_LOCAL_DATE_TIME, data.getLocalDateTime());
        assertEquals(Boolean.TRUE, data.getBooleanData());
        assertEquals(1, data.getBigDecimal().intValue());
        assertEquals(1, data.getBigInteger().intValue());
        assertEquals(1L, data.getLongData());
        assertEquals(1, (int) data.getIntegerData());
        assertEquals((short) 1, (short) data.getShortData());
        assertEquals((byte) 1, (byte) data.getByteData());
        assertEquals(1.0, data.getDoubleData(), 0.0);
        assertEquals((float) 1.0, data.getFloatData(), 0.0);
        assertEquals("test", data.getString());
        assertEquals("custom", data.getCellData().getStringValue());
    }

    @Test
    void fillStyleDataMatchesLegacyFixtures() throws Exception {
        List<FillStyleData> data = TestDataBuilder.fillStyleData(10);
        assertEquals(10, data.size());
        assertEquals("Zhang San", data.get(0).getName());
        assertEquals(5.2, data.get(0).getNumber(), 0.0);
        assertEquals("2020-01-01 01:01:01", DateUtils.format(data.get(0).getDate(), "yyyy-MM-dd HH:mm:ss"));
        assertEquals(null, data.get(5).getName());
    }

    @Test
    void fillStyleAnnotatedDataMatchesLegacyFixtures() throws Exception {
        List<FillStyleAnnotatedData> data = TestDataBuilder.fillStyleAnnotatedData(10);
        assertEquals(10, data.size());
        assertEquals("Zhang San", data.get(0).getName());
        assertEquals(5.2, data.get(0).getNumber(), 0.0);
        assertEquals(null, data.get(5).getName());
    }

    @Test
    void fillAnnotationDataMatchesLegacyFixtures() throws Exception {
        List<FillAnnotationData> data = TestDataBuilder.fillAnnotationData(5, "image-path");
        assertEquals(5, data.size());
        assertEquals(99.99, data.get(0).getNumber(), 0.0);
        assertEquals("string1", data.get(0).getString1());
        assertEquals("string2", data.get(0).getString2());
        assertEquals("image-path", data.get(0).getImage());
    }

    @Test
    void titleDataMatchesExpectedPrefixPattern() {
        assertEquals("sheet-0", TestDataBuilder.titleData(2, "sheet-").get(0).getTitle());
        assertEquals("sheet-1", TestDataBuilder.titleData(2, "sheet-").get(1).getTitle());
    }
}
