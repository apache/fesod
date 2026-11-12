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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.write;

import java.util.Collection;
import org.apache.fesod.common.util.ThrowableUtils;
import org.apache.fesod.sheet.context.WriteContext;
import org.apache.fesod.sheet.context.WriteContextImpl;
import org.apache.fesod.sheet.enums.WriteTypeEnum;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.executor.ExcelWriteAddExecutor;
import org.apache.fesod.sheet.write.executor.ExcelWriteFillExecutor;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteTable;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.fesod.sheet.write.metadata.fill.FillConfig;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 */
public class ExcelBuilderImpl implements ExcelBuilder {

    private final WriteContext context;
    private ExcelWriteFillExecutor excelWriteFillExecutor;
    private ExcelWriteAddExecutor excelWriteAddExecutor;

    static {
        // Create temporary cache directory at initialization time to avoid POI concurrent write bugs
        FileUtils.createPoiFilesDirectory();
    }

    public ExcelBuilderImpl(WriteWorkbook writeWorkbook) {
        try {
            context = new WriteContextImpl(writeWorkbook);
        } catch (Throwable e) {
            throw finishOnExceptionAndPropagate(e);
        }
    }

    @Override
    public void addContent(Collection<?> data, WriteSheet writeSheet) {
        addContent(data, writeSheet, null);
    }

    @Override
    public void addContent(Collection<?> data, WriteSheet writeSheet, WriteTable writeTable) {
        try {
            context.currentSheet(writeSheet, WriteTypeEnum.ADD);
            context.currentTable(writeTable);
            if (excelWriteAddExecutor == null) {
                excelWriteAddExecutor = new ExcelWriteAddExecutor(context);
            }
            excelWriteAddExecutor.add(data);
        } catch (Throwable e) {
            throw finishOnExceptionAndPropagate(e);
        }
    }

    @Override
    public void fill(Object data, FillConfig fillConfig, WriteSheet writeSheet) {
        try {
            if (context.writeWorkbookHolder().getTempTemplateInputStream() == null) {
                throw new ExcelGenerateException("Calling the 'fill' method must use a template.");
            }
            if (context.writeWorkbookHolder().getExcelType() == ExcelTypeEnum.CSV) {
                throw new ExcelGenerateException("csv does not support filling data.");
            }
            context.currentSheet(writeSheet, WriteTypeEnum.FILL);
            if (excelWriteFillExecutor == null) {
                excelWriteFillExecutor = new ExcelWriteFillExecutor(context);
            }
            excelWriteFillExecutor.fill(data, fillConfig);
        } catch (Throwable e) {
            throw finishOnExceptionAndPropagate(e);
        }
    }

    /**
     * Releases resources and hands {@code e} back to the caller: runtime exceptions and errors propagate as
     * they are, checked exceptions are wrapped in an {@link ExcelGenerateException}. A failure during cleanup
     * never replaces {@code e}; it is attached to it as a suppressed exception.
     *
     * @return the exception to throw when {@code e} is a checked exception; runtime exceptions and errors are
     *         rethrown from inside this method
     */
    private RuntimeException finishOnExceptionAndPropagate(Throwable e) {
        try {
            finishOnException();
        } catch (Throwable cleanupFailure) {
            ThrowableUtils.addSuppressedQuietly(e, cleanupFailure);
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        if (e instanceof Error) {
            throw (Error) e;
        }
        return new ExcelGenerateException(e);
    }

    private void finishOnException() {
        finish(true);
    }

    @Override
    public void finish(boolean onException) {
        if (context != null) {
            context.finish(onException);
        }
    }

    @Override
    public void merge(int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        context.writeSheetHolder().getSheet().addMergedRegion(cra);
    }

    @Override
    public WriteContext writeContext() {
        return context;
    }
}
