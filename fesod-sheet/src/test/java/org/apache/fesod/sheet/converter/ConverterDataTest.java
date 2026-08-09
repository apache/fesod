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

package org.apache.fesod.sheet.converter;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatCapability;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test converter read/write for all Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class ConverterDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<ConverterWriteData> data = TestDataBuilder.converterWriteData();

        RoundTripHelper.write(file, ConverterWriteData.class, data);
        List<ConverterReadData> result = RoundTripHelper.read(file, ConverterReadData.class);

        Assertions.assertEquals(1, result.size());
        ConverterReadData row = result.get(0);
        Assertions.assertEquals(TestUtil.TEST_DATE, row.getDate());
        Assertions.assertEquals(TestUtil.TEST_LOCAL_DATE, row.getLocalDate());
        Assertions.assertEquals(TestUtil.TEST_LOCAL_DATE_TIME, row.getLocalDateTime());
        Assertions.assertEquals(Boolean.TRUE, row.getBooleanData());
        Assertions.assertEquals(row.getBigDecimal().doubleValue(), BigDecimal.ONE.doubleValue(), 0.0);
        Assertions.assertEquals(row.getBigInteger().intValue(), BigInteger.ONE.intValue(), 0.0);
        Assertions.assertEquals(1L, (long) row.getLongData());
        Assertions.assertEquals(1L, (long) row.getIntegerData());
        Assertions.assertEquals(1L, (long) row.getShortData());
        Assertions.assertEquals(1L, (long) row.getByteData());
        Assertions.assertEquals(1.0, row.getDoubleData(), 0.0);
        Assertions.assertEquals((float) 1.0, row.getFloatData(), 0.0);
        Assertions.assertEquals("test", row.getString());
        Assertions.assertEquals("custom", row.getCellData().getStringValue());
    }

    @ParameterizedTest
    @ExcelFormatSource
    void readAllConverter(ExcelFormat format) throws Exception {
        String fileName = "converter" + File.separator + "converter"
                + format.name().toLowerCase().replace("xlsx", "07").replace("xls", "03")
                + format.getExtension();
        List<ReadAllConverterData> list = FesodSheet.read(readFile(fileName))
                .head(ReadAllConverterData.class)
                .sheet()
                .doReadSync();
        Assertions.assertEquals(1, list.size());
        ReadAllConverterData data = list.get(0);
        Assertions.assertEquals(data.getBigDecimalBoolean().doubleValue(), BigDecimal.ONE.doubleValue(), 0.0);
        Assertions.assertEquals(data.getBigDecimalNumber().doubleValue(), BigDecimal.ONE.doubleValue(), 0.0);
        Assertions.assertEquals(data.getBigDecimalString().doubleValue(), BigDecimal.ONE.doubleValue(), 0.0);
        Assertions.assertEquals(data.getBigIntegerBoolean().intValue(), BigInteger.ONE.intValue(), 0.0);
        Assertions.assertEquals(data.getBigIntegerNumber().intValue(), BigInteger.ONE.intValue(), 0.0);
        Assertions.assertEquals(data.getBigIntegerString().intValue(), BigInteger.ONE.intValue(), 0.0);
        Assertions.assertTrue(data.getBooleanBoolean());
        Assertions.assertTrue(data.getBooleanNumber());
        Assertions.assertTrue(data.getBooleanString());
        Assertions.assertEquals(1L, (long) data.getByteBoolean());
        Assertions.assertEquals(1L, (long) data.getByteNumber());
        Assertions.assertEquals(1L, (long) data.getByteString());
        Assertions.assertEquals(data.getDateNumber(), DateUtils.parseDate("2020-01-01 01:01:01"));
        Assertions.assertEquals(data.getDateString(), DateUtils.parseDate("2020-01-01 01:01:01"));
        Assertions.assertEquals(
                data.getLocalDateTimeNumber(), DateUtils.parseLocalDateTime("2020-01-01 01:01:01", null, null));
        Assertions.assertEquals(
                data.getLocalDateTimeString(), DateUtils.parseLocalDateTime("2020-01-01 01:01:01", null, null));
        Assertions.assertEquals(1.0, data.getDoubleBoolean(), 0.0);
        Assertions.assertEquals(1.0, data.getDoubleNumber(), 0.0);
        Assertions.assertEquals(1.0, data.getDoubleString(), 0.0);
        Assertions.assertEquals((float) 1.0, data.getFloatBoolean(), 0.0);
        Assertions.assertEquals((float) 1.0, data.getFloatNumber(), 0.0);
        Assertions.assertEquals((float) 1.0, data.getFloatString(), 0.0);
        Assertions.assertEquals(1L, (long) data.getIntegerBoolean());
        Assertions.assertEquals(1L, (long) data.getIntegerNumber());
        Assertions.assertEquals(1L, (long) data.getIntegerString());
        Assertions.assertEquals(1L, (long) data.getLongBoolean());
        Assertions.assertEquals(1L, (long) data.getLongNumber());
        Assertions.assertEquals(1L, (long) data.getLongString());
        Assertions.assertEquals(1L, (long) data.getShortBoolean());
        Assertions.assertEquals(1L, (long) data.getShortNumber());
        Assertions.assertEquals(1L, (long) data.getShortString());
        Assertions.assertEquals("true", data.getStringBoolean().toLowerCase());
        Assertions.assertEquals("测试", data.getStringString());
        Assertions.assertEquals("#VALUE!", data.getStringError());
        if (format != ExcelFormat.CSV) {
            Assertions.assertEquals("2020-1-1 1:01", data.getStringNumberDate());
        } else {
            Assertions.assertEquals("2020-01-01 01:01:01", data.getStringNumberDate());
        }
        double doubleStringFormulaNumber = new BigDecimal(data.getStringFormulaNumber()).doubleValue();
        Assertions.assertEquals(2.0, doubleStringFormulaNumber, 0.0);
        Assertions.assertEquals("1测试", data.getStringFormulaString());
    }

    @ParameterizedTest
    @ExcelFormatSource(requires = FormatCapability.IMAGES)
    void writeImage(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        InputStream inputStream = null;
        try {
            List<ImageData> list = new ArrayList<>();
            ImageData imageData = new ImageData();
            list.add(imageData);
            String imagePath =
                    readFile("converter" + File.separator + "img.jpg").getAbsolutePath();
            imageData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
            imageData.setFile(new File(imagePath));
            imageData.setString(imagePath);
            inputStream = FileUtils.openInputStream(new File(imagePath));
            imageData.setInputStream(inputStream);
            FesodSheet.write(file, ImageData.class).sheet().doWrite(list);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
