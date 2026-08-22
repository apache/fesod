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

package org.apache.fesod.sheet.read.listener;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests the field-assignment fallback in {@link ModelBuildEventListener} for models whose mapped
 * property has no standard JavaBeans setter.
 */
@Tag(Tags.ROUND_TRIP)
class ModelBuildEventListenerTest extends AbstractExcelTest {

    @Getter
    public static class GetterOnlyData {
        @ExcelProperty("name")
        public String name;
    }

    public static class InheritedGetterOnlyData extends GetterOnlyData {}

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivateGetterOnlyData {
        @ExcelProperty("name")
        private String name;
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldAssignFieldValueDirectlyWhenSetterIsMissing(ExcelFormat format) throws Exception {
        GetterOnlyData out = new GetterOnlyData();
        out.name = "hello";
        List<GetterOnlyData> result = RoundTripHelper.writeAndRead(
                createTempFile(format), GetterOnlyData.class, Collections.singletonList(out));

        Assertions.assertEquals("hello", result.get(0).getName());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldAssignInheritedFieldValueWhenSetterIsMissing(ExcelFormat format) throws Exception {
        GetterOnlyData out = new GetterOnlyData();
        out.name = "hello";
        List<InheritedGetterOnlyData> result = RoundTripHelper.writeAndRead(
                createTempFile(format),
                GetterOnlyData.class,
                Collections.singletonList(out),
                InheritedGetterOnlyData.class);

        Assertions.assertEquals("hello", result.get(0).getName());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldAssignPrivateFieldValueWhenSetterIsMissing(ExcelFormat format) throws Exception {
        List<PrivateGetterOnlyData> result = RoundTripHelper.writeAndRead(
                createTempFile(format),
                PrivateGetterOnlyData.class,
                Collections.singletonList(new PrivateGetterOnlyData("hello")));

        Assertions.assertEquals("hello", result.get(0).getName());
    }
}
