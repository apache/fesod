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

package org.apache.fesod.sheet.examples.write.handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.BooleanUtils;
import org.apache.fesod.sheet.write.handler.CellWriteHandler;
import org.apache.fesod.sheet.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;

/**
 * Custom {@link CellWriteHandler} that adds a hyperlink to the first header cell.
 *
 * <h2>Scenario</h2>
 * <p>You want to customize individual cells after they are written — for example,
 * adding hyperlinks, conditional formatting, or cell validation. The {@code CellWriteHandler}
 * gives you access to the Apache POI {@link Cell} object for low-level customization.</p>
 *
 * <h2>How It Works</h2>
 * <ol>
 *   <li>Implements {@link CellWriteHandler#afterCellDispose(CellWriteHandlerContext)},
 *       which is called after each cell (both header and content cells).</li>
 *   <li>Checks if the cell is a header cell ({@code context.getHead()}) in the first
 *       column ({@code cell.getColumnIndex() == 0}).</li>
 *   <li>Creates a URL hyperlink pointing to the Fesod GitHub repository and attaches
 *       it to the cell.</li>
 * </ol>
 *
 * <h2>Result</h2>
 * <p>The first column header cell becomes a clickable hyperlink to
 * {@code https://github.com/apache/fesod}.</p>
 *
 * <h2>Registration</h2>
 * <pre>{@code
 * FesodSheet.write(fileName, DemoData.class)
 *     .registerWriteHandler(new CustomCellWriteHandler())
 *     .sheet().doWrite(data);
 * }</pre>
 *
 * <h2>Handler Execution Order</h2>
 * <p>Fesod calls write handlers in registration order. If multiple handlers modify
 * the same cell, later handlers can override earlier ones.</p>
 *
 * @see CellWriteHandler
 * @see org.apache.poi.ss.usermodel.Hyperlink
 */
@Slf4j
public class CustomCellWriteHandler implements CellWriteHandler {

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Cell cell = context.getCell();
        log.info("Row {}, Column {} write completed.", cell.getRowIndex(), cell.getColumnIndex());
        if (BooleanUtils.isTrue(context.getHead()) && cell.getColumnIndex() == 0) {
            CreationHelper createHelper =
                    context.getWriteSheetHolder().getSheet().getWorkbook().getCreationHelper();
            Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress("https://github.com/apache/fesod");
            cell.setHyperlink(hyperlink);
        }
    }
}
