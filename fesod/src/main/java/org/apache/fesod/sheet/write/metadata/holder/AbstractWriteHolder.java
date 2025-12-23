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

package org.apache.fesod.sheet.write.metadata.holder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fesod.sheet.constant.OrderConstant;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.converters.ConverterKeyBuild;
import org.apache.fesod.sheet.converters.DefaultConverterLoader;
import org.apache.fesod.sheet.enums.HeadKindEnum;
import org.apache.fesod.sheet.enums.HeaderMergeStrategy;
import org.apache.fesod.sheet.event.NotRepeatExecutor;
import org.apache.fesod.sheet.metadata.AbstractHolder;
import org.apache.fesod.sheet.metadata.Head;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.metadata.property.LoopMergeProperty;
import org.apache.fesod.sheet.metadata.property.OnceAbsoluteMergeProperty;
import org.apache.fesod.sheet.metadata.property.RowHeightProperty;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.DefaultWriteHandlerLoader;
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.WorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.WriteHandler;
import org.apache.fesod.sheet.write.handler.chain.CellHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.RowHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.SheetHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.chain.WorkbookHandlerExecutionChain;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.sheet.write.merge.LoopMergeStrategy;
import org.apache.fesod.sheet.write.merge.OnceAbsoluteMergeStrategy;
import org.apache.fesod.sheet.write.metadata.WriteBasicParameter;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.property.ExcelWriteHeadProperty;
import org.apache.fesod.sheet.write.style.AbstractVerticalCellStyleStrategy;
import org.apache.fesod.sheet.write.style.column.AbstractHeadColumnWidthStyleStrategy;
import org.apache.fesod.sheet.write.style.row.SimpleRowHeightStyleStrategy;

