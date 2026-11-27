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

package org.apache.fesod.sheet.analysis;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadWorkbookHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * The executor-close cleanup step must record its failure like every other step: keeping an earlier failure as the
 * cause and attaching its own as suppressed, instead of overwriting what was already recorded.
 */
class ExecutorCloseFailureRecordTest {

    @Test
    void executorCloseFailureDoesNotOverwriteEarlierCleanupFailure() throws Exception {
        ExcelAnalyserImpl analyser = Mockito.mock(ExcelAnalyserImpl.class);
        Mockito.doCallRealMethod().when(analyser).finish();

        RuntimeException destroyFailure = new RuntimeException("destroy failed");
        ReadCache failingReadCache = Mockito.mock(ReadCache.class);
        Mockito.doThrow(new RuntimeException(destroyFailure))
                .when(failingReadCache)
                .destroy();

        IOException executorCloseFailure = new IOException("executor close failed");
        ExcelReadExecutor failingExecutor = new ExcelReadExecutor() {
            @Override
            public void execute() {}

            @Override
            public List<ReadSheet> sheetList() {
                return Collections.emptyList();
            }

            @Override
            public void close() {
                throw new RuntimeException(executorCloseFailure);
            }
        };

        XlsxReadWorkbookHolder workbookHolder = Mockito.mock(XlsxReadWorkbookHolder.class);
        Mockito.doReturn(failingReadCache).when(workbookHolder).getReadCache();
        Mockito.doReturn(null).when(workbookHolder).getOpcPackage();
        Mockito.doReturn(Boolean.FALSE).when(workbookHolder).getAutoCloseStream();

        AnalysisContext context = Mockito.mock(AnalysisContext.class);
        Mockito.doReturn(workbookHolder).when(context).readWorkbookHolder();

        setField(analyser, "analysisContext", context);
        setField(analyser, "excelReadExecutor", failingExecutor);

        ExcelAnalysisException exception = Assertions.assertThrows(ExcelAnalysisException.class, analyser::finish);

        Throwable cause = exception.getCause();
        Assertions.assertTrue(
                cause.getMessage().contains("destroy failed"),
                "the read-cache destroy failure happened first and must stay the cause, got: " + cause);
        Assertions.assertEquals(
                1, cause.getSuppressed().length, "the executor close failure is attached as suppressed");
        Assertions.assertTrue(
                cause.getSuppressed()[0].getMessage().contains("executor close failed"),
                "the suppressed failure is the executor close failure");
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = ExcelAnalyserImpl.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
