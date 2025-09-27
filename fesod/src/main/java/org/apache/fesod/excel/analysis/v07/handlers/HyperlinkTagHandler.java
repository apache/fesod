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

package org.apache.fesod.excel.analysis.v07.handlers;

import java.util.Optional;
import org.apache.fesod.excel.constant.ExcelXmlConstants;
import org.apache.fesod.excel.context.xlsx.XlsxReadContext;
import org.apache.fesod.excel.enums.CellExtraTypeEnum;
import org.apache.fesod.excel.metadata.CellExtra;
import org.apache.fesod.excel.util.StringUtils;
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
        // case 1，In the 'location' tag
        String location = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_LOCATION);
        if (location != null) {
            CellExtra cellExtra = new CellExtra(CellExtraTypeEnum.HYPERLINK, location, ref);
            xlsxReadContext.readSheetHolder().setCellExtra(cellExtra);
            xlsxReadContext.analysisEventProcessor().extra(xlsxReadContext);
            return;
        }
        // case 2, In the 'r:id' tag, Then go to 'PackageRelationshipCollection' to get inside
        String rId = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_RID);
        PackageRelationshipCollection packageRelationshipCollection =
                xlsxReadContext.xlsxReadSheetHolder().getPackageRelationshipCollection();
        if (rId == null || packageRelationshipCollection == null) {
            return;
        }
        Optional.ofNullable(packageRelationshipCollection.getRelationshipByID(rId))
                .map(PackageRelationship::getTargetURI)
                .ifPresent(uri -> {
                    CellExtra cellExtra = new CellExtra(CellExtraTypeEnum.HYPERLINK, uri.toString(), ref);
                    xlsxReadContext.readSheetHolder().setCellExtra(cellExtra);
                    xlsxReadContext.analysisEventProcessor().extra(xlsxReadContext);
                });
    }
}
