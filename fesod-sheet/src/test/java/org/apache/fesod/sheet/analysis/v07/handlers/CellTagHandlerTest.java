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
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadWorkbookHolder;
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
 *
 * <p>Also covers <a href="https://github.com/apache/fesod/issues/355">issue #355</a>: a malformed
 * {@code s} (style index) attribute used to abort the whole file read via a bare
 * {@code Integer.parseInt}. The handler must fall back to the default format index instead.
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

    @Test
    void startElement_fallsBackToDefaultFormat_forMalformedStyleIndex() {
        XlsxReadContext context = Mockito.mock(XlsxReadContext.class);
        XlsxReadSheetHolder sheetHolder = Mockito.mock(XlsxReadSheetHolder.class);
        XlsxReadWorkbookHolder workbookHolder = Mockito.mock(XlsxReadWorkbookHolder.class);
        Mockito.when(context.xlsxReadSheetHolder()).thenReturn(sheetHolder);
        Mockito.when(context.xlsxReadWorkbookHolder()).thenReturn(workbookHolder);
        Mockito.when(sheetHolder.getTempCellData()).thenReturn(new ReadCellData<>(CellDataTypeEnum.NUMBER));

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "r", "r", "CDATA", "B4");
        attributes.addAttribute("", "t", "t", "CDATA", "n");
        attributes.addAttribute("", "s", "s", "CDATA", "abc");

        Assertions.assertDoesNotThrow(
                () -> new CellTagHandler().startElement(context, "c", attributes),
                "a malformed style index must not abort the read");

        // The style lookup must fall back to the default format index (0) instead of parsing "abc".
        Mockito.verify(workbookHolder).dataFormatData(0);
    }

    @Test
    void startElement_usesStyleIndex_whenNumeric() {
        XlsxReadContext context = Mockito.mock(XlsxReadContext.class);
        XlsxReadSheetHolder sheetHolder = Mockito.mock(XlsxReadSheetHolder.class);
        XlsxReadWorkbookHolder workbookHolder = Mockito.mock(XlsxReadWorkbookHolder.class);
        Mockito.when(context.xlsxReadSheetHolder()).thenReturn(sheetHolder);
        Mockito.when(context.xlsxReadWorkbookHolder()).thenReturn(workbookHolder);
        Mockito.when(sheetHolder.getTempCellData()).thenReturn(new ReadCellData<>(CellDataTypeEnum.NUMBER));

        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("", "r", "r", "CDATA", "B4");
        attributes.addAttribute("", "t", "t", "CDATA", "n");
        attributes.addAttribute("", "s", "s", "CDATA", "3");

        Assertions.assertDoesNotThrow(() -> new CellTagHandler().startElement(context, "c", attributes));

        // Numeric style indices keep their exact lookup; the happy path is untouched.
        Mockito.verify(workbookHolder).dataFormatData(3);
    }
}
