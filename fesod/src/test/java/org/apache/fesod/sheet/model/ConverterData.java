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

package org.apache.fesod.sheet.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.metadata.data.CellData;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.util.TestUtil;

/**
 * Unified data model for converter tests.
 * Consolidates the previous ConverterReadData and ConverterWriteData classes.
 *
 * <p>This class uses a generic CellData type to support both read and write operations.
 * For write operations, use {@link WriteCellData}; for read operations, the framework
 * will populate {@link ReadCellData}.</p>
 *
 * @see org.apache.fesod.sheet.converter.ConverterReadData (deprecated)
 * @see org.apache.fesod.sheet.converter.ConverterWriteData (deprecated)
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"cellData"}) // Exclude cellData to avoid StackOverflow in hashCode
@ToString(exclude = {"cellData"})
public class ConverterData {

    @ExcelProperty("日期")
    private Date date;

    @ExcelProperty("本地日期")
    private LocalDate localDate;

    @ExcelProperty("本地日期时间")
    private LocalDateTime localDateTime;

    @ExcelProperty("布尔")
    private Boolean booleanData;

    @ExcelProperty("大数")
    private BigDecimal bigDecimal;

    @ExcelProperty("大整数")
    private BigInteger bigInteger;

    @ExcelProperty("长整型")
    private Long longData;

    @ExcelProperty("整型")
    private Integer integerData;

    @ExcelProperty("短整型")
    private Short shortData;

    @ExcelProperty("字节型")
    private Byte byteData;

    @ExcelProperty("双精度浮点型")
    private Double doubleData;

    @ExcelProperty("浮点型")
    private Float floatData;

    @ExcelProperty("字符串")
    private String string;

    @ExcelProperty("自定义")
    private CellData<?> cellData;

    /**
     * Default constructor.
     */
    public ConverterData() {}

    /**
     * Creates a ConverterData instance with all standard test values.
     * Useful for write tests.
     *
     * @return a fully populated ConverterData instance
     */
    public static ConverterData createTestData() {
        ConverterData data = new ConverterData();
        data.setDate(TestUtil.TEST_DATE);
        data.setLocalDate(TestUtil.TEST_LOCAL_DATE);
        data.setLocalDateTime(TestUtil.TEST_LOCAL_DATE_TIME);
        data.setBooleanData(Boolean.TRUE);
        data.setBigDecimal(BigDecimal.ONE);
        data.setBigInteger(BigInteger.ONE);
        data.setLongData(1L);
        data.setIntegerData(1);
        data.setShortData((short) 1);
        data.setByteData((byte) 1);
        data.setDoubleData(1.0);
        data.setFloatData(1.0f);
        data.setString("测试");
        data.setCellData(new WriteCellData<>("自定义"));
        return data;
    }
}
