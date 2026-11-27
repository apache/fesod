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

package org.apache.fesod.quarkus.runtime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import org.apache.fesod.sheet.ExcelReader;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.read.listener.PageReadListener;

@ApplicationScoped
public class SpreadsheetReader {

    private final int batchSize;

    @Inject
    public SpreadsheetReader(FesodConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }
        this.batchSize = config.batchSize();
        if (this.batchSize < 1) {
            throw new IllegalArgumentException("quarkus.fesod.batch-size must be positive");
        }
    }

    /**
     * Reads the first worksheet into typed rows, delivering batches to the consumer on the calling thread.
     * The caller owns the input stream and is responsible for closing it.
     */
    public <T> void readFirstSheet(InputStream input, Class<T> rowType, Consumer<List<T>> consumer) {
        if (input == null || rowType == null || consumer == null) {
            throw new IllegalArgumentException("input, rowType and consumer are required");
        }
        try (ExcelReader reader = FesodSheet.read(input, rowType, new PageReadListener<>(consumer, batchSize))
                .autoCloseStream(false)
                .build()) {
            reader.read(FesodSheet.readSheet(0).build());
        }
    }
}
