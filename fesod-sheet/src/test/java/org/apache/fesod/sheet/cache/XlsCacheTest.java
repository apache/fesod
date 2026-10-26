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

package org.apache.fesod.sheet.cache;

import org.apache.fesod.sheet.testkit.Tags;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link XlsCache}.
 */
@Tag(Tags.UNIT)
class XlsCacheTest {

    @Test
    void getReturnsSstStringByIndex() {
        SSTRecord sstRecord = new SSTRecord();
        int first = sstRecord.addString(new UnicodeString("alpha"));
        int second = sstRecord.addString(new UnicodeString("beta"));
        XlsCache cache = new XlsCache(sstRecord);

        Assertions.assertEquals("alpha", cache.get(first));
        Assertions.assertEquals("beta", cache.get(second));
    }

    @Test
    void defaultMethodsDoNotChangeSstContents() {
        SSTRecord sstRecord = new SSTRecord();
        sstRecord.addString(new UnicodeString("original"));
        XlsCache cache = new XlsCache(sstRecord);

        cache.init(null);
        cache.put("ignored");
        cache.putFinished();
        cache.destroy();

        Assertions.assertEquals("original", cache.get(0));
    }

    @Test
    void getReturnsUnicodeContent() {
        SSTRecord sstRecord = new SSTRecord();
        sstRecord.addString(new UnicodeString("中文"));
        XlsCache cache = new XlsCache(sstRecord);

        Assertions.assertEquals("中文", cache.get(0));
    }
}
