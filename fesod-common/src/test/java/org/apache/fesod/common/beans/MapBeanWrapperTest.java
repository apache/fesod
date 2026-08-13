/*
 * Licensed to the Apache Software Foundation (ASF)
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

package org.apache.fesod.common.beans;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MapBeanWrapper}.
 */
class MapBeanWrapperTest {

    private static Map<String, Object> sampleMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "Jack");
        map.put("age", 18);
        map.put("active", true);
        return map;
    }

    @Test
    void shouldRejectNullMap() {
        Assertions.assertThrows(NullPointerException.class, () -> new MapBeanWrapper(null));
    }

    @Test
    void shouldGetPropertyValue() {
        MapBeanWrapper wrapper = new MapBeanWrapper(sampleMap());

        Assertions.assertEquals("Jack", wrapper.getProperty("name"));
        Assertions.assertEquals(18, wrapper.getProperty("age"));
        Assertions.assertEquals(true, wrapper.getProperty("active"));
    }

    @Test
    void shouldReturnNullForMissingKey() {
        MapBeanWrapper wrapper = new MapBeanWrapper(sampleMap());

        Assertions.assertNull(wrapper.getProperty("nope"));
    }

    @Test
    void shouldSetProperty() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        wrapper.setProperty("name", "Tom");

        Assertions.assertEquals("Tom", map.get("name"));
        Assertions.assertEquals("Tom", wrapper.getProperty("name"));
    }

    @Test
    void shouldBatchSetProperties() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);
        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("name", "Tomas");
        extra.put("city", "Beijing");

        wrapper.setProperties(extra);

        Assertions.assertEquals("Tomas", map.get("name"));
        Assertions.assertEquals("Beijing", map.get("city"));
    }

    @Test
    void shouldTreatNullOrEmptyPropertiesAsNoOp() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        wrapper.setProperties(null);
        wrapper.setProperties(Collections.emptyMap());

        Assertions.assertEquals(3, map.size());
    }

    @Test
    void shouldReportPropertyNames() {
        MapBeanWrapper wrapper = new MapBeanWrapper(sampleMap());

        Assertions.assertTrue(wrapper.getPropertyNames().contains("name"));
        Assertions.assertTrue(wrapper.getPropertyNames().contains("age"));
        Assertions.assertTrue(wrapper.getPropertyNames().contains("active"));
    }

    @Test
    void shouldExposeUnmodifiablePropertyNames() {
        MapBeanWrapper wrapper = new MapBeanWrapper(sampleMap());

        Assertions.assertThrows(UnsupportedOperationException.class, () -> wrapper.getPropertyNames()
                .add("evil"));
    }

    @Test
    void shouldReportContainsProperty() {
        MapBeanWrapper wrapper = new MapBeanWrapper(sampleMap());

        Assertions.assertTrue(wrapper.containsProperty("name"));
        Assertions.assertFalse(wrapper.containsProperty("nope"));
    }

    @Test
    void shouldReportPropertySize() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        Assertions.assertEquals(map.size(), wrapper.getPropertySize());
    }

    @Test
    void shouldInferPropertyTypeFromValue() {
        Map<String, Object> map = sampleMap();
        map.put("nullable", null);
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        Assertions.assertEquals(String.class, wrapper.getPropertyType("name"));
        Assertions.assertEquals(Integer.class, wrapper.getPropertyType("age"));
        Assertions.assertEquals(Boolean.class, wrapper.getPropertyType("active"));
        Assertions.assertNull(wrapper.getPropertyType("nullable"));
        Assertions.assertNull(wrapper.getPropertyType("nope"));
    }

    @Test
    void shouldUnwrapOriginalMap() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        Assertions.assertSame(map, wrapper.unwrap());
    }

    @Test
    void shouldReflectLiveKeyChanges() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        Assertions.assertFalse(wrapper.containsProperty("newKey"));

        wrapper.setProperty("newKey", "value");

        Assertions.assertTrue(wrapper.containsProperty("newKey"));
        Assertions.assertTrue(wrapper.getPropertyNames().contains("newKey"));
        Assertions.assertEquals(map.size(), wrapper.getPropertySize());
    }

    @Test
    void shouldReturnWrappedClass() {
        Map<String, Object> map = sampleMap();
        MapBeanWrapper wrapper = new MapBeanWrapper(map);

        Assertions.assertEquals(map.getClass(), wrapper.getWrappedClass());
    }
}
