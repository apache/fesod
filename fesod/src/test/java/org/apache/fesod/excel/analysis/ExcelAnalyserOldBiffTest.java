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

package org.apache.fesod.excel.analysis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Collections;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for handling very old XLS (e.g., BIFF2) gracefully.
 */
class ExcelAnalyserOldBiffTest {

    /**
     * Given a BIFF2-like minimal input (from fuzz crash seed Base64: CQAE),
     * ExcelAnalyserImpl should not throw; it should fall back to a no-op executor.
     */
    @Test
    void chooseExecutor_shouldNoop_onOldBiffBytes_stream() {
        byte[] seed = Base64.getDecoder().decode("CQAE");
        InputStream in = new ByteArrayInputStream(seed);

        ReadWorkbook rw = new ReadWorkbook();
        rw.setInputStream(in);
        // Force XLS branch so chooseExcelExecutor will attempt POIFS construction
        rw.setExcelType(ExcelTypeEnum.XLS);

        ExcelAnalyserImpl analyser = new ExcelAnalyserImpl(rw);
        // analysis should not throw even if sheets list is empty when readAll=true
        Assertions.assertDoesNotThrow(() -> analyser.analysis(Collections.emptyList(), true));
        // Noop executor should present empty sheet list
        Assertions.assertTrue(analyser.excelExecutor().sheetList().isEmpty());
        // Analysis context should be XLS (fallback context)
        Assertions.assertEquals(
                ExcelTypeEnum.XLS,
                analyser.analysisContext().readWorkbookHolder().getExcelType());
        Assertions.assertTrue(
                analyser.excelExecutor() instanceof ExcelAnalyserImpl.NoopExcelReadExecutor,
                "Executor should be NoopExcelReadExecutor for old BIFF");
    }

    /**
     * Same as above but via File path to cover the other constructor branch.
     */
    @Test
    void chooseExecutor_shouldNoop_onOldBiffBytes_file(@TempDir Path tmp) throws Exception {
        byte[] seed = Base64.getDecoder().decode("CQAE");
        Path f = tmp.resolve("old_biff_seed.xls");
        Files.write(f, seed);

        ReadWorkbook rw = new ReadWorkbook();
        rw.setFile(f.toFile());
        // Force XLS branch
        rw.setExcelType(ExcelTypeEnum.XLS);

        ExcelAnalyserImpl analyser = new ExcelAnalyserImpl(rw);
        Assertions.assertDoesNotThrow(() -> analyser.analysis(Collections.emptyList(), true));
        Assertions.assertTrue(analyser.excelExecutor().sheetList().isEmpty());
        Assertions.assertEquals(
                ExcelTypeEnum.XLS,
                analyser.analysisContext().readWorkbookHolder().getExcelType());
        Assertions.assertTrue(
                analyser.excelExecutor() instanceof ExcelAnalyserImpl.NoopExcelReadExecutor,
                "Executor should be NoopExcelReadExecutor for old BIFF");
    }
}
