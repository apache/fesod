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

package org.apache.fesod.beans.cglib;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link BeanPropertyScanner}.
 */
class BeanPropertyScannerTest {

    @Test
    void shouldExposeFluentAccessorsBackedByField() {
        PropertyDescriptor[] getters = BeanPropertyScanner.getBeanGetters(FluentBean.class);
        PropertyDescriptor[] setters = BeanPropertyScanner.getBeanSetters(FluentBean.class);

        PropertyDescriptor getter = findPropertyDescriptor(getters, "firstName");
        Assertions.assertNotNull(getter);
        Assertions.assertEquals("firstName", getter.getReadMethod().getName());

        PropertyDescriptor setter = findPropertyDescriptor(setters, "firstName");
        Assertions.assertNotNull(setter);
        Assertions.assertEquals("firstName", setter.getWriteMethod().getName());
    }

    @Test
    void shouldIgnoreFluentAccessorsNotMatchingFieldType() {
        Set<String> getterNames = propertyNames(BeanPropertyScanner.getBeanGetters(MismatchedAccessorBean.class));
        Set<String> setterNames = propertyNames(BeanPropertyScanner.getBeanSetters(MismatchedAccessorBean.class));

        Assertions.assertTrue(getterNames.contains("token"));
        Assertions.assertFalse(setterNames.contains("token"));
    }

    @Test
    void shouldIgnoreMethodsWithoutBackingField() {
        Set<String> getterNames = propertyNames(BeanPropertyScanner.getBeanGetters(HelperBean.class));
        Set<String> setterNames = propertyNames(BeanPropertyScanner.getBeanSetters(HelperBean.class));

        Assertions.assertFalse(getterNames.contains("describe"));
        Assertions.assertFalse(setterNames.contains("rename"));
    }

    @Test
    void shouldPreferStandardAccessorsOverFluentOnes() {
        PropertyDescriptor pd =
                findPropertyDescriptor(BeanPropertyScanner.getBeanGetters(MixedAccessorBean.class), "name");

        Assertions.assertNotNull(pd);
        Assertions.assertEquals("getName", pd.getReadMethod().getName());
    }

    @Test
    void shouldReturnReadOnlyAndWriteOnlyPropertiesOnMatchingSideOnly() {
        Set<String> getterNames = propertyNames(BeanPropertyScanner.getBeanGetters(PartialFluentBean.class));
        Set<String> setterNames = propertyNames(BeanPropertyScanner.getBeanSetters(PartialFluentBean.class));

        Assertions.assertTrue(getterNames.contains("id"));
        Assertions.assertFalse(setterNames.contains("id"));
        Assertions.assertTrue(setterNames.contains("secret"));
        Assertions.assertFalse(getterNames.contains("secret"));
    }

    @Test
    void shouldRecognizeInheritedFluentAccessors() {
        Set<String> getterNames = propertyNames(BeanPropertyScanner.getBeanGetters(ChildBean.class));

        Assertions.assertTrue(getterNames.contains("code"));
        Assertions.assertTrue(getterNames.contains("extra"));
    }

    private static PropertyDescriptor findPropertyDescriptor(PropertyDescriptor[] descriptors, String name) {
        for (PropertyDescriptor pd : descriptors) {
            if (pd.getName().equals(name)) {
                return pd;
            }
        }
        return null;
    }

    private static Set<String> propertyNames(PropertyDescriptor[] descriptors) {
        Set<String> names = new HashSet<>();
        for (PropertyDescriptor pd : descriptors) {
            names.add(pd.getName());
        }
        return names;
    }

    static class FluentBean {
        private String firstName;

        public String firstName() {
            return firstName;
        }

        public FluentBean firstName(String firstName) {
            this.firstName = firstName.toString();
            return this;
        }
    }

    static class MismatchedAccessorBean {
        private String token;

        public String token() {
            return token;
        }

        public void token(StringBuilder token) {
            this.token = token.toString();
        }
    }

    static class HelperBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String describe() {
            return "helper";
        }

        public void rename(String newName) {
            this.name = newName;
        }
    }

    static class MixedAccessorBean {
        private String name;

        public String getName() {
            return name;
        }

        public String name() {
            return name;
        }
    }

    static class PartialFluentBean {
        private Long id;
        private String secret;

        public Long id() {
            return id;
        }

        public void secret(String secret) {
            this.secret = secret;
        }
    }

    static class BaseBean {
        private String code;

        public String code() {
            return code;
        }

        public void code(String code) {
            this.code = code;
        }
    }

    static class ChildBean extends BaseBean {
        private String extra;

        public String extra() {
            return extra;
        }
    }
}
