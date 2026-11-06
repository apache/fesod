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

package org.apache.fesod.sheet.write.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.write.handler.impl.DefaultRowWriteHandler;
import org.apache.fesod.sheet.write.handler.impl.DimensionWorkbookWriteHandler;
import org.apache.fesod.sheet.write.handler.impl.EscapeHexCellWriteHandler;
import org.apache.fesod.sheet.write.handler.impl.FillStyleCellWriteHandler;
import org.apache.fesod.sheet.write.handler.impl.WriteSheetWorkbookWriteHandler;
import org.apache.fesod.sheet.write.style.DefaultStyle;

/**
 * Loads the built-in {@link WriteHandler} implementations that Fesod registers automatically for
 * every write operation, so that callers do not have to wire them up manually.
 *
 * <p>The set of handlers depends on the target {@link ExcelTypeEnum} (XLSX / XLS / CSV) and on
 * whether the caller wants Fesod's {@link DefaultStyle} to be attached. See
 * {@link #loadDefaultHandler(Boolean, ExcelTypeEnum)} for the exact per-format list.</p>
 */
public class DefaultWriteHandlerLoader {

    /**
     * The default handlers that are shared across formats and are always registered, regardless of
     * the target {@link ExcelTypeEnum}. This list is immutable in intent — callers should not
     * modify it.
     *
     * <p>Contents: {@link DimensionWorkbookWriteHandler}, {@link DefaultRowWriteHandler},
     * {@link FillStyleCellWriteHandler}.</p>
     */
    public static final List<WriteHandler> DEFAULT_WRITE_HANDLER_LIST = new ArrayList<>();

    static {
        DEFAULT_WRITE_HANDLER_LIST.add(new DimensionWorkbookWriteHandler());
        DEFAULT_WRITE_HANDLER_LIST.add(new DefaultRowWriteHandler());
        DEFAULT_WRITE_HANDLER_LIST.add(new FillStyleCellWriteHandler());
    }

    /**
     * Builds the list of default {@link WriteHandler}s to register for a given output format.
     *
     * <p>The returned list depends on {@code excelType}:</p>
     * <ul>
     *   <li>{@link ExcelTypeEnum#XLSX}: {@link DimensionWorkbookWriteHandler},
     *       {@link DefaultRowWriteHandler}, {@link EscapeHexCellWriteHandler},
     *       {@link FillStyleCellWriteHandler}, {@link WriteSheetWorkbookWriteHandler}, and
     *       optionally {@link DefaultStyle}.</li>
     *   <li>{@link ExcelTypeEnum#XLS}: {@link DefaultRowWriteHandler},
     *       {@link FillStyleCellWriteHandler}, {@link WriteSheetWorkbookWriteHandler}, and
     *       optionally {@link DefaultStyle}.</li>
     *   <li>{@link ExcelTypeEnum#CSV}: {@link DefaultRowWriteHandler} and
     *       {@link FillStyleCellWriteHandler}.</li>
     *   <li>Any other value: an empty list.</li>
     * </ul>
     *
     * @param useDefaultStyle whether to append {@link DefaultStyle} so that cells without an
     *                        explicit style still get Fesod's built-in look; has no effect for
     *                        {@link ExcelTypeEnum#CSV}
     * @param excelType       the target spreadsheet format; must not be {@code null}
     * @return a new mutable {@link List} containing the default handlers for the requested format,
     *         in the order they should be registered; never {@code null}, but may be empty when
     *         {@code excelType} is not one of XLSX, XLS or CSV
     */
    public static List<WriteHandler> loadDefaultHandler(Boolean useDefaultStyle, ExcelTypeEnum excelType) {
        List<WriteHandler> handlerList = new ArrayList<>();
        switch (excelType) {
            case XLSX:
                handlerList.add(new DimensionWorkbookWriteHandler());
                handlerList.add(new DefaultRowWriteHandler());
                handlerList.add(new EscapeHexCellWriteHandler());
                handlerList.add(new FillStyleCellWriteHandler());
                handlerList.add(new WriteSheetWorkbookWriteHandler());
                if (useDefaultStyle) {
                    handlerList.add(new DefaultStyle());
                }
                break;
            case XLS:
                handlerList.add(new DefaultRowWriteHandler());
                handlerList.add(new FillStyleCellWriteHandler());
                handlerList.add(new WriteSheetWorkbookWriteHandler());
                if (useDefaultStyle) {
                    handlerList.add(new DefaultStyle());
                }
                break;
            case CSV:
                handlerList.add(new DefaultRowWriteHandler());
                handlerList.add(new FillStyleCellWriteHandler());
                break;
            default:
                break;
        }
        return handlerList;
    }
}
