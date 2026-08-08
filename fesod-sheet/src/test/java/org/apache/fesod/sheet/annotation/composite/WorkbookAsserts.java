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

package org.apache.fesod.sheet.annotation.composite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;

/**
 * A simple assertion tool for workbooks.
 */
class WorkbookAsserts {

    private final List<FileMetadata> list;

    WorkbookAsserts(List<FileMetadata> list) {
        this.list = list;
    }

    private static class FileMetadata {
        final File file;
        final String label;

        FileMetadata(File file, String label) {
            this.file = file;
            this.label = label;
        }
    }

    static WorkbookAsserts build(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Arguments must be pairs of Label (String) and File (File)");
        }
        List<FileMetadata> files = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            files.add(new FileMetadata((File) args[i], (String) args[i + 1]));
        }
        return new WorkbookAsserts(files);
    }

    void assertMulti(BiConsumer<String, Workbook> consumer) {
        for (FileMetadata metadata : list) {
            try (Workbook workbook = WorkbookFactory.create(metadata.file)) {
                consumer.accept(metadata.label, workbook);
            } catch (Exception ex) {
                Assertions.fail("Failed to process workbook [" + metadata.label + "]", ex);
            }
        }
    }
}
