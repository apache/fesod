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

package org.apache.fesod.sheet.testkit.assertions;

import java.io.File;
import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Entry point for the fluent Excel assertion API.
 *
 * <p>Opens a workbook from a file and provides chainable assertions on its contents.
 * Implements {@link AutoCloseable} to ensure the workbook is closed after assertions.
 *
 * <p>Usage:
 * <pre>{@code
 * try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
 *     ea.sheet(0).row(0).cell(0).hasStringValue("Name");
 * }
 * }</pre>
 */
public final class ExcelAssertions implements AutoCloseable {

    private final Workbook workbook;
    private final File file;

    private ExcelAssertions(Workbook workbook, File file) {
        this.workbook = workbook;
        this.file = file;
    }

    /**
     * Opens the given Excel file and returns a fluent assertion entry point.
     *
     * @param file the Excel file to assert against
     * @return a new {@code ExcelAssertions} instance
     * @throws AssertionError if the file does not exist or cannot be opened as a workbook
     */
    public static ExcelAssertions assertThat(File file) {
        if (!file.exists()) {
            throw new AssertionError("File does not exist: " + file.getAbsolutePath());
        }
        try {
            Workbook wb = WorkbookFactory.create(file);
            return new ExcelAssertions(wb, file);
        } catch (IOException | EncryptedDocumentException e) {
            throw new AssertionError("Failed to open workbook: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Returns a {@link WorkbookAssert} for the underlying workbook.
     */
    public WorkbookAssert workbook() {
        return new WorkbookAssert(workbook, this);
    }

    /**
     * Shortcut to assert on a specific sheet by index.
     */
    public SheetAssert sheet(int index) {
        return workbook().sheet(index);
    }

    Workbook getWorkbook() {
        return workbook;
    }

    File getFile() {
        return file;
    }

    @Override
    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            // best effort
        }
    }
}
