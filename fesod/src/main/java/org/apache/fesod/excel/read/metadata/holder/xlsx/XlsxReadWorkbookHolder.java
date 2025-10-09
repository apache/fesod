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

package org.apache.fesod.excel.read.metadata.holder.xlsx;

import java.util.Map;
import javax.xml.parsers.SAXParserFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.excel.constant.BuiltinFormats;
import org.apache.fesod.excel.metadata.data.DataFormatData;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.read.metadata.holder.ReadWorkbookHolder;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.MapUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * Workbook holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class XlsxReadWorkbookHolder extends ReadWorkbookHolder {
    /**
     * Package
     */
    private OPCPackage opcPackage;
    /**
     * SAXParserFactory used when reading xlsx.
     * <p>
     * The default will automatically find.
     * <p>
     * Please pass in the name of a class ,like : "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl"
     *
     * @see SAXParserFactory#newInstance()
     * @see SAXParserFactory#newInstance(String, ClassLoader)
     */
    private String saxParserFactoryName;
    /**
     * Current style information
     */
    private StylesTable stylesTable;
    /**
     * cache data format
     */
    private Map<Integer, DataFormatData> dataFormatDataCache;

    /**
     * excel Relationship, key: sheetNo value: PackageRelationshipCollection
     */
    private Map<Integer, PackageRelationshipCollection> packageRelationshipCollectionMap;

    public XlsxReadWorkbookHolder(ReadWorkbook readWorkbook) {
        super(readWorkbook);
        this.saxParserFactoryName = readWorkbook.getXlsxSAXParserFactoryName();
        setExcelType(ExcelTypeEnum.XLSX);
        dataFormatDataCache = MapUtils.newHashMap();
    }

    public DataFormatData dataFormatData(int dateFormatIndexInteger) {
        return dataFormatDataCache.computeIfAbsent(dateFormatIndexInteger, key -> {
            DataFormatData dataFormatData = new DataFormatData();
            if (stylesTable == null) {
                return null;
            }
            XSSFCellStyle xssfCellStyle = stylesTable.getStyleAt(dateFormatIndexInteger);
            if (xssfCellStyle == null) {
                return null;
            }
            dataFormatData.setIndex(xssfCellStyle.getDataFormat());
            dataFormatData.setFormat(BuiltinFormats.getBuiltinFormat(
                    dataFormatData.getIndex(),
                    xssfCellStyle.getDataFormatString(),
                    globalConfiguration().getLocale()));
            return dataFormatData;
        });
    }
}
