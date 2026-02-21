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

import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.ReadWorkbookHolder;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests {@link ParameterUtil}
 */
@ExtendWith(MockitoExtension.class)
class ParameterUtilTest {

    @Mock
    private AnalysisContext analysisContext;

    @Mock
    private ReadWorkbookHolder readWorkbookHolder;

    @Mock
    private WriteContext writeContext;

    @Mock
    private WriteWorkbookHolder writeWorkbookHolder;

    @Mock
    private GlobalConfiguration globalConfiguration;

    @Mock
    private ReadSheet readSheet;

    @Mock
    private WriteSheet writeSheet;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(analysisContext.readWorkbookHolder()).thenReturn(readWorkbookHolder);
        Mockito.lenient().when(readWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);

        Mockito.lenient().when(writeContext.writeWorkbookHolder()).thenReturn(writeWorkbookHolder);
        Mockito.lenient().when(writeWorkbookHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
    }

    @Test
    void test_AutoTrim_LocalTrue() {
        Mockito.when(readSheet.getAutoTrim()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoTrimFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoTrim_LocalNull_GlobalTrue() {
        Mockito.when(readSheet.getAutoTrim()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoTrim()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoTrimFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoTrim_LocalNull_GlobalFalse() {
        Mockito.when(readSheet.getAutoTrim()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoTrim()).thenReturn(false);

        Assertions.assertFalse(ParameterUtil.getAutoTrimFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoTrim_LocalFalse_Override_Global() {
        Mockito.when(readSheet.getAutoTrim()).thenReturn(false);
        Mockito.lenient().when(globalConfiguration.getAutoTrim()).thenReturn(true);

        Assertions.assertFalse(ParameterUtil.getAutoTrimFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoStrip_LocalTrue() {
        Mockito.when(readSheet.getAutoStrip()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoStripFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoStrip_LocalNull_GlobalTrue() {
        Mockito.when(readSheet.getAutoStrip()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoStripFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoStrip_AllFalse() {
        Mockito.when(readSheet.getAutoStrip()).thenReturn(false);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(false);

        Assertions.assertFalse(ParameterUtil.getAutoStripFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoStrip_LocalFalse_Cannot_Override_Global() {
        Mockito.when(readSheet.getAutoStrip()).thenReturn(false);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoStripFlag(readSheet, analysisContext));
    }

    @Test
    void test_AutoTrim() {
        Mockito.when(writeSheet.getAutoTrim()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoTrim()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoTrimFlag(writeSheet, writeContext));
    }

    @Test
    void test_AutoTrim_Override() {
        Mockito.when(writeSheet.getAutoTrim()).thenReturn(false);
        Mockito.lenient().when(globalConfiguration.getAutoTrim()).thenReturn(true);

        Assertions.assertFalse(ParameterUtil.getAutoTrimFlag(writeSheet, writeContext));
    }

    @Test
    void test_AutoStrip_Fallback() {
        Mockito.when(writeSheet.getAutoStrip()).thenReturn(null);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoStripFlag(writeSheet, writeContext));
    }

    @Test
    void test_AutoStrip_CannotOverride() {
        Mockito.when(writeSheet.getAutoStrip()).thenReturn(false);
        Mockito.when(globalConfiguration.getAutoStrip()).thenReturn(true);

        Assertions.assertTrue(ParameterUtil.getAutoStripFlag(writeSheet, writeContext));
    }
}
