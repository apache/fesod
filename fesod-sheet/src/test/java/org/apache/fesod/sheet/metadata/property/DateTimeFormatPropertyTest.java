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

package org.apache.fesod.sheet.metadata.property;

import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DateTimeFormatProperty}.
 */
@Tag(Tags.UNIT)
class DateTimeFormatPropertyTest {

    @DateTimeFormat("yyyy-MM-dd")
    private String defaultWindowing;

    @DateTimeFormat(value = "yyyy-MM-dd", use1904windowing = BooleanEnum.TRUE)
    private String explicitTrueWindowing;

    @DateTimeFormat(value = "yyyy-MM-dd", use1904windowing = BooleanEnum.FALSE)
    private String explicitFalseWindowing;

    @Test
    void buildPreservesDefaultWindowingAsNull() throws NoSuchFieldException {
        DateTimeFormat annotation =
                getClass().getDeclaredField("defaultWindowing").getAnnotation(DateTimeFormat.class);

        DateTimeFormatProperty property = DateTimeFormatProperty.build(annotation);

        Assertions.assertNull(property.getUse1904windowing());
    }

    @Test
    void buildPreservesExplicitTrueWindowing() throws NoSuchFieldException {
        DateTimeFormat annotation =
                getClass().getDeclaredField("explicitTrueWindowing").getAnnotation(DateTimeFormat.class);

        DateTimeFormatProperty property = DateTimeFormatProperty.build(annotation);

        Assertions.assertEquals(Boolean.TRUE, property.getUse1904windowing());
    }

    @Test
    void buildPreservesExplicitFalseWindowing() throws NoSuchFieldException {
        DateTimeFormat annotation =
                getClass().getDeclaredField("explicitFalseWindowing").getAnnotation(DateTimeFormat.class);

        DateTimeFormatProperty property = DateTimeFormatProperty.build(annotation);

        Assertions.assertEquals(Boolean.FALSE, property.getUse1904windowing());
    }
}
