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

package org.apache.fesod.sheet.converters.clob;

import java.io.File;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.serial.SerialClob;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.helpers.RoundTripHelper;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Field-level round trips for Clob converter across every supported format.
 */
@Tag(Tags.ROUND_TRIP)
public class ClobFieldRoundTripTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void clobFieldRoundTrip(ExcelFormat format) throws Exception {
        File file = createTempFile(format);
        List<ClobFieldData> data = new ArrayList<>();
        ClobFieldData row = new ClobFieldData();
        row.setClob(new SerialClob("Large text 字符".toCharArray()));
        data.add(row);

        List<ClobFieldData> result = RoundTripHelper.writeAndRead(file, ClobFieldData.class, data);

        Assertions.assertEquals(1, result.size());
        Clob actual = result.get(0).getClob();
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Large text 字符", actual.getSubString(1, (int) actual.length()));
    }

    @Getter
    @Setter
    public static class ClobFieldData {

        @ExcelProperty("clob")
        private Clob clob;
    }
}
