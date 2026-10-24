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

package org.apache.fesod.sheet.analysis.v03.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.fesod.sheet.context.xls.XlsReadContext;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.enums.RowTypeEnum;
import org.apache.fesod.sheet.metadata.Cell;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadSheetHolder;
import org.apache.fesod.sheet.read.metadata.holder.xls.XlsReadWorkbookHolder;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * Regression test: BIFF {@link RKRecord} numeric cells must be decoded, not stored as EMPTY.
 *
 * <p>Modern Excel / POI usermodel writes {@code NUMBER} records, not {@code RK}. The event-API path
 * still sees {@code RK} in older BIFF files, so this test builds real {@link RKRecord}s from bytes
 * covering the RK encodings supported by Apache POI ({@code RKUtil.decodeNumber}).
 */
@Tag(Tags.UNIT)
class RkRecordHandlerTest {

    private static final int ROW = 3;
    private static final int COLUMN = 1;
    private static final String FORMAT_STRING = "0.00";

    @Test
    void processRecord_preservesDataFormatAndMarksRowAsData() throws IOException {
        Map<Integer, Cell> cellMap = new LinkedHashMap<>();
        XlsReadContext context = createContext(cellMap);

        new RkRecordHandler().processRecord(context, createRkRecord(ROW, COLUMN, encodeIntegerRk(42, false)));

        ReadCellData<?> cellData = (ReadCellData<?>) cellMap.get(COLUMN);
        Assertions.assertNotNull(cellData.getDataFormatData());
        Assertions.assertEquals(FORMAT_STRING, cellData.getDataFormatData().getFormat());
        Mockito.verify(context.xlsReadSheetHolder()).setTempRowType(RowTypeEnum.DATA);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rkEncodings")
    void processRecord_decodesRkEncodings(String caseName, int rkNumber, double expectedValue) throws IOException {
        Map<Integer, Cell> cellMap = new LinkedHashMap<>();
        XlsReadContext context = createContext(cellMap);

        RKRecord rec = createRkRecord(ROW, COLUMN, rkNumber);
        Assertions.assertEquals(
                expectedValue, rec.getRKNumber(), 0.0, "fixture RK encoding must decode to " + expectedValue);

        new RkRecordHandler().processRecord(context, rec);

        Cell cell = cellMap.get(COLUMN);
        Assertions.assertNotNull(cell, caseName + ": RK cell must be stored in the cell map");
        Assertions.assertInstanceOf(ReadCellData.class, cell);
        ReadCellData<?> cellData = (ReadCellData<?>) cell;

        Assertions.assertEquals(
                CellDataTypeEnum.NUMBER,
                cellData.getType(),
                caseName + ": RK record must be read as NUMBER, not EMPTY");
        Assertions.assertEquals(
                0,
                BigDecimal.valueOf(expectedValue).compareTo(cellData.getNumberValue()),
                caseName + ": RK numeric value must be preserved");
        Assertions.assertEquals(ROW, cellData.getRowIndex());
        Assertions.assertEquals(COLUMN, cellData.getColumnIndex());
    }

    static Stream<Arguments> rkEncodings() {
        return Stream.of(
                Arguments.of("positive integer", encodeIntegerRk(42, false), 42.0),
                Arguments.of("negative integer", encodeIntegerRk(-7, false), -7.0),
                Arguments.of("integer with /100 flag", encodeIntegerRk(1234, true), 12.34),
                Arguments.of("negative integer with /100 flag", encodeIntegerRk(-50, true), -0.5),
                Arguments.of("IEEE floating point", encodeIeeeRk(1.5, false), 1.5),
                Arguments.of("IEEE floating point with /100 flag", encodeIeeeRk(5.5, true), 0.055));
    }

    private static XlsReadContext createContext(Map<Integer, Cell> cellMap) {
        XlsReadContext context = Mockito.mock(XlsReadContext.class);
        ReadSheetHolder readSheetHolder = Mockito.mock(ReadSheetHolder.class);
        XlsReadSheetHolder xlsReadSheetHolder = Mockito.mock(XlsReadSheetHolder.class);
        XlsReadWorkbookHolder xlsReadWorkbookHolder = Mockito.mock(XlsReadWorkbookHolder.class);
        FormatTrackingHSSFListener formatListener = Mockito.mock(FormatTrackingHSSFListener.class);
        GlobalConfiguration globalConfiguration = new GlobalConfiguration();
        globalConfiguration.setLocale(Locale.US);

        Mockito.when(context.readSheetHolder()).thenReturn(readSheetHolder);
        Mockito.when(context.xlsReadSheetHolder()).thenReturn(xlsReadSheetHolder);
        Mockito.when(context.xlsReadWorkbookHolder()).thenReturn(xlsReadWorkbookHolder);
        Mockito.when(readSheetHolder.determineTargetColumnIndex(COLUMN)).thenReturn(COLUMN);
        Mockito.when(readSheetHolder.getGlobalConfiguration()).thenReturn(globalConfiguration);
        Mockito.when(xlsReadSheetHolder.getCellMap()).thenReturn(cellMap);
        Mockito.when(xlsReadWorkbookHolder.getFormatTrackingHSSFListener()).thenReturn(formatListener);
        Mockito.when(formatListener.getFormatIndex(Mockito.any())).thenReturn(2);
        Mockito.when(formatListener.getFormatString(Mockito.any(RKRecord.class)))
                .thenReturn(FORMAT_STRING);
        return context;
    }

    /**
     * Integer RK: {@code (value << 2) | 0x02}, optionally {@code | 0x01} for divide-by-100.
     */
    private static int encodeIntegerRk(int value, boolean divideBy100) {
        int rkNumber = (value << 2) | 0x02;
        if (divideBy100) {
            rkNumber |= 0x01;
        }
        return rkNumber;
    }

    /**
     * IEEE RK: top 30 bits of the double bit pattern in bits 2–31, optionally {@code | 0x01} for
     * divide-by-100. Only values whose lower 34 mantissa bits are zero encode losslessly.
     */
    private static int encodeIeeeRk(double value, boolean divideBy100) {
        long bits = Double.doubleToLongBits(value);
        int rkNumber = (int) ((bits >>> 34) << 2);
        if (divideBy100) {
            rkNumber |= 0x01;
        }
        return rkNumber;
    }

    private static RKRecord createRkRecord(int row, int column, int rkNumber) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (LittleEndianOutputStream out = new LittleEndianOutputStream(bos)) {
            out.writeShort(RKRecord.sid);
            out.writeShort(10); // row(2) + col(2) + xf(2) + rk(4)
            out.writeShort(row);
            out.writeShort(column);
            out.writeShort(0); // xf index
            out.writeInt(rkNumber);
        }
        RecordInputStream ris = new RecordInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ris.nextRecord();
        return new RKRecord(ris);
    }
}
