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

import java.util.Collections;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.ReadWorkbookHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link SheetUtils}
 */
@ExtendWith(MockitoExtension.class)
class SheetUtilsTest {

    @Mock
    private AnalysisContext analysisContext;

    @Mock
    private ReadWorkbookHolder readWorkbookHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @Mock
    private ReadSheet actualSheet;

    @Mock
    private ReadSheet paramSheet;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(analysisContext.readWorkbookHolder()).thenReturn(readWorkbookHolder);
        Mockito.lenient().when(readWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
    }

    @Test
    void test_match_withIgnoreHiddenSheet() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(true);
        Mockito.when(actualSheet.isHidden()).thenReturn(true);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        Assertions.assertNull(result);
    }

    @Test
    void test_match_withReadAll() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        Mockito.when(readWorkbookHolder.getReadAll()).thenReturn(true);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        Assertions.assertEquals(actualSheet, result);
    }

    @Test
    void test_match_withParameterSheetDataList_empty() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        Mockito.when(readWorkbookHolder.getParameterSheetDataList()).thenReturn(Collections.emptyList());

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        Assertions.assertNull(result);
    }

    @Test
    void test_match_ByName_FromSheet_RealObject() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        ReadSheet realParamSheet = new ReadSheet();

        Mockito.when(readWorkbookHolder.getParameterSheetDataList())
                .thenReturn(Collections.singletonList(realParamSheet));

        Mockito.when(actualSheet.getSheetNo()).thenReturn(0);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        // Verify
        Assertions.assertEquals(actualSheet, result);
        Assertions.assertEquals(0, realParamSheet.getSheetNo());
        Mockito.verify(actualSheet).copyBasicParameter(realParamSheet);
    }

    @Test
    void test_match_ByName_AutoStrip_FromSheet() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        Mockito.when(readWorkbookHolder.getParameterSheetDataList()).thenReturn(Collections.singletonList(paramSheet));
        Mockito.when(paramSheet.getSheetNo()).thenReturn(99);
        Mockito.when(actualSheet.getSheetNo()).thenReturn(100);
        Mockito.when(paramSheet.getSheetName()).thenReturn("  SheetA  ");
        Mockito.when(actualSheet.getSheetName()).thenReturn("SheetA");

        Mockito.when(paramSheet.getAutoStrip()).thenReturn(true);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        // verify
        Assertions.assertEquals(actualSheet, result);
        Mockito.verify(actualSheet).copyBasicParameter(paramSheet);
    }

    @Test
    void test_match_ByName_AutoTrim_FromSheet() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        Mockito.when(readWorkbookHolder.getParameterSheetDataList()).thenReturn(Collections.singletonList(paramSheet));

        Mockito.when(paramSheet.getSheetNo()).thenReturn(99);
        Mockito.when(actualSheet.getSheetNo()).thenReturn(100);
        Mockito.when(paramSheet.getSheetName()).thenReturn("  SheetA  ");
        Mockito.when(actualSheet.getSheetName()).thenReturn("SheetA");

        Mockito.when(paramSheet.getAutoStrip()).thenReturn(false);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(false);
        Mockito.when(paramSheet.getAutoTrim()).thenReturn(true);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        // verify
        Assertions.assertEquals(actualSheet, result);
    }

    @Test
    void test_match_ByName_GlobalConfig() {
        // Setup
        Mockito.when(readWorkbookHolder.getIgnoreHiddenSheet()).thenReturn(false);
        Mockito.when(readWorkbookHolder.getParameterSheetDataList()).thenReturn(Collections.singletonList(paramSheet));

        Mockito.when(paramSheet.getSheetNo()).thenReturn(99);
        Mockito.when(actualSheet.getSheetNo()).thenReturn(100);
        Mockito.when(paramSheet.getSheetName()).thenReturn("  SheetA  ");
        Mockito.when(actualSheet.getSheetName()).thenReturn("SheetA");

        Mockito.when(paramSheet.getAutoStrip()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(true);

        // Execute
        ReadSheet result = SheetUtils.match(actualSheet, analysisContext);

        // verify
        Assertions.assertEquals(actualSheet, result);
    }
}
