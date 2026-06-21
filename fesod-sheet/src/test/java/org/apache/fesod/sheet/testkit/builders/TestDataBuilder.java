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

package org.apache.fesod.sheet.testkit.builders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.converter.CellDataWriteData;
import org.apache.fesod.sheet.converter.ConverterWriteData;
import org.apache.fesod.sheet.converter.CustomConverterWriteData;
import org.apache.fesod.sheet.converter.ExcludeOrIncludeData;
import org.apache.fesod.sheet.converter.SortData;
import org.apache.fesod.sheet.core.RepetitionData;
import org.apache.fesod.sheet.core.UnCamelData;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.head.ComplexHeadData;
import org.apache.fesod.sheet.head.NoHeadData;
import org.apache.fesod.sheet.metadata.data.FormulaData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.readwrite.CacheData;
import org.apache.fesod.sheet.sheet.WriteSheetData;
import org.apache.fesod.sheet.style.AnnotationData;
import org.apache.fesod.sheet.style.AnnotationIndexAndNameData;
import org.apache.fesod.sheet.style.FillAnnotationData;
import org.apache.fesod.sheet.style.FillData;
import org.apache.fesod.sheet.style.FillStyleAnnotatedData;
import org.apache.fesod.sheet.style.FillStyleData;
import org.apache.fesod.sheet.style.StyleData;
import org.apache.fesod.sheet.template.TemplateData;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.models.TitleData;
import org.apache.fesod.sheet.util.DateUtils;
import org.apache.fesod.sheet.util.TestUtil;

/**
 * Shared test data factory methods, replacing per-class {@code data()} methods.
 */
public final class TestDataBuilder {

    private TestDataBuilder() {}

