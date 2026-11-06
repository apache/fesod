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

package org.apache.fesod.sheet.read;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.enums.CellDataTypeEnum;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The OOXML cell type {@code t="d"} (ECMA-376 section 18.18.11) is a valid date cell holding an ISO-8601 string, so
 * reading it must work like any other cell instead of aborting the whole read. Genuinely unknown types still fail.
 */
@Tag(Tags.READ)
public class IsoDateCellTypeTest extends AbstractExcelTest {

    private File resource(String name) {
        return new File(getClass()
                .getClassLoader()
                .getResource("typed" + File.separator + name)
                .getFile());
    }

    @Test
    public void isoDateCellReadsAsText() {
        List<Map<Integer, String>> rows = FesodSheet.read(resource("iso-date-cell.xlsx"))
                .headRowNumber(0)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("2020-01-02T00:00:00", rows.get(0).get(0));
    }

    @Test
    public void plainNumberCellStillReads() {
        List<Map<Integer, String>> rows = FesodSheet.read(resource("number-cell.xlsx"))
                .headRowNumber(0)
                .sheet()
                .doReadSync();

        Assertions.assertEquals(1, rows.size());
        Assertions.assertEquals("123", rows.get(0).get(0));
    }

    @Test
    public void isoDateTypeRoutesToDirectString() {
        Assertions.assertEquals(CellDataTypeEnum.DIRECT_STRING, CellDataTypeEnum.buildFromCellType("d"));
        Assertions.assertNull(CellDataTypeEnum.buildFromCellType("not-a-type"), "unknown types stay unknown");
    }
}
