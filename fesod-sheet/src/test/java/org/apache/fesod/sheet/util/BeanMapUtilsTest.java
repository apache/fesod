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

package org.apache.fesod.sheet.util;

import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BeanMapUtils}
 */
class BeanMapUtilsTest {

    public static class TestUser {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    void test_create_Functionality() {
        TestUser user = new TestUser();
        user.setName("Fesod");
        user.setAge(18);

        BeanMap beanMap = BeanMapUtils.create(user);

        Assertions.assertNotNull(beanMap);
        Assertions.assertEquals("Fesod", beanMap.get("name"));
        Assertions.assertEquals(18, beanMap.get("age"));
        beanMap.put("name", "Fesod");
        Assertions.assertEquals("Fesod", user.getName());
    }

    @Test
    void test_create_NamingPolicy() {
        TestUser user = new TestUser();
        BeanMap beanMap = BeanMapUtils.create(user);

        String generatedClassName = beanMap.getClass().getName();

        Assertions.assertTrue(generatedClassName.contains("ByFesodCGLIB"));
    }

    @Test
    void test_NamingPolicy_tag() {
        BeanMapUtils.FesodSheetNamingPolicy policy = BeanMapUtils.FesodSheetNamingPolicy.INSTANCE;

        Assertions.assertDoesNotThrow(() -> {
            java.lang.reflect.Method getTagMethod = policy.getClass().getDeclaredMethod("getTag");
            getTagMethod.setAccessible(true);
            String tag = (String) getTagMethod.invoke(policy);
            Assertions.assertEquals("ByFesodCGLIB", tag);
        });
    }

    @Test
    void test_create_null() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            BeanMapUtils.create(null);
        });
    }
}
