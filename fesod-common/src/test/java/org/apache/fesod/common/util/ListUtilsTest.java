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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ListUtils}
 */
class ListUtilsTest {

    @Test
    void test_newArrayList() {
        ArrayList<String> list = ListUtils.newArrayList();
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void test_newArrayList_varargs() {
        ArrayList<String> list = ListUtils.newArrayList("a", "b", "c");
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(Arrays.asList("a", "b", "c"), list);

        ArrayList<Integer> emptyList = ListUtils.newArrayList();
        Assertions.assertTrue(emptyList.isEmpty());
    }

    @Test
    void test_newArrayList_varargs_null() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ListUtils.newArrayList((Object[]) null);
        });
    }

    @Test
    void test_newArrayList_iterator() {
        List<Integer> source = Arrays.asList(1, 2, 3);
        ArrayList<Integer> list = ListUtils.newArrayList(source.iterator());

        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(source, list);
    }

    @Test
    void test_newArrayList_iterator_null() {
        ArrayList<Object> list = ListUtils.newArrayList((Iterator<Object>) null);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    void test_newArrayList_iterable_collection() {
        List<String> source = Arrays.asList("apple", "banana");
        ArrayList<String> list = ListUtils.newArrayList(source);

        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(source, list);
    }

    @Test
    void test_newArrayList_iterable_plain() {
        Iterable<Integer> iterable = () -> Arrays.asList(1, 2).iterator();

        ArrayList<Integer> list = ListUtils.newArrayList(iterable);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void test_newArrayList_iterable_null() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ListUtils.newArrayList((Iterable<Object>) null);
        });
    }

    @Test
    void test_newArrayListWithCapacity() {
        ArrayList<Integer> list = ListUtils.newArrayListWithCapacity(10);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ListUtils.newArrayListWithCapacity(-1);
        });
    }

    @Test
    void test_newArrayListWithExpectedSize() {
        ArrayList<Integer> list = ListUtils.newArrayListWithExpectedSize(100);
        Assertions.assertNotNull(list);
        Assertions.assertTrue(list.isEmpty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ListUtils.newArrayListWithExpectedSize(-5);
        });
    }

    @Test
    void test_computeArrayListCapacity() {
        Assertions.assertEquals(5, ListUtils.computeArrayListCapacity(0));
        Assertions.assertEquals(16, ListUtils.computeArrayListCapacity(10));
        Assertions.assertEquals(115, ListUtils.computeArrayListCapacity(100));
    }

    @Test
    void test_checkNotNull() {
        String ref = "hello";
        String result = ListUtils.checkNotNull(ref);
        Assertions.assertEquals("hello", result);

        Assertions.assertThrows(NullPointerException.class, () -> {
            ListUtils.checkNotNull(null);
        });
    }

    @Test
    void test_checkNonnegative() {
        Assertions.assertEquals(0, ListUtils.checkNonnegative(0, "test"));
        Assertions.assertEquals(100, ListUtils.checkNonnegative(100, "test"));

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ListUtils.checkNonnegative(-1, "myField");
        });
        Assertions.assertTrue(exception.getMessage().contains("myField"));
        Assertions.assertTrue(exception.getMessage().contains("-1"));
    }
}
