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

package org.apache.fesod.excel.csv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.fesod.excel.FastExcelFactory;
import org.apache.fesod.excel.exception.ExcelCommonException;
import org.apache.fesod.excel.read.builder.CsvReaderBuilder;
import org.junit.jupiter.api.Test;

/**
 * Tests that truncated/unfinished quoted fields in CSV are treated as benign and
 * end the current sheet gracefully without throwing to the caller.
 */
class CsvBenignErrorToleranceTest {

    @Test
    void shouldNotThrowOnUnclosedQuotedField_EOFBenign() {
        // Given: a CSV with an unclosed quoted field that triggers Commons CSV EOF inside quotes
        String csv = "col1,col2\n\"unfinished,2"; // second line has an unclosed quoted field
        ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

        // When / Then: reading should complete without throwing any exception
        assertDoesNotThrow(() -> {
            CsvReaderBuilder builder = FastExcelFactory.read(in).csv();
            // Use sync read to drive the pipeline end-to-end
            List<Object> rows = builder.doReadSync();
            // No strict assertion on content; important is no exception is thrown
            // and the reader finishes gracefully.
        });
    }

    @Test
    void shouldRethrowOnNonBenignUncheckedIOException() {
        // Given: a CSV stream that throws a non-benign IOException during read
        String csv = "a,b\n1,2\n3,4\n";
        byte[] data = csv.getBytes(StandardCharsets.UTF_8);
        InputStream throwing = new InputStream() {
            int idx = 0;

            @Override
            public int read() throws IOException {
                if (idx >= data.length) {
                    return -1;
                }
                // After consuming some bytes, simulate an IO failure with a non-benign message
                if (idx > data.length / 2) {
                    throw new IOException("Simulated IO failure");
                }
                return data[idx++];
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                // Fallback to single-byte reads to trigger our error logic reliably
                int i = read();
                if (i == -1) {
                    return -1;
                }
                b[off] = (byte) i;
                return 1;
            }
        };

        // When / Then: the pipeline should convert UncheckedIOException into ExcelAnalysisException
        assertThrows(ExcelCommonException.class, () -> {
            FastExcelFactory.read(throwing).csv().doReadSync();
        });
    }

    @Test
    void shouldReadWellFormedCsvNormally() {
        String csv = "a,b\n1,2\n3,4\n";
        ByteArrayInputStream in = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
        assertDoesNotThrow(() -> {
            CsvReaderBuilder builder = FastExcelFactory.read(in).csv();
            List<Object> rows = builder.doReadSync();
        });
    }
}
