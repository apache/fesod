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

package org.apache.fesod.excel.converter;

import com.alibaba.fastjson2.JSON;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.exception.ExcelCommonException;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.DateUtils;
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
        Assertions.assertEquals(list.size(), 1);
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
        Assertions.assertEquals((long) data.getByteBoolean(), 1L);
        Assertions.assertEquals((long) data.getByteNumber(), 1L);
        Assertions.assertEquals((long) data.getByteString(), 1L);
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
        Assertions.assertEquals(data.getDoubleBoolean(), 1.0, 0.0);
        Assertions.assertEquals(data.getDoubleNumber(), 1.0, 0.0);
        Assertions.assertEquals(data.getDoubleString(), 1.0, 0.0);
        Assertions.assertEquals(data.getFloatBoolean(), (float) 1.0, 0.0);
        Assertions.assertEquals(data.getFloatNumber(), (float) 1.0, 0.0);
        Assertions.assertEquals(data.getFloatString(), (float) 1.0, 0.0);
        Assertions.assertEquals((long) data.getIntegerBoolean(), 1L);
        Assertions.assertEquals((long) data.getIntegerNumber(), 1L);
        Assertions.assertEquals((long) data.getIntegerString(), 1L);
        Assertions.assertEquals((long) data.getLongBoolean(), 1L);
        Assertions.assertEquals((long) data.getLongNumber(), 1L);
        Assertions.assertEquals((long) data.getLongString(), 1L);
        Assertions.assertEquals((long) data.getShortBoolean(), 1L);
        Assertions.assertEquals((long) data.getShortNumber(), 1L);
        Assertions.assertEquals((long) data.getShortString(), 1L);
        Assertions.assertEquals(data.getStringBoolean().toLowerCase(), "true");
        Assertions.assertEquals(data.getStringString(), "测试");
        Assertions.assertEquals(data.getStringError(), "#VALUE!");
        if (context.readWorkbookHolder().getExcelType() != ExcelTypeEnum.CSV) {
            Assertions.assertEquals("2020-1-1 1:01", data.getStringNumberDate());
        } else {
            Assertions.assertEquals("2020-01-01 01:01:01", data.getStringNumberDate());
        }
        double doubleStringFormulaNumber = new BigDecimal(data.getStringFormulaNumber()).doubleValue();
        Assertions.assertEquals(doubleStringFormulaNumber, 2.0, 0.0);
        Assertions.assertEquals(data.getStringFormulaString(), "1测试");
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
