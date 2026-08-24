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

package org.apache.fesod.sheet.write;

import java.io.File;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Writing with a head-class that resolves to {@code 0} fields must fail-fast.
 */
@Tag(Tags.WRITE)
class HeadClassNoValidFieldWriteTest extends AbstractExcelTest {

    static class NoFieldData {}

    @Getter
    @Setter
    @ExcelIgnoreUnannotated
    static class UnannotatedOnlyData {
        private String name;
    }

    interface GetterOnlyModel {
        String getName();
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldFailFastWhenHeadClassHasNoFields(ExcelFormat format) throws Exception {
        File file = createTempFile(format);

        IllegalArgumentException exception =
                Assertions.assertThrows(IllegalArgumentException.class, () -> FesodSheet.write(file, NoFieldData.class)
                        .sheet()
                        .doWrite(Collections.singletonList(new NoFieldData())));

        Assertions.assertTrue(exception.getMessage().contains(NoFieldData.class.getName()));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldFailFastWhenAllHeadClassFieldsAreIgnored(ExcelFormat format) throws Exception {
        File file = createTempFile(format);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class, () -> FesodSheet.write(file, UnannotatedOnlyData.class)
                        .sheet()
                        .doWrite(Collections.singletonList(new UnannotatedOnlyData())));

        Assertions.assertTrue(exception.getMessage().contains(UnannotatedOnlyData.class.getName()));
    }

    @ParameterizedTest
    @ExcelFormatSource
    void shouldFailFastWhenHeadClassIsGetterOnlyInterface(ExcelFormat format) throws Exception {
        File file = createTempFile(format);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> FesodSheet.write(file, GetterOnlyModel.class).sheet().doWrite(Collections.emptyList()));

        Assertions.assertTrue(exception.getMessage().contains(GetterOnlyModel.class.getName()));
    }
}
