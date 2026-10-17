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

package org.apache.fesod.sheet.readwrite;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.FesodMarked;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.HeadFontStyle;
import org.apache.fesod.sheet.enums.BooleanEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * End-to-end coverage for composable annotations: {@code @FesodMarked}
 * annotations carrying meta-declared {@link ExcelProperty}, {@code @ColumnWidth}
 * and {@code @HeadFontStyle} values, and {@code @FesodMarked.AliasFor}
 * attribute overrides (explicit, defaulted, and same-name blank) flowing
 * through to the written header cells.
 */
@Tag(Tags.ROUND_TRIP)
class ComposableAnnotationDataTest extends AbstractExcelTest {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty("Meta Head")
    @interface TitledColumn {

        @FesodMarked.AliasFor(annotation = ExcelProperty.class, attribute = "value")
        String title() default "Alias Head";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty("Meta Value Head")
    @interface NamedColumn {

        // Blank attribute() aliases the same-named attribute of the meta-annotation
        @FesodMarked.AliasFor(annotation = ExcelProperty.class)
        String[] value() default {"Same Name Default Head"};
    }

    /**
     * Composes several fesod annotations without any alias, relying on meta-declared values.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @FesodMarked
    @ExcelProperty(value = "Composed Name", index = 0)
    @ColumnWidth(20)
    @HeadFontStyle(bold = BooleanEnum.TRUE, fontHeightInPoints = 20)
    @interface MarkedNameColumn {}

    @Getter
    @Setter
    public static class TitledData {

        @TitledColumn
        private String name;

        @NamedColumn("Explicit Head")
        private String alias;

        @TitledColumn(title = "Custom Head")
        private String customTitle;
    }

    @Getter
    @Setter
    public static class StyledData {

        @MarkedNameColumn
        private String name;
    }

    private static TitledData titledData() {
        TitledData data = new TitledData();
        data.setName("v1");
        data.setAlias("v2");
        data.setCustomTitle("v3");
        return data;
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shouldApplyComposedAnnotationAttributesEndToEnd(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        StyledData data = new StyledData();
        data.setName("v1");

        List<StyledData> result = RoundTripHelper.writeAndRead(file, StyledData.class, Collections.singletonList(data));
        Assertions.assertEquals("v1", result.get(0).getName());

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .hasColumnWidth(0, 20 * 256)
                    .row(0)
                    .cell(0)
                    .hasStringValue("Composed Name")
                    .hasBoldFont(true)
                    .hasFontSize((short) 20);
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shouldApplyExplicitAliasOverrideEndToEnd(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, TitledData.class).sheet().doWrite(Collections.singletonList(titledData()));

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(0).cell(2).hasStringValue("Custom Head");
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shouldApplyAliasDefaultHeadOverMetaDeclaredHead(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, TitledData.class).sheet().doWrite(Collections.singletonList(titledData()));

        // aliased attribute must win over the meta-declared "Meta Head"
        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(0).cell(0).hasStringValue("Alias Head");
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void shouldResolveBlankAliasAttributeToSameNamedAttribute(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        FesodSheet.write(file, TitledData.class).sheet().doWrite(Collections.singletonList(titledData()));

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0).row(0).cell(1).hasStringValue("Explicit Head");
        }
    }
}
