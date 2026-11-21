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

package org.apache.fesod.sheet.format;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Writing CSV must honor the writer's stream-ownership contract: with {@code autoCloseStream(false)} the caller keeps
 * the stream, and output failures must surface instead of being swallowed by the PrintWriter that wraps the stream.
 */
@Tag(Tags.WRITE)
public class CsvWriteStreamOwnershipTest extends AbstractExcelTest {

    @Test
    public void writeWithAutoCloseStreamFalseKeepsCallerStreamOpen() {
        TrackingOutputStream out = new TrackingOutputStream();

        FesodSheet.write(out).autoCloseStream(false).csv().doWrite(data());

        Assertions.assertFalse(out.closed, "autoCloseStream(false) leaves the stream to the caller");
        Assertions.assertTrue(out.content().contains("name,note"), "the data itself must still be written");
    }

    @Test
    public void writeWithAutoCloseStreamTrueClosesTheStream() {
        TrackingOutputStream out = new TrackingOutputStream();

        FesodSheet.write(out).autoCloseStream(true).csv().doWrite(data());

        Assertions.assertTrue(out.closed, "the default path still closes the caller's stream");
    }

    @Test
    public void writeSurfacesOutputErrors() {
        OutputStream broken = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("disk full");
            }
        };

        Assertions.assertThrows(
                ExcelGenerateException.class,
                () -> FesodSheet.write(broken).csv().doWrite(data()),
                "a failing output stream must fail the write instead of finishing silently");
    }

    private List<List<Object>> data() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("name", "note"));
        rows.add(Arrays.asList("plain", "value"));
        return rows;
    }

    private static class TrackingOutputStream extends OutputStream {
        private boolean closed;
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public void write(int b) {
            buffer.append((char) b);
        }

        @Override
        public void close() {
            closed = true;
        }

        String content() {
            return buffer.toString();
        }
    }
}
