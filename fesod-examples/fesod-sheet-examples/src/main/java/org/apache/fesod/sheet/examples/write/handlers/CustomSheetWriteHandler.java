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
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddressList;

/**
 * Custom {@link SheetWriteHandler} that adds a dropdown validation list to a sheet.
 *
 * <h2>Scenario</h2>
 * <p>You want to add data validation, conditional formatting, or other sheet-level
 * customizations when a worksheet is first created. The {@code SheetWriteHandler}
 * provides a hook into the sheet creation lifecycle.</p>
 *
 * <h2>How It Works</h2>
 * <ol>
 *   <li>Implements {@link SheetWriteHandler#afterSheetCreate(SheetWriteHandlerContext)},
 *       called immediately after a new sheet is created.</li>
 *   <li>Creates a data validation constraint with explicit list values
 *       ({@code "Test1", "Test2"}).</li>
 *   <li>Applies the constraint to cells A2:A3 (rows 1-2, column 0).</li>
 * </ol>
 *
 * <h2>Result</h2>
 * <p>Cells A2 and A3 will show a dropdown arrow. Clicking it reveals the options
 * "Test1" and "Test2". Entering other values triggers a validation error.</p>
 *
 * <h2>Registration</h2>
 * <pre>{@code
 * FesodSheet.write(fileName, DemoData.class)
 *     .registerWriteHandler(new CustomSheetWriteHandler())
 *     .sheet().doWrite(data);
 * }</pre>
 *
 * <h2>Other Use Cases for SheetWriteHandler</h2>
 * <ul>
 *   <li>Setting print areas or page breaks</li>
 *   <li>Freezing panes (freeze rows/columns)</li>
 *   <li>Adding auto-filters</li>
 *   <li>Setting sheet protection</li>
 * </ul>
 *
 * @see SheetWriteHandler
 * @see org.apache.poi.ss.usermodel.DataValidation
 */
@Slf4j
public class CustomSheetWriteHandler implements SheetWriteHandler {

    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        log.info("Sheet {} write successful.", context.getWriteSheetHolder().getSheetNo());

        // Add a dropdown list to the first column of the second and third rows.
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 2, 0, 0);
        DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[] {"Test1", "Test2"});
        DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
        context.getWriteSheetHolder().getSheet().addValidationData(dataValidation);
    }
}
