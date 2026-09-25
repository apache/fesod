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

package org.apache.fesod.sheet.analysis.v07.handlers;

import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.constant.ExcelXmlConstants;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.enums.CellExtraTypeEnum;
import org.apache.fesod.sheet.metadata.CellExtra;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.xml.sax.Attributes;

/**
 * Cell Handler
 *
 *
 */
public class HyperlinkTagHandler extends AbstractXlsxTagHandler {

    @Override
    public boolean support(XlsxReadContext xlsxReadContext) {
        return xlsxReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.HYPERLINK);
    }

    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        String ref = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_REF);
        if (StringUtils.isEmpty(ref)) {
            return;
        }
        // Hyperlink has 2 case:
        // case 1, In the 'r:id' tag, Then go to 'PackageRelationshipCollection' to get inside;
        // a 'location' next to it is the URI fragment
        String location = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_LOCATION);
        String rId = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_RID);
        PackageRelationshipCollection packageRelationshipCollection =
                xlsxReadContext.xlsxReadSheetHolder().getPackageRelationshipCollection();
        PackageRelationship relationship = rId == null || packageRelationshipCollection == null
                ? null
                : packageRelationshipCollection.getRelationshipByID(rId);
        String address;
        if (relationship != null) {
            address = relationship.getTargetURI().toString();
            if (location != null) {
                address += "#" + location;
            }
        } else if (location != null) {
            // case 2，In the 'location' tag
            address = location;
        } else {
            return;
        }
        CellExtra cellExtra = new CellExtra(CellExtraTypeEnum.HYPERLINK, address, ref);
        xlsxReadContext.readSheetHolder().setCellExtra(cellExtra);
        xlsxReadContext.analysisEventProcessor().extra(xlsxReadContext);
    }
}
