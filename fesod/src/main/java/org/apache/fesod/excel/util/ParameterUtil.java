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

import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.context.WriteContext;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.write.metadata.WriteSheet;

/**
 * Parameter Util
 */
public class ParameterUtil {

    /**
     * Get autoTrim flag for read
     *
     * @param readSheet actual read sheet
     * @param context   Analysis Context
     * @return autoTrim flag
     */
    public static boolean getAutoTrimFlag(ReadSheet readSheet, AnalysisContext context) {
        return (readSheet.getAutoTrim() != null && readSheet.getAutoTrim())
                || (readSheet.getAutoTrim() == null
                        && context.readWorkbookHolder().getGlobalConfiguration().getAutoTrim());
    }

    /**
     * Get autoStrip flag for read
     *
     * @param readSheet actual read sheet
     * @param context   Analysis Context
     * @return autoStrip flag
     */
    public static boolean getAutoStripFlag(ReadSheet readSheet, AnalysisContext context) {
        return (readSheet.getAutoStrip() != null && readSheet.getAutoStrip())
                || context.readWorkbookHolder().getGlobalConfiguration().getAutoStrip();
    }

    /**
     * Get autoTrim flag for write
     *
     * @param writeSheet actual write sheet
     * @param context    Write Context
     * @return autoTrim flag
     */
    public static boolean getAutoTrimFlag(WriteSheet writeSheet, WriteContext context) {
        return (writeSheet.getAutoTrim() != null && writeSheet.getAutoTrim())
                || (writeSheet.getAutoTrim() == null
                        && context.writeWorkbookHolder()
                                .getGlobalConfiguration()
                                .getAutoTrim());
    }

    /**
     * Get autoStrip flag for write
     *
     * @param writeSheet actual write sheet
     * @param context    Write Context
     * @return autoStrip flag
     */
    public static boolean getAutoStripFlag(WriteSheet writeSheet, WriteContext context) {
        return (writeSheet.getAutoStrip() != null && writeSheet.getAutoStrip())
                || context.writeWorkbookHolder().getGlobalConfiguration().getAutoStrip();
    }
}
