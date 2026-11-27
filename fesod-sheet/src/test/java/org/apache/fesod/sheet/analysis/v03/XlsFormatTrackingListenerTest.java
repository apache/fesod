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

import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class XlsFormatTrackingListenerTest {

    private static final String YUAN_44 = "_(\"￥\"* #,##0.00_);_(\"￥\"* (#,##0.00);_(\"￥\"* \"-\"??_);_(@_)";

    @Test
    void getFormatStringPrefersFileFormatForBuiltinIndexes() {
        XlsFormatTrackingListener listener = new XlsFormatTrackingListener(record -> {});
        // FORMAT records a Chinese Excel writes for a currency format and for a reserved index
        listener.processRecord(new FormatRecord(44, YUAN_44));
        listener.processRecord(new FormatRecord(23, "\\$#,##0_);\\(\\$#,##0\\)"));

        Assertions.assertEquals(YUAN_44, listener.getFormatString(44));
        Assertions.assertEquals("reserved-0x17", listener.getFormatString(23));
        Assertions.assertEquals("General", listener.getFormatString(0));
        Assertions.assertEquals(HSSFDataFormat.getBuiltinFormat((short) 41), listener.getFormatString(41));
    }
}
