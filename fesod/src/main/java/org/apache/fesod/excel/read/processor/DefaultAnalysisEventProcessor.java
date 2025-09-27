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

package org.apache.fesod.excel.read.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.enums.CellDataTypeEnum;
import org.apache.fesod.excel.enums.HeadKindEnum;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.exception.ExcelAnalysisException;
import org.apache.fesod.excel.exception.ExcelAnalysisStopException;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.read.metadata.holder.ReadRowHolder;
import org.apache.fesod.excel.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.excel.read.metadata.property.ExcelReadHeadProperty;
import org.apache.fesod.excel.util.BooleanUtils;
import org.apache.fesod.excel.util.ConverterUtils;
import org.apache.fesod.excel.util.StringUtils;

/**
 * Analysis event
 *
 */
@Slf4j
public class DefaultAnalysisEventProcessor implements AnalysisEventProcessor {

    @Override
    public void extra(AnalysisContext analysisContext) {
        dealExtra(analysisContext);
    }

    /**
     * Ends the processing of a row.
     * This method is called after reading a row of data to perform corresponding processing.
     * If the current row is empty and the workbook holder is set to ignore empty rows, then directly return without processing.
     * If the row is not empty or empty rows are not ignored, then call the dealData method to process the data.
     *
     * @param analysisContext Analysis context, containing information about the current analysis, including the type and content of the row.
     */
    @Override
    public void endRow(AnalysisContext analysisContext) {
        // Check if the current row is empty
        if (RowTypeEnum.EMPTY.equals(analysisContext.readRowHolder().getRowType())) {
            // Log debug information if the current row is empty
            if (log.isDebugEnabled()) {
                log.debug("Empty row!");
            }
            // If the workbook holder is set to ignore empty rows, then directly return
            if (analysisContext.readWorkbookHolder().getIgnoreEmptyRow()) {
                return;
            }
        }
        // Call the data processing method
        dealData(analysisContext);
    }

    @Override
    public void endSheet(AnalysisContext analysisContext) {
        ReadSheetHolder readSheetHolder = analysisContext.readSheetHolder();
        if (BooleanUtils.isTrue(readSheetHolder.getEnded())) {
            return;
        }
        readSheetHolder.setEnded(Boolean.TRUE);

        for (ReadListener readListener : analysisContext.currentReadHolder().readListenerList()) {
            readListener.doAfterAllAnalysed(analysisContext);
        }
    }

    private void dealExtra(AnalysisContext analysisContext) {
        for (ReadListener readListener : analysisContext.currentReadHolder().readListenerList()) {
            try {
                readListener.extra(analysisContext.readSheetHolder().getCellExtra(), analysisContext);
            } catch (Exception e) {
                onException(analysisContext, e);
                break;
            }
            if (!readListener.hasNext(analysisContext)) {
                throw new ExcelAnalysisStopException();
            }
        }
    }

    private void onException(AnalysisContext analysisContext, Exception e) {
        for (ReadListener readListenerException :
                analysisContext.currentReadHolder().readListenerList()) {
            try {
                readListenerException.onException(e, analysisContext);
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e1) {
                throw new ExcelAnalysisException(e1.getMessage(), e1);
            }
        }
    }

    private void dealData(AnalysisContext analysisContext) {
        ReadRowHolder readRowHolder = analysisContext.readRowHolder();
        Map<Integer, ReadCellData<?>> cellDataMap = (Map) readRowHolder.getCellMap();
        readRowHolder.setCurrentRowAnalysisResult(cellDataMap);
        int rowIndex = readRowHolder.getRowIndex();
        int currentHeadRowNumber = analysisContext.readSheetHolder().getHeadRowNumber();

        boolean isData = rowIndex >= currentHeadRowNumber;

        // Now is data
        for (ReadListener readListener : analysisContext.currentReadHolder().readListenerList()) {
            try {
                if (isData) {
                    // handle data row
                    readListener.invoke(readRowHolder.getCurrentRowAnalysisResult(), analysisContext);
                } else {
                    // handle data header
                    readListener.invokeHead(cellDataMap, analysisContext);
                }
            } catch (Exception e) {
                onException(analysisContext, e);
                break;
            }
            if (!readListener.hasNext(analysisContext)) {
                throw new ExcelAnalysisStopException();
            }
        }

        // Last head column
        if (!isData && currentHeadRowNumber == rowIndex + 1) {
            buildHead(analysisContext, cellDataMap);
        }
    }

    private void buildHead(AnalysisContext analysisContext, Map<Integer, ReadCellData<?>> cellDataMap) {
        // Rule out empty head, and then take the largest column
        if (MapUtils.isNotEmpty(cellDataMap)) {
            cellDataMap.entrySet().stream()
                    .filter(entry -> CellDataTypeEnum.EMPTY != entry.getValue().getType())
                    .forEach(entry -> analysisContext.readSheetHolder().setMaxNotEmptyDataHeadSize(entry.getKey()));
        }

        if (!HeadKindEnum.CLASS.equals(
                analysisContext.currentReadHolder().excelReadHeadProperty().getHeadKind())) {
            return;
        }
        Map<Integer, String> dataMap = ConverterUtils.convertToStringMap(cellDataMap, analysisContext);
        ExcelReadHeadProperty excelHeadPropertyData =
                analysisContext.readSheetHolder().excelReadHeadProperty();
        Map<Integer, Head> headMapData = excelHeadPropertyData.getHeadMap();
        Map<Integer, Head> tmpHeadMap = new HashMap<Integer, Head>(headMapData.size() * 4 / 3 + 1);
        for (Map.Entry<Integer, Head> entry : headMapData.entrySet()) {
            Head headData = entry.getValue();
            if (headData.getForceIndex() || !headData.getForceName()) {
                tmpHeadMap.put(entry.getKey(), headData);
                continue;
            }
            List<String> headNameList = headData.getHeadNameList();
            String headName = headNameList.get(headNameList.size() - 1);
            for (Map.Entry<Integer, String> stringEntry : dataMap.entrySet()) {
                if (stringEntry == null) {
                    continue;
                }
                String headString = stringEntry.getValue();
                Integer stringKey = stringEntry.getKey();
                if (StringUtils.isEmpty(headString)) {
                    continue;
                }
                if (analysisContext.currentReadHolder().globalConfiguration().getAutoStrip()) {
                    headString = StringUtils.strip(headString);
                } else if (analysisContext
                        .currentReadHolder()
                        .globalConfiguration()
                        .getAutoTrim()) {
                    headString = headString.trim();
                }

                if (headName.equals(headString)) {
                    headData.setColumnIndex(stringKey);
                    tmpHeadMap.put(stringKey, headData);
                    break;
                }
            }
        }
        excelHeadPropertyData.setHeadMap(tmpHeadMap);
    }
}
