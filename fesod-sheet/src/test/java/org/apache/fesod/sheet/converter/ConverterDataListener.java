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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.util.TestUtil;
import org.junit.jupiter.api.Assertions;

/**
 *
 */
@Slf4j
public class ConverterDataListener extends AnalysisEventListener<ConverterReadData> {
    private final List<ConverterReadData> list = new ArrayList<>();

    @Override
    public void invoke(ConverterReadData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        Assertions.assertEquals(1, list.size());
        ConverterReadData data = list.get(0);
        Assertions.assertEquals(TestUtil.TEST_DATE, data.getDate());
        Assertions.assertEquals(TestUtil.TEST_LOCAL_DATE, data.getLocalDate());
        Assertions.assertEquals(TestUtil.TEST_LOCAL_DATE_TIME, data.getLocalDateTime());
        Assertions.assertEquals(Boolean.TRUE, data.getBooleanData());
        Assertions.assertEquals(data.getBigDecimal().doubleValue(), BigDecimal.ONE.doubleValue(), 0.0);
        Assertions.assertEquals(data.getBigInteger().intValue(), BigInteger.ONE.intValue(), 0.0);
        Assertions.assertEquals(1L, (long) data.getLongData());
        Assertions.assertEquals(1L, (long) data.getIntegerData());
        Assertions.assertEquals(1L, (long) data.getShortData());
        Assertions.assertEquals(1L, (long) data.getByteData());
        Assertions.assertEquals(1.0, data.getDoubleData(), 0.0);
        Assertions.assertEquals((float) 1.0, data.getFloatData(), 0.0);
        Assertions.assertEquals("测试", data.getString());
        Assertions.assertEquals("自定义", data.getCellData().getStringValue());
        log.debug("First row:{}", JSON.toJSONString(list.get(0)));
    }
}
