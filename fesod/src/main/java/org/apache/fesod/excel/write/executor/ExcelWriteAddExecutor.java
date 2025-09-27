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

package org.apache.fesod.excel.write.executor;

import cn.idev.excel.support.cglib.beans.BeanMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.excel.context.WriteContext;
import org.apache.fesod.excel.enums.HeadKindEnum;
import org.apache.fesod.excel.metadata.FieldCache;
import org.apache.fesod.excel.metadata.FieldWrapper;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.BeanMapUtils;
import org.apache.fesod.excel.util.ClassUtils;
import org.apache.fesod.excel.util.FieldUtils;
import org.apache.fesod.excel.util.WorkBookUtil;
import org.apache.fesod.excel.util.WriteHandlerUtils;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.CollectionRowData;
import org.apache.fesod.excel.write.metadata.MapRowData;
import org.apache.fesod.excel.write.metadata.RowData;
import org.apache.fesod.excel.write.metadata.holder.WriteHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * Add the data into excel
 *
 *
 */
public class ExcelWriteAddExecutor extends AbstractExcelWriteExecutor {

    public ExcelWriteAddExecutor(WriteContext writeContext) {
        super(writeContext);
    }

    public void add(Collection<?> data) {
        if (CollectionUtils.isEmpty(data)) {
            data = new ArrayList<>();
        }
        WriteSheetHolder writeSheetHolder = writeContext.writeSheetHolder();
        int newRowIndex = writeSheetHolder.getNewRowIndexAndStartDoWrite();
        if (writeSheetHolder.isNew()
                && !writeSheetHolder.getExcelWriteHeadProperty().hasHead()) {
            newRowIndex += writeContext.currentWriteHolder().relativeHeadRowIndex();
        }
        int relativeRowIndex = 0;
        for (Object oneRowData : data) {
            int lastRowIndex = relativeRowIndex + newRowIndex;
            addOneRowOfDataToExcel(oneRowData, lastRowIndex, relativeRowIndex);
            relativeRowIndex++;
        }
    }

    private void addOneRowOfDataToExcel(Object oneRowData, int rowIndex, int relativeRowIndex) {
        if (oneRowData == null) {
            return;
        }
        RowWriteHandlerContext rowWriteHandlerContext =
                WriteHandlerUtils.createRowWriteHandlerContext(writeContext, rowIndex, relativeRowIndex, Boolean.FALSE);
        WriteHandlerUtils.beforeRowCreate(rowWriteHandlerContext);

        Row row = WorkBookUtil.createRow(writeContext.writeSheetHolder().getSheet(), rowIndex);
        rowWriteHandlerContext.setRow(row);

        WriteHandlerUtils.afterRowCreate(rowWriteHandlerContext);

        if (oneRowData instanceof Collection<?>) {
            addBasicTypeToExcel(new CollectionRowData((Collection<?>) oneRowData), row, rowIndex, relativeRowIndex);
        } else if (oneRowData instanceof Map) {
            addBasicTypeToExcel(new MapRowData((Map<Integer, ?>) oneRowData), row, rowIndex, relativeRowIndex);
        } else {
            addJavaObjectToExcel(oneRowData, row, rowIndex, relativeRowIndex);
        }

        WriteHandlerUtils.afterRowDispose(rowWriteHandlerContext);
    }

    private void addBasicTypeToExcel(RowData oneRowData, Row row, int rowIndex, int relativeRowIndex) {
        if (oneRowData.isEmpty()) {
            return;
        }
        Map<Integer, Head> headMap =
                writeContext.currentWriteHolder().excelWriteHeadProperty().getHeadMap();
        int dataIndex = 0;
        int maxCellIndex = -1;
        for (Map.Entry<Integer, Head> entry : headMap.entrySet()) {
            if (dataIndex >= oneRowData.size()) {
                return;
            }
            int columnIndex = entry.getKey();
            Head head = entry.getValue();
            doAddBasicTypeToExcel(oneRowData, head, row, rowIndex, relativeRowIndex, dataIndex++, columnIndex);
            maxCellIndex = Math.max(maxCellIndex, columnIndex);
        }
        // Finish
        if (dataIndex >= oneRowData.size()) {
            return;
        }
        // If there is data, it is written to the next cell
        maxCellIndex++;

        int size = oneRowData.size() - dataIndex;
        for (int i = 0; i < size; i++) {
            doAddBasicTypeToExcel(oneRowData, null, row, rowIndex, relativeRowIndex, dataIndex++, maxCellIndex++);
        }
    }

