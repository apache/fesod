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

package org.apache.fesod.excel.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.fesod.excel.FastExcelFactory;
import org.apache.fesod.excel.read.builder.ExcelReaderBuilder;

/**
 * Fuzzes the generic read path with arbitrary bytes to discover parsing issues.
 */
public class ExcelReadFuzzTest {

    private static final int MAX_SIZE = 1_000_000; // 1MB guard to avoid OOM / long loops

    @SneakyThrows
    @FuzzTest
    void fuzzRead(byte[] data) {
        if (data == null || data.length == 0 || data.length > MAX_SIZE) {
            return; // Ignore trivial or oversized inputs
        }
        try (InputStream in = new ByteArrayInputStream(data)) {
            ExcelReaderBuilder builder = FastExcelFactory.read(in);
            // Always attempt to read first sheet synchronously if possible
            builder.sheet().doReadSync();
        } catch (Throwable t) {
            // Jazzer treats uncaught exceptions as findings. We allow RuntimeExceptions that
            // indicate expected format errors, but still surface anything else.
            // Swallow common benign exceptions to reduce noise.
            String msg = t.getMessage();
            if (msg != null) {
                String lower = msg.toLowerCase();
                if (lower.contains("invalid")
                        || lower.contains("zip")
                        || lower.contains("format")
                        || lower.contains("end of central directory")) {
                    return; // expected parse/format issues
                }
            }
            throw t;
        }
    }
}
