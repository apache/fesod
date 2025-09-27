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

package org.apache.fesod.excel.write;

import java.util.Collection;
import org.apache.fesod.excel.context.WriteContext;
import org.apache.fesod.excel.context.WriteContextImpl;
import org.apache.fesod.excel.enums.WriteTypeEnum;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.FileUtils;
import org.apache.fesod.excel.util.WriteHandlerUtils;
import org.apache.fesod.excel.write.executor.ExcelWriteAddExecutor;
import org.apache.fesod.excel.write.executor.ExcelWriteFillExecutor;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.apache.fesod.excel.write.metadata.WriteWorkbook;
import org.apache.fesod.excel.write.metadata.fill.FillConfig;
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
        } catch (RuntimeException e) {
            finishOnException();
            throw e;
        } catch (Throwable e) {
            finishOnException();
            throw new ExcelGenerateException(e);
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
            // execute callback after the sheet is written
            WriteHandlerUtils.afterSheetDispose(context);
        } catch (RuntimeException e) {
            finishOnException();
            throw e;
        } catch (Throwable e) {
            finishOnException();
            throw new ExcelGenerateException(e);
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
            // execute callback after the sheet is written
            WriteHandlerUtils.afterSheetDispose(context);
        } catch (RuntimeException e) {
            finishOnException();
            throw e;
        } catch (Throwable e) {
            finishOnException();
            throw new ExcelGenerateException(e);
        }
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
