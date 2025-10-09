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

package org.apache.fesod.excel.read.listener;

import cn.idev.excel.support.cglib.beans.BeanMap;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.enums.HeadKindEnum;
import org.apache.fesod.excel.enums.ReadDefaultReturnEnum;
import org.apache.fesod.excel.exception.ExcelDataConvertException;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.excel.read.metadata.property.ExcelReadHeadProperty;
import org.apache.fesod.excel.util.BeanMapUtils;
import org.apache.fesod.excel.util.ClassUtils;
import org.apache.fesod.excel.util.ConverterUtils;
import org.apache.fesod.excel.util.DateUtils;
import org.apache.fesod.excel.util.MapUtils;

/**
 * Convert to the object the user needs
 *
 */
public class ModelBuildEventListener implements IgnoreExceptionReadListener<Map<Integer, ReadCellData<?>>> {

    @Override
    public void invoke(Map<Integer, ReadCellData<?>> cellDataMap, AnalysisContext context) {
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        if (HeadKindEnum.CLASS.equals(readSheetHolder.excelReadHeadProperty().getHeadKind())) {
            context.readRowHolder().setCurrentRowAnalysisResult(buildUserModel(cellDataMap, readSheetHolder, context));
            return;
        }
        context.readRowHolder().setCurrentRowAnalysisResult(buildNoModel(cellDataMap, readSheetHolder, context));
    }

    private Object buildNoModel(
            Map<Integer, ReadCellData<?>> cellDataMap, ReadSheetHolder readSheetHolder, AnalysisContext context) {
        int index = 0;
        Map<Integer, Object> map = MapUtils.newLinkedHashMapWithExpectedSize(cellDataMap.size());
        for (Map.Entry<Integer, ReadCellData<?>> entry : cellDataMap.entrySet()) {
            Integer key = entry.getKey();
            ReadCellData<?> cellData = entry.getValue();
            while (index < key) {
                map.put(index, null);
                index++;
            }
            index++;

            ReadDefaultReturnEnum readDefaultReturn =
                    context.readWorkbookHolder().getReadDefaultReturn();
            if (readDefaultReturn == ReadDefaultReturnEnum.STRING) {
                // string
                map.put(key, (String) ConverterUtils.convertToJavaObject(
                        cellData,
                        null,
                        null,
                        readSheetHolder.converterMap(),
                        context,
                        context.readRowHolder().getRowIndex(),
                        key));
            } else {
                // return ReadCellData
                ReadCellData<?> convertedReadCellData = convertReadCellData(
                        cellData, context.readWorkbookHolder().getReadDefaultReturn(), readSheetHolder, context, key);
                if (readDefaultReturn == ReadDefaultReturnEnum.READ_CELL_DATA) {
                    map.put(key, convertedReadCellData);
                } else {
                    map.put(key, convertedReadCellData.getData());
                }
            }
        }
        int headSize = calculateHeadSize(readSheetHolder);
        while (index <= headSize) {
            map.put(index, null);
            index++;
        }
        return map;
    }

    private ReadCellData convertReadCellData(
            ReadCellData<?> cellData,
            ReadDefaultReturnEnum readDefaultReturn,
            ReadSheetHolder readSheetHolder,
            AnalysisContext context,
            Integer columnIndex) {
        Class<?> classGeneric;
        switch (cellData.getType()) {
            case STRING:
            case DIRECT_STRING:
            case ERROR:
            case EMPTY:
                classGeneric = String.class;
                break;
            case BOOLEAN:
                classGeneric = Boolean.class;
                break;
            case NUMBER:
                DataFormatData dataFormatData = cellData.getDataFormatData();
                if (dataFormatData != null
                        && DateUtils.isADateFormat(dataFormatData.getIndex(), dataFormatData.getFormat())) {
                    classGeneric = LocalDateTime.class;
                } else {
                    classGeneric = BigDecimal.class;
                }
                break;
            default:
                classGeneric = ConverterUtils.defaultClassGeneric;
                break;
        }

        return (ReadCellData) ConverterUtils.convertToJavaObject(
                cellData,
                null,
                ReadCellData.class,
                classGeneric,
                null,
                readSheetHolder.converterMap(),
                context,
                context.readRowHolder().getRowIndex(),
                columnIndex);
    }

    private int calculateHeadSize(ReadSheetHolder readSheetHolder) {
        if (readSheetHolder.excelReadHeadProperty().getHeadMap().size() > 0) {
            return readSheetHolder.excelReadHeadProperty().getHeadMap().size();
        }
        if (readSheetHolder.getMaxNotEmptyDataHeadSize() != null) {
            return readSheetHolder.getMaxNotEmptyDataHeadSize();
        }
        return 0;
    }

    private Object buildUserModel(
            Map<Integer, ReadCellData<?>> cellDataMap, ReadSheetHolder readSheetHolder, AnalysisContext context) {
        ExcelReadHeadProperty excelReadHeadProperty = readSheetHolder.excelReadHeadProperty();
        Object resultModel;
        try {
            resultModel = excelReadHeadProperty.getHeadClazz().newInstance();
        } catch (Exception e) {
            throw new ExcelDataConvertException(
                    context.readRowHolder().getRowIndex(),
                    0,
                    new ReadCellData<>(CellDataTypeEnum.EMPTY),
                    null,
                    "Can not instance class: "
                            + excelReadHeadProperty.getHeadClazz().getName(),
                    e);
        }
        Map<Integer, Head> headMap = excelReadHeadProperty.getHeadMap();
        BeanMap dataMap = BeanMapUtils.create(resultModel);
        for (Map.Entry<Integer, Head> entry : headMap.entrySet()) {
            Integer index = entry.getKey();
            Head head = entry.getValue();
            String fieldName = head.getFieldName();
            if (!cellDataMap.containsKey(index)) {
                continue;
            }
            ReadCellData<?> cellData = cellDataMap.get(index);
            Object value = ConverterUtils.convertToJavaObject(
                    cellData,
                    head.getField(),
                    ClassUtils.declaredExcelContentProperty(
                            dataMap,
                            readSheetHolder.excelReadHeadProperty().getHeadClazz(),
                            fieldName,
                            readSheetHolder),
                    readSheetHolder.converterMap(),
                    context,
                    context.readRowHolder().getRowIndex(),
                    index);
            if (value != null) {
                dataMap.put(fieldName, value);

                // 规避由于实体类 setter 不规范导致无法赋值的问题
                if (dataMap.get(fieldName) == null) {
                    Object bean = dataMap.getBean();
                    try {
                        Field field = bean.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(bean, value);
                    } catch (NoSuchFieldException ignore) {
                        // ignore
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return resultModel;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {}
}
