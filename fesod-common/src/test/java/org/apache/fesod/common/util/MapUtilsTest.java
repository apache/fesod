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

package org.apache.fesod.common.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MapUtils}
 */
class MapUtilsTest {

    @Test
    void test_newHashMap() {
        HashMap<String, Integer> map = MapUtils.newHashMap();
        Assertions.assertNotNull(map);
        Assertions.assertInstanceOf(HashMap.class, map);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void test_newTreeMap() {
        TreeMap<String, String> map = MapUtils.newTreeMap();
        Assertions.assertNotNull(map);
        Assertions.assertInstanceOf(TreeMap.class, map);

        map.put("b", "2");
        map.put("a", "1");
        Assertions.assertEquals("a", map.firstKey());
    }

    @Test
    void test_newLinkedHashMap() {
        LinkedHashMap<Integer, String> map = MapUtils.newLinkedHashMap();
        Assertions.assertNotNull(map);
        Assertions.assertInstanceOf(LinkedHashMap.class, map);

        map.put(2, "two");
        map.put(1, "one");
        map.put(3, "three");

        Integer[] keys = map.keySet().toArray(new Integer[0]);
        Assertions.assertArrayEquals(new Integer[] {2, 1, 3}, keys);
    }

    @Test
    void test_newHashMapWithExpectedSize() {
        HashMap<String, String> map = MapUtils.newHashMapWithExpectedSize(10);
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void test_newHashMapWithExpectedSize_negative() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MapUtils.newHashMapWithExpectedSize(-5);
        });
    }

    @Test
    void test_newLinkedHashMapWithExpectedSize() {
        LinkedHashMap<String, String> map = MapUtils.newLinkedHashMapWithExpectedSize(20);
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void test_capacity_logic() {
        Assertions.assertEquals(1, MapUtils.capacity(0));
        Assertions.assertEquals(2, MapUtils.capacity(1));
        Assertions.assertEquals(3, MapUtils.capacity(2));

        Assertions.assertEquals(5, MapUtils.capacity(3));
        Assertions.assertEquals(17, MapUtils.capacity(12));

        int veryLargeSize = Integer.MAX_VALUE - 100;
        Assertions.assertEquals(Integer.MAX_VALUE, MapUtils.capacity(veryLargeSize));
    }

    @Test
    void test_generics() {
        Map<String, Long> map = MapUtils.newHashMap();
        HashMap<String, Long> hashMap = MapUtils.newHashMap();

        Assertions.assertNotNull(map);
        Assertions.assertNotNull(hashMap);
    }
}
