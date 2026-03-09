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

package org.apache.fesod.sheet.examples.write;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.ExampleTestBase;
import org.apache.fesod.sheet.examples.write.data.DemoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link BasicWriteExample}.
 *
 * <p>Verifies: (1) the example completes without exception, and (2) a separate controlled write
 * to a {@code @TempDir} produces a valid Excel workbook with the expected number of data rows.
 */
class BasicWriteExampleITCase extends ExampleTestBase {

    @Test
    void testBasicWrite() {
        assertDoesNotThrow(BasicWriteExample::basicWrite);
    }

    @Test
    void testWriteProducesValidExcel(@TempDir Path tempDir) {
        String fileName = getTempOutputPath(tempDir, "basicWrite.xlsx");
        List<DemoData> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData d = new DemoData();
            d.setString("String" + i);
            d.setDate(new Date());
            d.setDoubleData(0.56);
            data.add(d);
        }
        FesodSheet.write(fileName, DemoData.class).sheet("Template").doWrite(data);

        assertValidExcelFile(new File(fileName), 10);
    }
}
