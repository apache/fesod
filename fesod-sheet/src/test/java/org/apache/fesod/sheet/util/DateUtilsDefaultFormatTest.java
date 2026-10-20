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

import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.property.DateTimeFormatProperty;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests the default date pattern tiering: {@code @DateTimeFormat} annotation over per-call configuration over the
 * process-wide default, plus the workbook-level snapshot semantics of {@link DateUtils} defaults.
 */
@Tag(Tags.UNIT)
class DateUtilsDefaultFormatTest {

    private static final String ANNOTATION_FORMAT = "yyyy.MM.dd";

    private static final String PARAM_FORMAT = "yyyy/MM/dd";

    private static final String GLOBAL_FORMAT = "yyyyMMdd";

    @AfterEach
    void resetDefaults() {
        DateUtils.resetDefaultDateFormats();
    }

    @Test
    void builtInDefaultsAreExposed() {
        Assertions.assertEquals(DateUtils.DATE_FORMAT_19, DateUtils.getDefaultDateFormat());
        Assertions.assertEquals(DateUtils.DATE_FORMAT_10, DateUtils.getDefaultLocalDateFormat());
        Assertions.assertEquals(DateUtils.DEFAULT_YEAR_FORMAT, DateUtils.getDefaultYearFormat());
    }

    @Test
    void settersRejectBlankPatterns() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.setDefaultDateFormat(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.setDefaultLocalDateFormat(" "));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.setDefaultYearFormat(""));
    }

    @Test
    void resetRestoresBuiltInPatterns() {
        DateUtils.setDefaultDateFormat(PARAM_FORMAT);
        DateUtils.setDefaultLocalDateFormat(PARAM_FORMAT);
        DateUtils.setDefaultYearFormat(PARAM_FORMAT);

        DateUtils.resetDefaultDateFormats();

        Assertions.assertEquals(DateUtils.DATE_FORMAT_19, DateUtils.getDefaultDateFormat());
        Assertions.assertEquals(DateUtils.DATE_FORMAT_10, DateUtils.getDefaultLocalDateFormat());
        Assertions.assertEquals(DateUtils.DEFAULT_YEAR_FORMAT, DateUtils.getDefaultYearFormat());
    }

    @Test
    void annotationWinsOverPerCallConfigurationAndGlobalDefault() {
        GlobalConfiguration globalConfiguration = perCallConfiguration(PARAM_FORMAT);
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultLocalDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultYearFormat(GLOBAL_FORMAT);
        ExcelContentProperty contentProperty = contentProperty(ANNOTATION_FORMAT);

        Assertions.assertEquals(
                ANNOTATION_FORMAT, DateUtils.resolveDateTimeFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(
                ANNOTATION_FORMAT, DateUtils.resolveLocalDateFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(ANNOTATION_FORMAT, DateUtils.resolveYearFormat(contentProperty, globalConfiguration));
    }

    @Test
    void emptyAnnotationFallsThroughToLowerTiers() {
        GlobalConfiguration globalConfiguration = perCallConfiguration(PARAM_FORMAT);
        ExcelContentProperty contentProperty = contentProperty("");

        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveDateTimeFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveLocalDateFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveYearFormat(contentProperty, globalConfiguration));
    }

    @Test
    void perCallConfigurationWinsOverGlobalDefault() {
        GlobalConfiguration globalConfiguration = perCallConfiguration(PARAM_FORMAT);
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultLocalDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultYearFormat(GLOBAL_FORMAT);
        ExcelContentProperty contentProperty = contentProperty(null);

        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveDateTimeFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveLocalDateFormat(contentProperty, globalConfiguration));
        Assertions.assertEquals(PARAM_FORMAT, DateUtils.resolveYearFormat(contentProperty, globalConfiguration));
    }

    @Test
    void globalDefaultIsUsedWithoutAnnotationOrPerCallConfiguration() {
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultLocalDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultYearFormat(GLOBAL_FORMAT);
        ExcelContentProperty contentProperty = contentProperty(null);

        Assertions.assertEquals(
                GLOBAL_FORMAT, DateUtils.resolveDateTimeFormat(contentProperty, new GlobalConfiguration()));
        Assertions.assertEquals(
                GLOBAL_FORMAT, DateUtils.resolveLocalDateFormat(contentProperty, new GlobalConfiguration()));
        Assertions.assertEquals(GLOBAL_FORMAT, DateUtils.resolveYearFormat(contentProperty, new GlobalConfiguration()));
    }

    @Test
    void nullArgumentsResolveToGlobalDefault() {
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultLocalDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultYearFormat(GLOBAL_FORMAT);

        Assertions.assertEquals(GLOBAL_FORMAT, DateUtils.resolveDateTimeFormat(null, null));
        Assertions.assertEquals(GLOBAL_FORMAT, DateUtils.resolveLocalDateFormat(null, null));
        Assertions.assertEquals(GLOBAL_FORMAT, DateUtils.resolveYearFormat(null, null));
    }

    @Test
    void workbookHolderSnapshotsGlobalDefaultOnce() {
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultLocalDateFormat(GLOBAL_FORMAT);
        DateUtils.setDefaultYearFormat(GLOBAL_FORMAT);

        WriteWorkbookHolder workbookHolder = new WriteWorkbookHolder(new WriteWorkbook());

        Assertions.assertEquals(
                GLOBAL_FORMAT, workbookHolder.getGlobalConfiguration().getDefaultDateFormat());
        Assertions.assertEquals(
                GLOBAL_FORMAT, workbookHolder.getGlobalConfiguration().getDefaultLocalDateFormat());
        Assertions.assertEquals(
                GLOBAL_FORMAT, workbookHolder.getGlobalConfiguration().getDefaultYearFormat());

        // The workbook keeps its snapshot; a global change only affects contexts created afterwards.
        DateUtils.setDefaultDateFormat(PARAM_FORMAT);
        WriteSheetHolder sheetHolder = new WriteSheetHolder(new WriteSheet(), workbookHolder);

        Assertions.assertEquals(
                GLOBAL_FORMAT, sheetHolder.getGlobalConfiguration().getDefaultDateFormat());
    }

    @Test
    void workbookParameterOverridesGlobalSnapshot() {
        DateUtils.setDefaultDateFormat(GLOBAL_FORMAT);
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setDefaultDateFormat(PARAM_FORMAT);

        WriteWorkbookHolder workbookHolder = new WriteWorkbookHolder(writeWorkbook);

        Assertions.assertEquals(
                PARAM_FORMAT, workbookHolder.getGlobalConfiguration().getDefaultDateFormat());
    }

    private static ExcelContentProperty contentProperty(String format) {
        ExcelContentProperty contentProperty = new ExcelContentProperty();
        contentProperty.setDateTimeFormatProperty(new DateTimeFormatProperty(format, null));
        return contentProperty;
    }

    private static GlobalConfiguration perCallConfiguration(String format) {
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setDefaultDateFormat(format);
        globalConfiguration.setDefaultLocalDateFormat(format);
        globalConfiguration.setDefaultYearFormat(format);
        return globalConfiguration;
    }
}
