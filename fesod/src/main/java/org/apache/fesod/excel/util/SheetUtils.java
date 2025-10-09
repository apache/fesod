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

package org.apache.fesod.excel.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.ReadWorkbookHolder;

/**
 * Sheet utils
 *
 *
 */
@Slf4j
public class SheetUtils {

    private SheetUtils() {}

    /**
     * Match the parameters to the actual sheet
     *
     * @param readSheet       actual sheet
     * @param analysisContext
     * @return
     */
    public static ReadSheet match(ReadSheet readSheet, AnalysisContext analysisContext) {
        ReadWorkbookHolder readWorkbookHolder = analysisContext.readWorkbookHolder();
        if (analysisContext.readWorkbookHolder().getIgnoreHiddenSheet()
                && (readSheet.isHidden() || readSheet.isVeryHidden())) {
            return null;
        }
        if (readWorkbookHolder.getReadAll()) {
            return readSheet;
        }
        for (ReadSheet parameterReadSheet : readWorkbookHolder.getParameterSheetDataList()) {
            if (parameterReadSheet == null) {
                continue;
            }
            if (parameterReadSheet.getSheetNo() == null && parameterReadSheet.getSheetName() == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The first is read by default.");
                }
                parameterReadSheet.setSheetNo(0);
            }
            boolean match = (parameterReadSheet.getSheetNo() != null
                    && parameterReadSheet.getSheetNo().equals(readSheet.getSheetNo()));
            if (!match) {
                String parameterSheetName = parameterReadSheet.getSheetName();
                if (!StringUtils.isEmpty(parameterSheetName)) {
                    String sheetName = readSheet.getSheetName();
                    if (sheetName != null) {
                        boolean autoStrip = ParameterUtil.getAutoStripFlag(parameterReadSheet, analysisContext);
                        boolean autoTrim = ParameterUtil.getAutoTrimFlag(parameterReadSheet, analysisContext);

                        if (autoStrip) {
                            parameterSheetName = StringUtils.strip(parameterSheetName);
                            sheetName = StringUtils.strip(sheetName);
                        } else if (autoTrim) {
                            parameterSheetName = parameterSheetName.trim();
                            sheetName = sheetName.trim();
                        }
                        match = parameterSheetName.equals(sheetName);
                    }
                }
            }
            if (match) {
                readSheet.copyBasicParameter(parameterReadSheet);
                return readSheet;
            }
        }
        return null;
    }
}
