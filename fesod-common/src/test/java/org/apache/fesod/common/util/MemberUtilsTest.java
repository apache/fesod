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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link MemberUtils}
 */
class MemberUtilsTest {

    public static class TestPublicClass {
        public void publicMethod() {}
    }

    static class TestPrivateClass {
        public void publicMethod() {}

        void packageMethod() {}

        private void privateMethod() {}
    }

    @Test
    void test_setAccessibleWorkaround_null() {
        Assertions.assertFalse(MemberUtils.setAccessibleWorkaround(null));
    }

    @Test
    void test_setAccessibleWorkaround_PublicClass_PublicMethod() throws NoSuchMethodException {
        Method method = TestPublicClass.class.getMethod("publicMethod");

        boolean result = MemberUtils.setAccessibleWorkaround(method);
        Assertions.assertFalse(result);
    }

    @Test
    void test_setAccessibleWorkaround_PackagePrivateClass_PrivateMethod() throws NoSuchMethodException {
        Method method = TestPrivateClass.class.getDeclaredMethod("privateMethod");

        boolean result = MemberUtils.setAccessibleWorkaround(method);
        Assertions.assertFalse(result);
    }

    @Test
    void test_setAccessibleWorkaround_PackagePrivateClass_PublicMethod() throws NoSuchMethodException {
        Method method = TestPrivateClass.class.getDeclaredMethod("publicMethod");
        method.setAccessible(false);

        boolean result = MemberUtils.setAccessibleWorkaround(method);
        Assertions.assertTrue(result);
        Assertions.assertTrue(method.isAccessible());
    }

    @Test
    void test_setAccessibleWorkaround_AlreadyAccessible() throws NoSuchMethodException {
        Method method = TestPrivateClass.class.getDeclaredMethod("publicMethod");
        method.setAccessible(true);

        boolean result = MemberUtils.setAccessibleWorkaround(method);
        Assertions.assertFalse(result);
    }

    @Test
    void test_setAccessibleWorkaround_PackageMethod() throws NoSuchMethodException {
        Method method = TestPrivateClass.class.getDeclaredMethod("packageMethod");

        boolean result = MemberUtils.setAccessibleWorkaround(method);
        Assertions.assertFalse(result);
    }

    @Test
    void test_isPackageAccess() {
        Assertions.assertTrue(MemberUtils.isPackageAccess(0));
        Assertions.assertTrue(MemberUtils.isPackageAccess(Modifier.STATIC));
        Assertions.assertTrue(MemberUtils.isPackageAccess(Modifier.FINAL));

        Assertions.assertFalse(MemberUtils.isPackageAccess(Modifier.PUBLIC));
        Assertions.assertFalse(MemberUtils.isPackageAccess(Modifier.PROTECTED));
        Assertions.assertFalse(MemberUtils.isPackageAccess(Modifier.PRIVATE));
        Assertions.assertFalse(MemberUtils.isPackageAccess(Modifier.PUBLIC | Modifier.STATIC));
    }
}
