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

package org.apache.fesod.excel.write.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.write.handler.impl.DefaultRowWriteHandler;
import org.apache.fesod.excel.write.handler.impl.DimensionWorkbookWriteHandler;
import org.apache.fesod.excel.write.handler.impl.FillStyleCellWriteHandler;
import org.apache.fesod.excel.write.handler.impl.WriteSheetWorkbookWriteHandler;
import org.apache.fesod.excel.write.style.DefaultStyle;

/**
 * Load default handler
 *
 *
 */
public class DefaultWriteHandlerLoader {

    public static final List<WriteHandler> DEFAULT_WRITE_HANDLER_LIST = new ArrayList<>();

    static {
        DEFAULT_WRITE_HANDLER_LIST.add(new DimensionWorkbookWriteHandler());
        DEFAULT_WRITE_HANDLER_LIST.add(new DefaultRowWriteHandler());
        DEFAULT_WRITE_HANDLER_LIST.add(new FillStyleCellWriteHandler());
    }

    /**
     * Load default handler
     *
     * @return
     */
    public static List<WriteHandler> loadDefaultHandler(Boolean useDefaultStyle, ExcelTypeEnum excelType) {
        List<WriteHandler> handlerList = new ArrayList<>();
        switch (excelType) {
            case XLSX:
                handlerList.add(new DimensionWorkbookWriteHandler());
                handlerList.add(new DefaultRowWriteHandler());
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
