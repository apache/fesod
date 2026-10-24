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

package org.apache.fesod.sheet.analysis.v03;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.Record;

/**
 * Returns the format string the file defines for built-in format indexes 1-22 and 37-49. POI's
 * {@link FormatTrackingHSSFListener} ignores those FORMAT records and returns its own built-in strings, which use
 * "$" for the currency formats.
 */
class XlsFormatTrackingListener extends FormatTrackingHSSFListener {

    private static final String RESERVED = "reserved-";

    private final Map<Integer, String> fileFormats = new HashMap<>();

    XlsFormatTrackingListener(HSSFListener childListener) {
        super(childListener);
    }

    @Override
    public void processRecordInternally(Record record) {
        if (record instanceof FormatRecord) {
            FormatRecord formatRecord = (FormatRecord) record;
            fileFormats.put(formatRecord.getIndexCode(), formatRecord.getFormatString());
        }
        super.processRecordInternally(record);
    }

    @Override
    public String getFormatString(int formatIndex) {
        String format = super.getFormatString(formatIndex);
        // Keep index 0 and POI's reserved placeholders (23-36); BuiltinFormats resolves the latter from its tables
        if (formatIndex <= 0 || format == null || format.startsWith(RESERVED)) {
            return format;
        }
        String fileFormat = fileFormats.get(formatIndex);
        return fileFormat == null ? format : fileFormat;
    }
}