    /**
     * Creates a list of {@link SimpleData} with names "Name0" through "Name{count-1}".
     */
    public static List<SimpleData> simpleData(int count) {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SimpleData d = new SimpleData();
            d.setName("Name" + i);
            list.add(d);
        }
        return list;
    }

    /**
     * Creates a single-element list of {@link ConverterWriteData} with all 14 fields populated.
     */
    public static List<ConverterWriteData> converterWriteData() {
        List<ConverterWriteData> list = new ArrayList<>();
        ConverterWriteData data = new ConverterWriteData();
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
        data.setFloatData((float) 1.0);
        data.setString("test");
        data.setCellData(new WriteCellData<>("custom"));
        list.add(data);
        return list;
    }

    /**
     * Creates a list of {@link SimpleData} with prefixed names like "{prefix}Name0".
     */
    public static List<SimpleData> simpleData(int count, String prefix) {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SimpleData d = new SimpleData();
            d.setName(prefix + "Name" + i);
            list.add(d);
        }
        return list;
    }

    /**
     * Creates a list of {@link SimpleData} with name and age fields populated.
     */
    public static List<SimpleData> simpleDataWithAge(int count) {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SimpleData d = new SimpleData();
            d.setName("Name" + i);
            d.setAge(i);
            list.add(d);
        }
        return list;
    }

    /**
     * Creates a list of {@link SimpleData} with name and date fields populated.
     */
    public static List<SimpleData> simpleDataWithDate(int count) {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SimpleData d = new SimpleData();
            d.setName("Name" + i);
            d.setDate(new Date());
            list.add(d);
        }
        return list;
    }

    /**
     * Creates a list of {@link CacheData} with name ("Name") and age (Long, "Age") fields.
     */
    public static List<CacheData> cacheData(int count) {
        List<CacheData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CacheData d = new CacheData();
            d.setName("Name" + i);
            d.setAge((long) i);
            list.add(d);
        }
        return list;
    }

    /**
     * Creates a list of {@link TitleData} with titles like "{prefix}0".
     */
    public static List<TitleData> titleData(int count, String prefix) {
        List<TitleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TitleData d = new TitleData();
            d.setTitle(prefix + i);
            list.add(d);
        }
        return list;
    }

    public static List<UnCamelData> unCamelData(int count) {
        List<UnCamelData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UnCamelData d = new UnCamelData();
            d.setString1("string1");
            d.setString2("string2");
            d.setSTring3("string3");
            d.setSTring4("string4");
            d.setSTRING5("string5");
            d.setSTRing6("string6");
            list.add(d);
        }
        return list;
    }

    public static List<RepetitionData> repetitionData(int count) {
        List<RepetitionData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            RepetitionData d = new RepetitionData();
            d.setString("String0");
            list.add(d);
        }
        return list;
    }

    public static List<WriteSheetData> writeSheetData(int count) {
        List<WriteSheetData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            WriteSheetData d = new WriteSheetData();
            d.setString("String" + i);
            list.add(d);
        }
        return list;
    }

    public static List<ComplexHeadData> complexHeadData(int count) {
        List<ComplexHeadData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ComplexHeadData d = new ComplexHeadData();
            d.setString0("String0");
            d.setString1("String1");
            d.setString2("String2");
            d.setString3("String3");
            d.setString4("String4");
            list.add(d);
        }
        return list;
    }

    public static List<NoHeadData> noHeadData(int count) {
        List<NoHeadData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            NoHeadData d = new NoHeadData();
            d.setString("String0");
            list.add(d);
        }
        return list;
    }

    public static List<ExcludeOrIncludeData> excludeOrIncludeData(int count) {
        List<ExcludeOrIncludeData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ExcludeOrIncludeData d = new ExcludeOrIncludeData();
            d.setColumn1("column1");
            d.setColumn2("column2");
            d.setColumn3("column3");
            d.setColumn4("column4");
            list.add(d);
        }
        return list;
    }

    public static List<SortData> sortData(int count) {
        List<SortData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SortData d = new SortData();
            d.setColumn1("column1");
            d.setColumn2("column2");
            d.setColumn3("column3");
            d.setColumn4("column4");
            d.setColumn5("column5");
            d.setColumn6("column6");
            list.add(d);
        }
        return list;
    }

    public static List<StyleData> styleData(int count) {
        List<StyleData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StyleData d = new StyleData();
            d.setString("String" + i);
            d.setString1("String" + i + "1");
            list.add(d);
        }
        return list;
    }

    public static List<AnnotationData> annotationData(int count) {
        List<AnnotationData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AnnotationData d = new AnnotationData();
            try {
                d.setDate(DateUtils.parseDate("2020-01-01 01:01:01"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            d.setNumber(99.99);
            d.setIgnore("ignore");
            d.setTransientString("ignore");
            list.add(d);
        }
        return list;
    }

    public static List<AnnotationIndexAndNameData> annotationIndexAndNameData(int count) {
        List<AnnotationIndexAndNameData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AnnotationIndexAndNameData d = new AnnotationIndexAndNameData();
            d.setIndex0("Item0");
            d.setIndex1("Item1");
            d.setIndex2("Item2");
            d.setIndex4("Item4");
            list.add(d);
        }
        return list;
    }

    public static List<FillData> fillData(int count) {
        List<FillData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            FillData d = new FillData();
            if (i == 5) {
                d.setName(null);
            } else {
                d.setName("Zhang San");
            }
            d.setNumber(5.2);
            list.add(d);
        }
        return list;
    }

    public static List<FillStyleData> fillStyleData(int count) throws Exception {
        List<FillStyleData> list = new ArrayList<>();
        Date date = DateUtils.parseDate("2020-01-01 01:01:01");
        for (int i = 0; i < count; i++) {
            FillStyleData d = new FillStyleData();
            d.setName(i == 5 ? null : "Zhang San");
            d.setNumber(5.2);
            d.setDate(date);
            list.add(d);
        }
        return list;
    }

    public static List<FillStyleAnnotatedData> fillStyleAnnotatedData(int count) throws Exception {
        List<FillStyleAnnotatedData> list = new ArrayList<>();
        Date date = DateUtils.parseDate("2020-01-01 01:01:01");
        for (int i = 0; i < count; i++) {
            FillStyleAnnotatedData d = new FillStyleAnnotatedData();
            d.setName(i == 5 ? null : "Zhang San");
            d.setNumber(5.2);
            d.setDate(date);
            list.add(d);
        }
        return list;
    }

    public static List<FillAnnotationData> fillAnnotationData(int count, String imagePath) throws Exception {
        List<FillAnnotationData> list = new ArrayList<>();
        Date date = DateUtils.parseDate("2020-01-01 01:01:01");
        for (int i = 0; i < count; i++) {
            FillAnnotationData d = new FillAnnotationData();
            d.setDate(date);
            d.setNumber(99.99);
            d.setString1("string1");
            d.setString2("string2");
            d.setImage(imagePath);
            list.add(d);
        }
        return list;
    }

    public static List<TemplateData> templateData(int count) {
        List<TemplateData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TemplateData d = new TemplateData();
            d.setString0("String" + i);
            d.setString1("String" + i + "1");
            list.add(d);
        }
        return list;
    }

    public static List<List<Object>> noModelData(int count) throws Exception {
        List<List<Object>> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<Object> data = new ArrayList<>();
            data.add("string1" + i);
            data.add(100 + i);
            data.add(DateUtils.parseDate("2020-01-01 01:01:01"));
            list.add(data);
        }
        return list;
    }

    public static List<CellDataWriteData> cellDataWriteData() throws Exception {
        List<CellDataWriteData> list = new ArrayList<>();
        CellDataWriteData cellDataData = new CellDataWriteData();
        cellDataData.setDate(new WriteCellData<>(DateUtils.parseDate("2020-01-01 01:01:01")));
        WriteCellData<Integer> integer1 = new WriteCellData<>();
        integer1.setType(CellDataTypeEnum.NUMBER);
        integer1.setNumberValue(BigDecimal.valueOf(2L));
        cellDataData.setInteger1(integer1);
        cellDataData.setInteger2(2);
        WriteCellData<?> formulaValue = new WriteCellData<>();
        FormulaData formulaData = new FormulaData();
        formulaValue.setFormulaData(formulaData);
        formulaData.setFormulaValue("B2+C2");
        cellDataData.setFormulaValue(formulaValue);
        list.add(cellDataData);
        return list;
    }

    public static List<CustomConverterWriteData> customConverterWriteData() {
        List<CustomConverterWriteData> list = new ArrayList<>();
        CustomConverterWriteData writeData = new CustomConverterWriteData();
        writeData.setTimestampStringData(Timestamp.valueOf("2020-01-01 01:00:00"));
        writeData.setTimestampNumberData(Timestamp.valueOf("2020-12-01 12:12:12"));
        list.add(writeData);
        return list;
    }
}