/**
 * Write holder configuration
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public abstract class AbstractWriteHolder extends AbstractHolder implements WriteHolder {
    /**
     * Need Head
     */
    private Boolean needHead;
    /**
     * Writes the head relative to the existing contents of the sheet. Indexes are zero-based.
     */
    private Integer relativeHeadRowIndex;
    /**
     * Excel head property
     */
    private ExcelWriteHeadProperty excelWriteHeadProperty;
    /**
     * Use the default style.Default is true.
     */
    private Boolean useDefaultStyle;
    /**
     * Whether to automatically merge headers.Default is true.
     */
    private Boolean automaticMergeHead;
    /**
     * Header merge strategy.
     * If null, the behavior is determined by {@link #automaticMergeHead} for backward compatibility.
     */
    private HeaderMergeStrategy headerMergeStrategy;

    /**
     * Ignore the custom columns.
     */
    private Collection<Integer> excludeColumnIndexes;
    /**
     * Ignore the custom columns.
     */
    private Collection<String> excludeColumnFieldNames;
    /**
     * Only output the custom columns.
     */
    private Collection<Integer> includeColumnIndexes;
    /**
     * Only output the custom columns.
     */
    private Collection<String> includeColumnFieldNames;

    /**
     * Data will be ordered by {@link #includeColumnFieldNames} or {@link #includeColumnIndexes}.
     * <p>
     * Default is {@code false}.
     */
    private Boolean orderByIncludeColumn;

    /**
     * Write handler
     */
    private List<WriteHandler> writeHandlerList;

    /**
     * Execute the workbook handler chain
     * Created in the sheet in the workbook interceptors will not be executed because the workbook to
     * create an event long past. So when initializing sheet, supplementary workbook event.
     */
    public WorkbookHandlerExecutionChain ownWorkbookHandlerExecutionChain;
    /**
     * Execute the sheet handler chain
     * Created in the sheet in the workbook interceptors will not be executed because the workbook to
     * create an event long past. So when initializing sheet, supplementary workbook event.
     */
    public SheetHandlerExecutionChain ownSheetHandlerExecutionChain;

    /**
     * Execute the workbook handler chain
     */
    public WorkbookHandlerExecutionChain workbookHandlerExecutionChain;
    /**
     * Execute the sheet handler chain
     */
    public SheetHandlerExecutionChain sheetHandlerExecutionChain;

    /**
     * Execute the row handler chain
     */
    public RowHandlerExecutionChain rowHandlerExecutionChain;

    /**
     * Execute the cell handler chain
     */
    public CellHandlerExecutionChain cellHandlerExecutionChain;

    public AbstractWriteHolder(WriteBasicParameter writeBasicParameter, AbstractWriteHolder parentAbstractWriteHolder) {
        super(writeBasicParameter, parentAbstractWriteHolder);

        if (writeBasicParameter.getUseScientificFormat() != null) {
            throw new UnsupportedOperationException("Currently does not support setting useScientificFormat.");
        }

        if (writeBasicParameter.getNeedHead() == null) {
            if (parentAbstractWriteHolder == null) {
                this.needHead = Boolean.TRUE;
            } else {
                this.needHead = parentAbstractWriteHolder.getNeedHead();
            }
        } else {
            this.needHead = writeBasicParameter.getNeedHead();
        }

        if (writeBasicParameter.getRelativeHeadRowIndex() == null) {
            if (parentAbstractWriteHolder == null) {
                this.relativeHeadRowIndex = 0;
            } else {
                this.relativeHeadRowIndex = parentAbstractWriteHolder.getRelativeHeadRowIndex();
            }
        } else {
            this.relativeHeadRowIndex = writeBasicParameter.getRelativeHeadRowIndex();
        }

        if (writeBasicParameter.getUseDefaultStyle() == null) {
            if (parentAbstractWriteHolder == null) {
                this.useDefaultStyle = Boolean.TRUE;
            } else {
                this.useDefaultStyle = parentAbstractWriteHolder.getUseDefaultStyle();
            }
        } else {
            this.useDefaultStyle = writeBasicParameter.getUseDefaultStyle();
        }

        if (writeBasicParameter.getAutomaticMergeHead() == null) {
            if (parentAbstractWriteHolder == null) {
                this.automaticMergeHead = Boolean.TRUE;
            } else {
                this.automaticMergeHead = parentAbstractWriteHolder.getAutomaticMergeHead();
            }
        } else {
            this.automaticMergeHead = writeBasicParameter.getAutomaticMergeHead();
        }

        if (writeBasicParameter.getHeaderMergeStrategy() == null) {
            if (parentAbstractWriteHolder == null) {
                // Backward compatibility: if headerMergeStrategy is not set, use automaticMergeHead
                this.headerMergeStrategy = null;
            } else {
                this.headerMergeStrategy = parentAbstractWriteHolder.getHeaderMergeStrategy();
            }
        } else {
            this.headerMergeStrategy = writeBasicParameter.getHeaderMergeStrategy();
        }

        if (writeBasicParameter.getExcludeColumnFieldNames() == null && parentAbstractWriteHolder != null) {
            this.excludeColumnFieldNames = parentAbstractWriteHolder.getExcludeColumnFieldNames();
        } else {
            this.excludeColumnFieldNames = writeBasicParameter.getExcludeColumnFieldNames();
        }
        if (writeBasicParameter.getExcludeColumnIndexes() == null && parentAbstractWriteHolder != null) {
            this.excludeColumnIndexes = parentAbstractWriteHolder.getExcludeColumnIndexes();
        } else {
            this.excludeColumnIndexes = writeBasicParameter.getExcludeColumnIndexes();
        }
        if (writeBasicParameter.getIncludeColumnFieldNames() == null && parentAbstractWriteHolder != null) {
            this.includeColumnFieldNames = parentAbstractWriteHolder.getIncludeColumnFieldNames();
        } else {
            this.includeColumnFieldNames = writeBasicParameter.getIncludeColumnFieldNames();
        }

        if (writeBasicParameter.getOrderByIncludeColumn() == null) {
            if (parentAbstractWriteHolder == null) {
                this.orderByIncludeColumn = Boolean.FALSE;
            } else {
                this.orderByIncludeColumn = parentAbstractWriteHolder.getOrderByIncludeColumn();
            }
        } else {
            this.orderByIncludeColumn = writeBasicParameter.getOrderByIncludeColumn();
        }

        if (writeBasicParameter.getIncludeColumnIndexes() == null && parentAbstractWriteHolder != null) {
            this.includeColumnIndexes = parentAbstractWriteHolder.getIncludeColumnIndexes();
        } else {
            this.includeColumnIndexes = writeBasicParameter.getIncludeColumnIndexes();
        }

        // Initialization property
        this.excelWriteHeadProperty = new ExcelWriteHeadProperty(this, getClazz(), getHead());

        // Set converterMap
        if (parentAbstractWriteHolder == null) {
            setConverterMap(new HashMap<>(DefaultConverterLoader.loadDefaultWriteConverter()));
        } else {
            setConverterMap(new HashMap<>(parentAbstractWriteHolder.getConverterMap()));
        }
        if (writeBasicParameter.getCustomConverterList() != null
                && !writeBasicParameter.getCustomConverterList().isEmpty()) {
            for (Converter<?> converter : writeBasicParameter.getCustomConverterList()) {
                getConverterMap()
                        .put(
                                ConverterKeyBuild.buildKey(
                                        converter.supportJavaTypeKey(), converter.supportExcelTypeKey()),
                                converter);
            }
        }
    }

    protected void initHandler(WriteBasicParameter writeBasicParameter, AbstractWriteHolder parentAbstractWriteHolder) {
        // Set writeHandlerMap
        List<WriteHandler> handlerList = new ArrayList<>();

        // Initialization Annotation
        initAnnotationConfig(handlerList, writeBasicParameter);

        if (writeBasicParameter.getCustomWriteHandlerList() != null
                && !writeBasicParameter.getCustomWriteHandlerList().isEmpty()) {
            handlerList.addAll(writeBasicParameter.getCustomWriteHandlerList());
        }
        sortAndClearUpHandler(handlerList, true);

        if (parentAbstractWriteHolder != null) {
            if (CollectionUtils.isNotEmpty(parentAbstractWriteHolder.getWriteHandlerList())) {
                handlerList.addAll(parentAbstractWriteHolder.getWriteHandlerList());
            }
        } else {
            if (this instanceof WriteWorkbookHolder) {
                handlerList.addAll(DefaultWriteHandlerLoader.loadDefaultHandler(
                        useDefaultStyle, ((WriteWorkbookHolder) this).getExcelType()));
            }
        }
        sortAndClearUpHandler(handlerList, false);
    }

    protected void initAnnotationConfig(List<WriteHandler> handlerList, WriteBasicParameter writeBasicParameter) {
        if (!HeadKindEnum.CLASS.equals(getExcelWriteHeadProperty().getHeadKind())) {
            return;
        }
        if (writeBasicParameter.getClazz() == null) {
            return;
        }
        Map<Integer, Head> headMap = getExcelWriteHeadProperty().getHeadMap();
        boolean hasColumnWidth = false;

        for (Head head : headMap.values()) {
            if (head.getColumnWidthProperty() != null) {
                hasColumnWidth = true;
            }
            dealLoopMerge(handlerList, head);
        }

        if (hasColumnWidth) {
            dealColumnWidth(handlerList);
        }

        dealStyle(handlerList);
        dealRowHigh(handlerList);
        dealOnceAbsoluteMerge(handlerList);
    }

    private void dealStyle(List<WriteHandler> handlerList) {
        WriteHandler styleStrategy = new AbstractVerticalCellStyleStrategy() {
            @Override
            public int order() {
                return OrderConstant.ANNOTATION_DEFINE_STYLE;
            }

            @Override
            protected WriteCellStyle headCellStyle(CellWriteHandlerContext context) {
                Head head = context.getHeadData();
                if (head == null) {
                    return null;
                }
                return WriteCellStyle.build(head.getHeadStyleProperty(), head.getHeadFontProperty());
            }

            @Override
            protected WriteCellStyle contentCellStyle(CellWriteHandlerContext context) {
                ExcelContentProperty excelContentProperty = context.getExcelContentProperty();
                return WriteCellStyle.build(
                        excelContentProperty.getContentStyleProperty(), excelContentProperty.getContentFontProperty());
            }
        };
        handlerList.add(styleStrategy);
    }

    private void dealLoopMerge(List<WriteHandler> handlerList, Head head) {
        LoopMergeProperty loopMergeProperty = head.getLoopMergeProperty();
        if (loopMergeProperty == null) {
            return;
        }
        handlerList.add(new LoopMergeStrategy(loopMergeProperty, head.getColumnIndex()));
    }

    private void dealOnceAbsoluteMerge(List<WriteHandler> handlerList) {
        OnceAbsoluteMergeProperty onceAbsoluteMergeProperty =
                getExcelWriteHeadProperty().getOnceAbsoluteMergeProperty();
        if (onceAbsoluteMergeProperty == null) {
            return;
        }
        handlerList.add(new OnceAbsoluteMergeStrategy(onceAbsoluteMergeProperty));
    }

    private void dealRowHigh(List<WriteHandler> handlerList) {
        RowHeightProperty headRowHeightProperty = getExcelWriteHeadProperty().getHeadRowHeightProperty();
        RowHeightProperty contentRowHeightProperty = getExcelWriteHeadProperty().getContentRowHeightProperty();
        if (headRowHeightProperty == null && contentRowHeightProperty == null) {
            return;
        }
        Short headRowHeight = null;
        if (headRowHeightProperty != null) {
            headRowHeight = headRowHeightProperty.getHeight();
        }
        Short contentRowHeight = null;
        if (contentRowHeightProperty != null) {
            contentRowHeight = contentRowHeightProperty.getHeight();
        }
        handlerList.add(new SimpleRowHeightStyleStrategy(headRowHeight, contentRowHeight));
    }

    private void dealColumnWidth(List<WriteHandler> handlerList) {
        WriteHandler columnWidthStyleStrategy = new AbstractHeadColumnWidthStyleStrategy() {
            @Override
            protected Integer columnWidth(Head head, Integer columnIndex) {
                if (head == null) {
                    return null;
                }
                if (head.getColumnWidthProperty() != null) {
                    return head.getColumnWidthProperty().getWidth();
                }
                return null;
            }
        };
        handlerList.add(columnWidthStyleStrategy);
    }

    protected void sortAndClearUpHandler(List<WriteHandler> handlerList, boolean runOwn) {
        // sort
        Map<Integer, List<WriteHandler>> orderExcelWriteHandlerMap = new TreeMap<>();
        for (WriteHandler handler : handlerList) {
            int order = handler.order();
            if (orderExcelWriteHandlerMap.containsKey(order)) {
                orderExcelWriteHandlerMap.get(order).add(handler);
            } else {
                List<WriteHandler> tempHandlerList = new ArrayList<>();
                tempHandlerList.add(handler);
                orderExcelWriteHandlerMap.put(order, tempHandlerList);
            }
        }
        // clean up
        Set<String> alreadyExistedHandlerSet = new HashSet<>();
        List<WriteHandler> cleanUpHandlerList = new ArrayList<>();
        for (Map.Entry<Integer, List<WriteHandler>> entry : orderExcelWriteHandlerMap.entrySet()) {
            for (WriteHandler handler : entry.getValue()) {
                if (handler instanceof NotRepeatExecutor) {
                    String uniqueValue = ((NotRepeatExecutor) handler).uniqueValue();
                    if (alreadyExistedHandlerSet.contains(uniqueValue)) {
                        continue;
                    }
                    alreadyExistedHandlerSet.add(uniqueValue);
                }
                cleanUpHandlerList.add(handler);
            }
        }

        // build chain
        if (!runOwn) {
            this.writeHandlerList = new ArrayList<>();
        }
        for (WriteHandler writeHandler : cleanUpHandlerList) {
            buildChain(writeHandler, runOwn);
        }
    }

    protected void buildChain(WriteHandler writeHandler, boolean runOwn) {
        if (writeHandler instanceof CellWriteHandler) {
            if (!runOwn) {
                if (cellHandlerExecutionChain == null) {
                    cellHandlerExecutionChain = new CellHandlerExecutionChain((CellWriteHandler) writeHandler);
                } else {
                    cellHandlerExecutionChain.addLast((CellWriteHandler) writeHandler);
                }
            }
        }
        if (writeHandler instanceof RowWriteHandler) {
            if (!runOwn) {
                if (rowHandlerExecutionChain == null) {
                    rowHandlerExecutionChain = new RowHandlerExecutionChain((RowWriteHandler) writeHandler);
                } else {
                    rowHandlerExecutionChain.addLast((RowWriteHandler) writeHandler);
                }
            }
        }
        if (writeHandler instanceof SheetWriteHandler) {
            if (!runOwn) {
                if (sheetHandlerExecutionChain == null) {
                    sheetHandlerExecutionChain = new SheetHandlerExecutionChain((SheetWriteHandler) writeHandler);
                } else {
                    sheetHandlerExecutionChain.addLast((SheetWriteHandler) writeHandler);
                }
            } else {
                if (ownSheetHandlerExecutionChain == null) {
                    ownSheetHandlerExecutionChain = new SheetHandlerExecutionChain((SheetWriteHandler) writeHandler);
                } else {
                    ownSheetHandlerExecutionChain.addLast((SheetWriteHandler) writeHandler);
                }
            }
        }
        if (writeHandler instanceof WorkbookWriteHandler) {
            if (!runOwn) {
                if (workbookHandlerExecutionChain == null) {
                    workbookHandlerExecutionChain =
                            new WorkbookHandlerExecutionChain((WorkbookWriteHandler) writeHandler);
                } else {
                    workbookHandlerExecutionChain.addLast((WorkbookWriteHandler) writeHandler);
                }
            } else {
                if (ownWorkbookHandlerExecutionChain == null) {
                    ownWorkbookHandlerExecutionChain =
                            new WorkbookHandlerExecutionChain((WorkbookWriteHandler) writeHandler);
                } else {
                    ownWorkbookHandlerExecutionChain.addLast((WorkbookWriteHandler) writeHandler);
                }
            }
        }
        if (!runOwn) {
            this.writeHandlerList.add(writeHandler);
        }
    }

    @Override
    public boolean ignore(String fieldName, Integer columnIndex) {
        if (fieldName != null) {
            if (includeColumnFieldNames != null && !includeColumnFieldNames.contains(fieldName)) {
                return true;
            }
            if (excludeColumnFieldNames != null && excludeColumnFieldNames.contains(fieldName)) {
                return true;
            }
        }
        if (columnIndex != null) {
            if (includeColumnIndexes != null && !includeColumnIndexes.contains(columnIndex)) {
                return true;
            }
            if (excludeColumnIndexes != null && excludeColumnIndexes.contains(columnIndex)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ExcelWriteHeadProperty excelWriteHeadProperty() {
        return getExcelWriteHeadProperty();
    }

    @Override
    public boolean needHead() {
        return getNeedHead();
    }

    @Override
    public int relativeHeadRowIndex() {
        return getRelativeHeadRowIndex();
    }

    @Override
    public boolean automaticMergeHead() {
        return getAutomaticMergeHead();
    }

    @Override
    public HeaderMergeStrategy headerMergeStrategy() {
        // Backward compatibility: if headerMergeStrategy is null, determine based on automaticMergeHead
        if (headerMergeStrategy == null) {
            return automaticMergeHead ? HeaderMergeStrategy.AUTO : HeaderMergeStrategy.NONE;
        }
        return headerMergeStrategy;
    }

    @Override
    public boolean orderByIncludeColumn() {
        return getOrderByIncludeColumn();
    }

    @Override
    public Collection<Integer> includeColumnIndexes() {
        return getIncludeColumnIndexes();
    }

    @Override
    public Collection<String> includeColumnFieldNames() {
        return getIncludeColumnFieldNames();
    }

    @Override
    public Collection<Integer> excludeColumnIndexes() {
        return getExcludeColumnIndexes();
    }

    @Override
    public Collection<String> excludeColumnFieldNames() {
        return getExcludeColumnFieldNames();
    }
}
