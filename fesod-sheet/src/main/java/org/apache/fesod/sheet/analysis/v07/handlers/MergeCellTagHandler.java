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
import org.xml.sax.Attributes;

/**
 * Handler for the {@code mergeCell} tag of an XLSX worksheet. Each tag describes one
 * merged region through its {@code ref} attribute (for example {@code A1:B2}). The
 * region is wrapped into a {@link CellExtra} of type {@link CellExtraTypeEnum#MERGE}
 * and dispatched as an extra event, so listeners can receive merged-cell information
 * while the sheet is streamed.
 *
 * <p>The value of the merged region is not available from this tag and is therefore
 * left empty here; it is filled in later from the corresponding cell data.
 */
public class MergeCellTagHandler extends AbstractXlsxTagHandler {

    /**
     * Returns {@code true} only when the caller has requested
     * {@link CellExtraTypeEnum#MERGE} in the extra read set, since merged region
     * information is otherwise unnecessary and would add overhead.
     *
     * @param xlsxReadContext the current XLSX read context
     * @return {@code true} if merged region extras should be read
     */
    @Override
    public boolean support(XlsxReadContext xlsxReadContext) {
        return xlsxReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.MERGE);
    }

    /**
     * Reads the {@code ref} attribute of the {@code mergeCell} tag, stores the merged
     * region as a {@link CellExtra} on the sheet holder, and fires the extra event.
     * Tags without a {@code ref} attribute are ignored.
     *
     * @param xlsxReadContext the current XLSX read context
     * @param name            the tag name, always the {@code mergeCell} tag
     * @param attributes      the tag attributes, from which {@code ref} is read
     */
    @Override
    public void startElement(XlsxReadContext xlsxReadContext, String name, Attributes attributes) {
        String ref = attributes.getValue(ExcelXmlConstants.ATTRIBUTE_REF);
        if (StringUtils.isEmpty(ref)) {
            return;
        }
        CellExtra cellExtra = new CellExtra(CellExtraTypeEnum.MERGE, null, ref);
        xlsxReadContext.readSheetHolder().setCellExtra(cellExtra);
        xlsxReadContext.analysisEventProcessor().extra(xlsxReadContext);
    }
}
