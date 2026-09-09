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
 * Tests {@link AnnotationMap}.
 */
@Tag(Tags.UNIT)
class AnnotationMapTest {

    @Test
    void shouldExposeAbsentSemanticsOnEmptyMap() {
        Assertions.assertTrue(AnnotationMap.EMPTY.isEmpty());
        Assertions.assertEquals(0, AnnotationMap.EMPTY.size());
        Assertions.assertFalse(AnnotationMap.EMPTY.hasAnnotation(ExcelProperty.class));
        Assertions.assertNull(AnnotationMap.EMPTY.getAttributes(ExcelProperty.class));
        Assertions.assertNull(AnnotationMap.EMPTY.synthesize(ExcelProperty.class));
    }

    @Test
    void shouldKeepFirstOccurrenceForDuplicateTypes() {
        AnnotationMap map = AnnotationMap.builder()
                .putIfAbsent(ExcelProperty.class, attributes(1, "first"))
                .putIfAbsent(ExcelProperty.class, attributes(2, "second"))
                .build();

        // duplicate types are first-occurrence-wins: the second declaration is discarded wholesale
        AnnotationAttributes merged = map.getAttributes(ExcelProperty.class);
        Assertions.assertNotNull(merged);
        Assertions.assertEquals(Integer.valueOf(1), merged.getRequiredAttribute("index", Integer.class));
        Assertions.assertArrayEquals(new String[] {"first"}, merged.getRequiredAttribute("value", String[].class));
    }

    private AnnotationAttributes attributes(int index, String value) {
        Map<String, Object> attrs = new LinkedHashMap<>();
        attrs.put("index", index);
        attrs.put("value", new String[] {value});
        return new AnnotationAttributes(ExcelProperty.class, attrs);
    }
}
