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

package org.apache.fesod.excel.metadata.data;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.constant.FastExcelConstants;
import org.apache.fesod.excel.enums.CellDataTypeEnum;

/**
 * read cell data
 * <p>
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ReadCellData<T> extends CellData<T> {

    /**
     * originalNumberValue vs numberValue
     * <ol>
     * <li>
     * NUMBER：
     * originalNumberValue: Original data and the accuracy of his is 17, but in fact the excel only 15 precision to
     * process the data
     * numberValue: After correction of the data and the accuracy of his is 15
     * for example, originalNumberValue = `2087.0249999999996` , numberValue = `2087.03`
     * </li>
     * <li>
     * DATE：
     * originalNumberValue: Storage is a data type double, accurate to milliseconds
     * dateValue: Based on double converted to a date format, he will revised date difference, accurate to seconds
     * for example, originalNumberValue = `44729.99998836806` ,time is:`2022-06-17 23:59:58.995`,
     * But in excel is displayed:` 2022-06-17 23:59:59`, dateValue = `2022-06-17 23:59:59`
     * </li>
     * </ol>
     * {@link CellDataTypeEnum#NUMBER} {@link CellDataTypeEnum#DATE}
     */
    private BigDecimal originalNumberValue;

    /**
     * data format.
     */
    private DataFormatData dataFormatData;

    public ReadCellData(CellDataTypeEnum type) {
        super();
        if (type == null) {
            throw new IllegalArgumentException("Type can not be null");
        }
        setType(type);
    }

    public ReadCellData(T data) {
        super();
        setData(data);
    }

    public ReadCellData(String stringValue) {
        this(CellDataTypeEnum.STRING, stringValue);
    }

    public ReadCellData(CellDataTypeEnum type, String stringValue) {
        super();
        if (type != CellDataTypeEnum.STRING && type != CellDataTypeEnum.ERROR) {
            throw new IllegalArgumentException("Only support CellDataTypeEnum.STRING and  CellDataTypeEnum.ERROR");
        }
        if (stringValue == null) {
            throw new IllegalArgumentException("StringValue can not be null");
        }
        setType(type);
        setStringValue(stringValue);
    }

    public ReadCellData(BigDecimal numberValue) {
        super();
        if (numberValue == null) {
            throw new IllegalArgumentException("DoubleValue can not be null");
        }
        setType(CellDataTypeEnum.NUMBER);
        setNumberValue(numberValue);
    }

    public ReadCellData(Boolean booleanValue) {
        super();
        if (booleanValue == null) {
            throw new IllegalArgumentException("BooleanValue can not be null");
        }
        setType(CellDataTypeEnum.BOOLEAN);
        setBooleanValue(booleanValue);
    }

    public static ReadCellData<?> newEmptyInstance() {
        return newEmptyInstance(null, null);
    }

    public static ReadCellData<?> newEmptyInstance(Integer rowIndex, Integer columnIndex) {
        ReadCellData<?> cellData = new ReadCellData<>(CellDataTypeEnum.EMPTY);
        cellData.setRowIndex(rowIndex);
        cellData.setColumnIndex(columnIndex);
        return cellData;
    }

    public static ReadCellData<?> newInstance(Boolean booleanValue) {
        return newInstance(booleanValue, null, null);
    }

    public static ReadCellData<?> newInstance(Boolean booleanValue, Integer rowIndex, Integer columnIndex) {
        ReadCellData<?> cellData = new ReadCellData<>(booleanValue);
        cellData.setRowIndex(rowIndex);
        cellData.setColumnIndex(columnIndex);
        return cellData;
    }

    public static ReadCellData<?> newInstance(String stringValue, Integer rowIndex, Integer columnIndex) {
        ReadCellData<?> cellData = new ReadCellData<>(stringValue);
        cellData.setRowIndex(rowIndex);
        cellData.setColumnIndex(columnIndex);
        return cellData;
    }

    public static ReadCellData<?> newInstance(BigDecimal numberValue, Integer rowIndex, Integer columnIndex) {
        ReadCellData<?> cellData = new ReadCellData<>(numberValue);
        cellData.setRowIndex(rowIndex);
        cellData.setColumnIndex(columnIndex);
        return cellData;
    }

    public static ReadCellData<?> newInstanceOriginal(BigDecimal numberValue, Integer rowIndex, Integer columnIndex) {
        ReadCellData<?> cellData = new ReadCellData<>(numberValue);
        cellData.setRowIndex(rowIndex);
        cellData.setColumnIndex(columnIndex);
        cellData.setOriginalNumberValue(numberValue);
        cellData.setNumberValue(numberValue.round(FastExcelConstants.EXCEL_MATH_CONTEXT));
        return cellData;
    }

    @Override
    public ReadCellData<Object> clone() {
        ReadCellData<Object> readCellData = new ReadCellData<>();
        readCellData.setType(getType());
        readCellData.setNumberValue(getNumberValue());
        readCellData.setOriginalNumberValue(getOriginalNumberValue());
        readCellData.setStringValue(getStringValue());
        readCellData.setBooleanValue(getBooleanValue());
        readCellData.setData(getData());
        if (getDataFormatData() != null) {
            readCellData.setDataFormatData(getDataFormatData().clone());
        }
        if (getFormulaData() != null) {
            readCellData.setFormulaData(getFormulaData().clone());
        }
        return readCellData;
    }
}
