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

package org.apache.fesod.sheet.converter;

import com.alibaba.fastjson2.JSON;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.DateUtils;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class ReadAllConverterDataListener extends AnalysisEventListener<ReadAllConverterData> {
    List<ReadAllConverterData> list = new ArrayList<ReadAllConverterData>();

    @Override
    public void invoke(ReadAllConverterData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
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
        try {
            Assertions.assertEquals(data.getDateNumber(), DateUtils.parseDate("2020-01-01 01:01:01"));
            Assertions.assertEquals(data.getDateString(), DateUtils.parseDate("2020-01-01 01:01:01"));
        } catch (ParseException e) {
            throw new ExcelCommonException("Test Exception", e);
        }
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
        if (context.readWorkbookHolder().getExcelType() != ExcelTypeEnum.CSV) {
            Assertions.assertEquals("2020-1-1 1:01", data.getStringNumberDate());
        } else {
            Assertions.assertEquals("2020-01-01 01:01:01", data.getStringNumberDate());
        }
        double doubleStringFormulaNumber = new BigDecimal(data.getStringFormulaNumber()).doubleValue();
        Assertions.assertEquals(2.0, doubleStringFormulaNumber, 0.0);
        Assertions.assertEquals("1测试", data.getStringFormulaString());
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
