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

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.fesod.common.beans.BeanWrapper;
import org.apache.fesod.shaded.cglib.beans.BeanMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link CglibBeanWrapper}.
 */
class CglibBeanWrapperTest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class SampleBean {
        private String name;
        private int age;
        private boolean active;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(fluent = true)
    static class FluentSampleBean {
        private String name;
        private int age;
    }

    @Test
    void shouldRejectNullBean() {
        Assertions.assertThatThrownBy(() -> new CglibBeanWrapper(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The bean instance must not be null");
    }

    @Test
    void shouldGetPropertyValue() {
        SampleBean bean = new SampleBean("Jack", 18, true);

        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        Assertions.assertThat(wrapper.getProperty("name")).isEqualTo("Jack");
        Assertions.assertThat(wrapper.getProperty("age")).isEqualTo(18);
        Assertions.assertThat(wrapper.getProperty("active")).isEqualTo(true);
    }

    @Test
    void shouldReturnNullForUnknownProperty() {
        BeanWrapper wrapper = new CglibBeanWrapper(new SampleBean());

        Assertions.assertThat(wrapper.getProperty("nope")).isNull();
    }

    @Test
    void shouldSetProperty() {
        SampleBean bean = new SampleBean();
        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        wrapper.setProperty("name", "Tom");

        Assertions.assertThat(bean.getName()).isEqualTo("Tom");
        Assertions.assertThat(wrapper.getProperty("name")).isEqualTo("Tom");
    }

    @Test
    void shouldSetPrimitiveWithBoxing() {
        SampleBean bean = new SampleBean();
        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        wrapper.setProperty("age", 21);

        Assertions.assertThat(bean.getAge()).isEqualTo(21);
        Assertions.assertThat(wrapper.getProperty("age")).isEqualTo(21);
    }

    @Test
    void shouldBatchSetProperties() {
        SampleBean bean = new SampleBean();
        BeanWrapper wrapper = new CglibBeanWrapper(bean);
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", "Tomas");
        properties.put("age", 30);
        properties.put("active", false);

        wrapper.setProperties(properties);

        Assertions.assertThat(bean.getName()).isEqualTo("Tomas");
        Assertions.assertThat(bean.getAge()).isEqualTo(30);
        Assertions.assertThat(bean.isActive()).isFalse();
    }

    @Test
    void shouldReportPropertyNames() {
        BeanWrapper wrapper = new CglibBeanWrapper(new SampleBean());

        Assertions.assertThat(wrapper.getPropertyNames()).contains("name", "age", "active");
    }

    @Test
    void shouldReportContainsProperty() {
        BeanWrapper wrapper = new CglibBeanWrapper(new SampleBean());

        Assertions.assertThat(wrapper.containsProperty("name")).isTrue();
        Assertions.assertThat(wrapper.containsProperty("age")).isTrue();
        Assertions.assertThat(wrapper.containsProperty("active")).isTrue();
        Assertions.assertThat(wrapper.containsProperty("nope")).isFalse();
    }

    @Test
    void shouldReportPropertySize() {
        BeanWrapper wrapper = new CglibBeanWrapper(new SampleBean());

        Assertions.assertThat(wrapper.getPropertySize())
                .isEqualTo(wrapper.getPropertyNames().size());
    }

    @Test
    void shouldReportPropertyType() {
        BeanWrapper wrapper = new CglibBeanWrapper(new SampleBean());

        Assertions.assertThat(wrapper.getPropertyType("name")).isEqualTo(String.class);
        Assertions.assertThat(wrapper.getPropertyType("age")).isEqualTo(int.class);
        Assertions.assertThat(wrapper.getPropertyType("active")).isEqualTo(boolean.class);
        Assertions.assertThat(wrapper.getPropertyType("nope")).isNull();
    }

    @Test
    void shouldUnwrapOriginalBean() {
        SampleBean bean = new SampleBean();

        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        Assertions.assertThat(wrapper.unwrap()).isSameAs(bean);
    }

    @Test
    void shouldReturnWrappedClass() {
        SampleBean bean = new SampleBean();
        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        Assertions.assertThat(wrapper.getWrappedClass())
                .isEqualTo(SampleBean.class)
                .isEqualTo(bean.getClass());
    }

    @Test
    void shouldReflectExternalBeanMutation() {
        SampleBean bean = new SampleBean("Jackson", 1, false);
        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        bean.setName("Kendall");

        Assertions.assertThat(wrapper.getProperty("name")).isEqualTo("Kendall");
    }

    @Test
    void shouldReadFluentAccessorProperties() {
        BeanWrapper wrapper = new CglibBeanWrapper(new FluentSampleBean("Rose", 20));

        Assertions.assertThat(wrapper.getProperty("name")).isEqualTo("Rose");
        Assertions.assertThat(wrapper.getProperty("age")).isEqualTo(20);
    }

    @Test
    void shouldWriteFluentAccessorProperties() {
        FluentSampleBean bean = new FluentSampleBean();
        BeanWrapper wrapper = new CglibBeanWrapper(bean);

        wrapper.setProperty("name", "Lily");
        wrapper.setProperty("age", 21);

        Assertions.assertThat(bean.name()).isEqualTo("Lily");
        Assertions.assertThat(bean.age()).isEqualTo(21);
    }

    @Test
    void shouldReturnPreviousValueWhenPuttingFluentProperty() {
        EnhancedBeanMapGenerator generator = new EnhancedBeanMapGenerator();
        generator.setBeanClass(FluentSampleBean.class);
        BeanMap map = generator.create().newInstance(new FluentSampleBean("Rose", 20));

        Assertions.assertThat(map.put("name", "Lily")).isEqualTo("Rose");
        Assertions.assertThat(map.put("age", 21)).isEqualTo(20);
    }

    @Test
    void shouldExposeFluentAccessorPropertiesInMetadata() {
        BeanWrapper wrapper = new CglibBeanWrapper(new FluentSampleBean());

        Assertions.assertThat(wrapper.getPropertyNames()).contains("name", "age");
        Assertions.assertThat(wrapper.containsProperty("name")).isTrue();
        Assertions.assertThat(wrapper.containsProperty("nope")).isFalse();
        Assertions.assertThat(wrapper.getPropertyType("name")).isEqualTo(String.class);
    }

    @Test
    void shouldIsolateWrappersOfSameBeanClass() {
        SampleBean first = new SampleBean("Rose", 20, true);
        SampleBean second = new SampleBean("Jack", 40, false);

        BeanWrapper firstWrapper = new CglibBeanWrapper(first);
        BeanWrapper secondWrapper = new CglibBeanWrapper(second);
        firstWrapper.setProperty("name", "Changed");

        Assertions.assertThat(firstWrapper.getProperty("name")).isEqualTo("Changed");
        Assertions.assertThat(secondWrapper.getProperty("name")).isEqualTo("Jack");
        Assertions.assertThat(firstWrapper.unwrap()).isSameAs(first);
        Assertions.assertThat(secondWrapper.unwrap()).isSameAs(second);
    }

    @Test
    void shouldUseFesodCglibNamingPolicy() {
        CglibBeanWrapper.FesodSheetNamingPolicy policy = CglibBeanWrapper.FesodSheetNamingPolicy.INSTANCE;

        Assertions.assertThat(policy.getTag()).isEqualTo("ByFesodCGLIB");
    }
}
