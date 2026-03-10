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
import org.apache.fesod.sheet.write.handler.RowWriteHandler;
import org.apache.fesod.sheet.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * Custom {@link RowWriteHandler} that adds an Excel comment (note) to a header cell.
 *
 * <h2>Scenario</h2>
 * <p>You want to add hover-able comments/notes to specific cells — for example, adding
 * instructions or descriptions to column headers so end-users understand each column.</p>
 *
 * <h2>How It Works</h2>
 * <ol>
 *   <li>Implements {@link RowWriteHandler#afterRowDispose(RowWriteHandlerContext)},
 *       which is called after each row is written.</li>
 *   <li>Checks if the row is a header row via {@code context.getHead()}.</li>
 *   <li>Creates a drawing patriarch on the sheet and adds a comment anchored
 *       to cell B1 (row 0, column 1).</li>
 * </ol>
 *
 * <h2>Result</h2>
 * <p>The second column header cell will show a small red triangle indicator.
 * Hovering over it displays "Created a comment!".</p>
 *
 * <h2>Registration</h2>
 * <pre>{@code
 * FesodSheet.write(fileName, DemoData.class)
 *     .registerWriteHandler(new CommentWriteHandler())
 *     .sheet().doWrite(data);
 * }</pre>
 *
 * <p><b>Note:</b> This handler uses Apache POI's XSSF-specific classes ({@code XSSFClientAnchor},
 * {@code XSSFRichTextString}), so it only works with {@code .xlsx} format.</p>
 *
 * @see RowWriteHandler
 * @see org.apache.poi.ss.usermodel.Comment
 */
@Slf4j
public class CommentWriteHandler implements RowWriteHandler {

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (BooleanUtils.isTrue(context.getHead())) {
            Sheet sheet = context.getWriteSheetHolder().getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // Create a comment in the first row, second column.
            Comment comment =
                    drawingPatriarch.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 1, 0, (short) 2, 1));
            comment.setString(new XSSFRichTextString("Created a comment!"));
            sheet.getRow(0).getCell(1).setCellComment(comment);
        }
    }
}