    private void doAddBasicTypeToExcel(
            RowData oneRowData,
            Head head,
            Row row,
            int rowIndex,
            int relativeRowIndex,
            int dataIndex,
            int columnIndex) {
        ExcelContentProperty excelContentProperty = ClassUtils.declaredExcelContentProperty(
                null,
                writeContext.currentWriteHolder().excelWriteHeadProperty().getHeadClazz(),
                head == null ? null : head.getFieldName(),
                writeContext.currentWriteHolder());

        CellWriteHandlerContext cellWriteHandlerContext = WriteHandlerUtils.createCellWriteHandlerContext(
                writeContext, row, rowIndex, head, columnIndex, relativeRowIndex, Boolean.FALSE, excelContentProperty);
        WriteHandlerUtils.beforeCellCreate(cellWriteHandlerContext);

        Cell cell = WorkBookUtil.createCell(row, columnIndex);
        cellWriteHandlerContext.setCell(cell);

        WriteHandlerUtils.afterCellCreate(cellWriteHandlerContext);

        cellWriteHandlerContext.setOriginalValue(oneRowData.get(dataIndex));
        cellWriteHandlerContext.setOriginalFieldClass(
                FieldUtils.getFieldClass(cellWriteHandlerContext.getOriginalValue()));
        converterAndSet(cellWriteHandlerContext);

        WriteHandlerUtils.afterCellDispose(cellWriteHandlerContext);
    }

    private void addJavaObjectToExcel(Object oneRowData, Row row, int rowIndex, int relativeRowIndex) {
        WriteHolder currentWriteHolder = writeContext.currentWriteHolder();
        BeanMap beanMap = BeanMapUtils.create(oneRowData);
        // Bean the contains of the Map Key method with poor performance,So to create a keySet here
        Set<String> beanKeySet = new HashSet<>(beanMap.keySet());
        Set<String> beanMapHandledSet = new HashSet<>();
        int maxCellIndex = -1;
        // If it's a class it needs to be cast by type
        if (HeadKindEnum.CLASS.equals(
                writeContext.currentWriteHolder().excelWriteHeadProperty().getHeadKind())) {
            Map<Integer, Head> headMap =
                    writeContext.currentWriteHolder().excelWriteHeadProperty().getHeadMap();
            for (Map.Entry<Integer, Head> entry : headMap.entrySet()) {
                int columnIndex = entry.getKey();
                Head head = entry.getValue();
                String name = head.getFieldName();
                if (!beanKeySet.contains(name)) {
                    continue;
                }

                ExcelContentProperty excelContentProperty = ClassUtils.declaredExcelContentProperty(
                        beanMap, currentWriteHolder.excelWriteHeadProperty().getHeadClazz(), name, currentWriteHolder);
                CellWriteHandlerContext cellWriteHandlerContext = WriteHandlerUtils.createCellWriteHandlerContext(
                        writeContext,
                        row,
                        rowIndex,
                        head,
                        columnIndex,
                        relativeRowIndex,
                        Boolean.FALSE,
                        excelContentProperty);
                WriteHandlerUtils.beforeCellCreate(cellWriteHandlerContext);

                Cell cell = WorkBookUtil.createCell(row, columnIndex);
                cellWriteHandlerContext.setCell(cell);

                WriteHandlerUtils.afterCellCreate(cellWriteHandlerContext);

                cellWriteHandlerContext.setOriginalValue(beanMap.get(name));
                cellWriteHandlerContext.setOriginalFieldClass(head.getField().getType());
                converterAndSet(cellWriteHandlerContext);

                WriteHandlerUtils.afterCellDispose(cellWriteHandlerContext);

                beanMapHandledSet.add(name);
                maxCellIndex = Math.max(maxCellIndex, columnIndex);
            }
        }
        // Finish
        if (beanMapHandledSet.size() == beanMap.size()) {
            return;
        }
        maxCellIndex++;

        FieldCache fieldCache = ClassUtils.declaredFields(oneRowData.getClass(), writeContext.currentWriteHolder());
        for (Map.Entry<Integer, FieldWrapper> entry :
                fieldCache.getSortedFieldMap().entrySet()) {
            FieldWrapper field = entry.getValue();
            String fieldName = field.getFieldName();
            boolean uselessData = !beanKeySet.contains(fieldName) || beanMapHandledSet.contains(fieldName);
            if (uselessData) {
                continue;
            }
            Object value = beanMap.get(fieldName);
            ExcelContentProperty excelContentProperty = ClassUtils.declaredExcelContentProperty(
                    beanMap, currentWriteHolder.excelWriteHeadProperty().getHeadClazz(), fieldName, currentWriteHolder);
            CellWriteHandlerContext cellWriteHandlerContext = WriteHandlerUtils.createCellWriteHandlerContext(
                    writeContext,
                    row,
                    rowIndex,
                    null,
                    maxCellIndex,
                    relativeRowIndex,
                    Boolean.FALSE,
                    excelContentProperty);
            WriteHandlerUtils.beforeCellCreate(cellWriteHandlerContext);

            // If there is data, it is written to the next cell
            Cell cell = WorkBookUtil.createCell(row, maxCellIndex);
            cellWriteHandlerContext.setCell(cell);

            WriteHandlerUtils.afterCellCreate(cellWriteHandlerContext);

            cellWriteHandlerContext.setOriginalValue(value);
            cellWriteHandlerContext.setOriginalFieldClass(FieldUtils.getFieldClass(beanMap, fieldName, value));
            converterAndSet(cellWriteHandlerContext);

            WriteHandlerUtils.afterCellDispose(cellWriteHandlerContext);
            maxCellIndex++;
        }
    }
}
