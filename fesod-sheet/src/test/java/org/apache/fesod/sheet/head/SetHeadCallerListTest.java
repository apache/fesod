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

package org.apache.fesod.sheet.head;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.ExcelWriter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.write.metadata.WriteSheet;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * A head supplied through {@code WriteWorkbook#setHead} is the caller's list: the row-count padding must not rewrite
 * it in place (or crash on an immutable column) — the same guarantee the builder path already gives by copying.
 */
class SetHeadCallerListTest {

    @TempDir
    File tempDir;

    @Test
    void paddingDoesNotRewriteTheCallerSuppliedHead() throws Exception {
        List<List<String>> head = new ArrayList<>();
        head.add(new ArrayList<>(Arrays.asList("h0", "h0b")));
        List<String> shortColumn = new ArrayList<>(Arrays.asList("h1"));
        head.add(shortColumn);

        write(head);

        Assertions.assertEquals(1, shortColumn.size(), "the caller's column list must stay untouched");
        Assertions.assertEquals(Arrays.asList("h1"), shortColumn);
    }

    @Test
    void unmodifiableShortColumnWritesInsteadOfCrashing() throws Exception {
        List<List<String>> head = new ArrayList<>();
        head.add(Arrays.asList("h0", "h0b"));
        head.add(Collections.unmodifiableList(Arrays.asList("h1")));

        File file = new File(tempDir, "unmodifiable-head.xlsx");
        Assertions.assertDoesNotThrow(
                () -> {
                    WriteWorkbook writeWorkbook = new WriteWorkbook();
                    writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
                    writeWorkbook.setFile(file);
                    writeWorkbook.setHead(head);
                    try (ExcelWriter writer = new ExcelWriter(writeWorkbook)) {
                        List<List<Object>> data = new ArrayList<>();
                        data.add(Arrays.asList("v1", "v2"));
                        writer.write(data, FesodSheet.writerSheet().build());
                    }
                },
                "an immutable short column is legal input, not a crash");
        Assertions.assertTrue(file.length() > 0, "the file was written");
    }

    private void write(List<List<String>> head) throws Exception {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
        writeWorkbook.setFile(new File(tempDir, "mutable-head.xlsx"));
        writeWorkbook.setHead(head);
        try (ExcelWriter writer = new ExcelWriter(writeWorkbook)) {
            List<List<Object>> data = new ArrayList<>();
            data.add(Arrays.asList("v1", "v2"));
            WriteSheet sheet = FesodSheet.writerSheet().build();
            writer.write(data, sheet);
        }
    }
}
