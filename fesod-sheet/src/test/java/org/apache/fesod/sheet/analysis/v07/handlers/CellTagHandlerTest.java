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

package org.apache.fesod.sheet.analysis.v07.handlers;

import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Regression test for <a href="https://github.com/apache/fesod/issues/955">issue #955</a>.
 *
 * <p>A cell whose {@code t} attribute is not a recognized type made {@code buildFromCellType} return
 * {@code null}, which then tripped the {@code ReadCellData} constructor with a confusing
 * {@code IllegalArgumentException: Type can not be null} that named neither the cell nor the attribute.
 * The handler must instead throw an {@link ExcelAnalysisException} naming the invalid type and its location.
 */
@Tag(Tags.UNIT)
class CellTagHandlerTest {

    @Test
    void startElement_throwsDescriptiveError_forUnknownCellType() {
        XlsxReadContext context = Mockito.mock(XlsxReadContext.class);
        XlsxReadSheetHolder sheetHolder = Mockito.mock(XlsxReadSheetHolder.class);
        Mockito.when(context.xlsxReadSheetHolder()).thenReturn(sheetHolder);

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "r", "r", "CDATA", "B4");
        attributes.addAttribute("", "t", "t", "CDATA", "unknown");

        ExcelAnalysisException exception = Assertions.assertThrows(
                ExcelAnalysisException.class, () -> new CellTagHandler().startElement(context, "c", attributes));

        // The message must name the unrecognized type and the exact cell (Excel reference) for diagnostics.
        String message = exception.getMessage();
        Assertions.assertTrue(message.contains("'unknown'"), "should name the unrecognized type: " + message);
        Assertions.assertTrue(message.contains("B4"), "should name the cell reference: " + message);
    }
}
