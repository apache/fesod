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

package org.apache.fesod.excel.write.builder;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.fill.FillConfig;

/**
 * Build sheet
 *
 *
 */
public class ExcelWriterSheetBuilder extends AbstractExcelWriterParameterBuilder<ExcelWriterSheetBuilder, WriteSheet> {
    private ExcelWriter excelWriter;
    /**
     * Sheet
     */
    private final WriteSheet writeSheet;

    public ExcelWriterSheetBuilder() {
        this.writeSheet = new WriteSheet();
    }

    public ExcelWriterSheetBuilder(ExcelWriter excelWriter) {
        this.writeSheet = new WriteSheet();
        this.excelWriter = excelWriter;
    }

    /**
     * Starting from 0
     *
     * @param sheetNo
     * @return
     */
    public ExcelWriterSheetBuilder sheetNo(Integer sheetNo) {
        writeSheet.setSheetNo(sheetNo);
        return this;
    }

    public ExcelWriterSheetBuilder sheetNoIfNotNull(Integer sheetNo) {
        if (Objects.nonNull(sheetNo)) {
            writeSheet.setSheetNo(sheetNo);
        }
        return this;
    }

    /**
     * sheet name
     *
     * @param sheetName
     * @return
     */
    public ExcelWriterSheetBuilder sheetName(String sheetName) {
        writeSheet.setSheetName(sheetName);
        return this;
    }

    public ExcelWriterSheetBuilder sheetNameIfNotNull(String sheetName) {
        if (Objects.nonNull(sheetName)) {
            writeSheet.setSheetName(sheetName);
        }
        return this;
    }

    public WriteSheet build() {
        return writeSheet;
    }

    public void doWrite(Collection<?> data) {
        if (excelWriter == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.write().sheet()' to call this method");
        }
        excelWriter.write(data, build());
        excelWriter.finish();
    }

    public void doFill(Object data) {
        doFill(data, null);
    }

    public void doFill(Object data, FillConfig fillConfig) {
        if (excelWriter == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.write().sheet()' to call this method");
        }
        excelWriter.fill(data, fillConfig, build());
        excelWriter.finish();
    }

    public void doWrite(Supplier<Collection<?>> supplier) {
        doWrite(supplier.get());
    }

    public void doFill(Supplier<Object> supplier) {
        doFill(supplier.get());
    }

    public void doFill(Supplier<Object> supplier, FillConfig fillConfig) {
        doFill(supplier.get(), fillConfig);
    }

    public ExcelWriterTableBuilder table() {
        return table(null);
    }

    public ExcelWriterTableBuilder table(Integer tableNo) {
        ExcelWriterTableBuilder excelWriterTableBuilder = new ExcelWriterTableBuilder(excelWriter, build());
        if (tableNo != null) {
            excelWriterTableBuilder.tableNo(tableNo);
        }
        return excelWriterTableBuilder;
    }

    @Override
    protected WriteSheet parameter() {
        return writeSheet;
    }
}
