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

package org.apache.fesod.sheet.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link AnnotationAttributes}.
 */
@Tag(Tags.UNIT)
class AnnotationAttributesTest {

    @Test
    void shouldIsolateFromCallerOwnedMaps() {
        Map<String, Object> attrs = new LinkedHashMap<>();
        attrs.put("index", 2);

        AnnotationAttributes attributes = new AnnotationAttributes(ExcelProperty.class, attrs);
        attrs.put("index", 99);

        Assertions.assertEquals(Integer.valueOf(2), attributes.getRequiredAttribute("index", Integer.class));
    }

    @Test
    void shouldEqualByAnnotationTypeAndAttributeValues() {
        AnnotationAttributes near = newAnnotationAttributes(2);
        AnnotationAttributes far = newAnnotationAttributes(2);

        Assertions.assertEquals(near, far);
        Assertions.assertEquals(near.hashCode(), far.hashCode());

        far.put("index", 5);
        Assertions.assertNotEquals(near, far);
    }

    @Test
    void shouldRejectRequiredAttributeWhenAbsent() {
        AnnotationAttributes attributes = newAnnotationAttributes(2);

        Assertions.assertThrows(
                IllegalArgumentException.class, () -> attributes.getRequiredAttribute("order", Integer.class));
    }

    @Test
    void shouldExposeReadOnlyAttributeMapView() {
        AnnotationAttributes attributes = newAnnotationAttributes(2);
        Map<String, Object> view = attributes.asImmutableMap();

        Assertions.assertEquals(2, view.get("index"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> view.put("order", 99));
    }

    private AnnotationAttributes newAnnotationAttributes(int index) {
        Map<String, Object> attrs = new LinkedHashMap<>();
        attrs.put("index", index);
        return new AnnotationAttributes(ExcelProperty.class, attrs);
    }
}
