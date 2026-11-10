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

package org.apache.fesod.sheet.write.metadata.holder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.models.SimpleData;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.apache.fesod.sheet.write.metadata.WriteWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for the output stream lifecycle of {@link WriteWorkbookHolder} when workbook
 * initialization fails.
 */
public class WriteWorkbookHolderOutputStreamTest {

    @TempDir
    Path tempDir;

    @Test
    void constructorClosesOutputFileStreamWhenTemplateCopyFails() throws IOException {
        File outputFile = tempDir.resolve("output.xlsx").toFile();
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setFile(outputFile);
        writeWorkbook.setTemplateFile(tempDir.resolve("missing-template.xlsx").toFile());

        Assertions.assertThrows(ExcelGenerateException.class, () -> new WriteWorkbookHolder(writeWorkbook));
        // On Windows an open FileOutputStream locks the file, so deletion fails if the holder
        // leaked the stream it opened. On Unix the deletion succeeds either way, keeping the
        // assertion portable.
        Assertions.assertTrue(outputFile.delete(), "output stream should be closed after initialization failure");
    }

    @Test
    void constructorDoesNotCloseCallerProvidedOutputStreamWhenAutoCloseDisabled() throws IOException {
        WriteWorkbook writeWorkbook = new WriteWorkbook();
        writeWorkbook.setAutoCloseStream(false);
        writeWorkbook.setOutputStream(new ByteArrayOutputStream());
        writeWorkbook.setTemplateFile(tempDir.resolve("missing-template.xlsx").toFile());

        Assertions.assertThrows(ExcelGenerateException.class, () -> new WriteWorkbookHolder(writeWorkbook));
        // autoCloseStream(false) opts into manual stream management, so the holder must not close
        // caller-provided streams — mirroring the success-path contract.
        Assertions.assertDoesNotThrow(() -> writeWorkbook.getOutputStream().write(1));
    }

    @Test
    void writeRejectsSameTemplateAndOutputFileWithoutTruncatingTemplate() throws IOException {
        File template = tempDir.resolve("same-file-template.xlsx").toFile();
        FesodSheet.write(template, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(1));
        byte[] before = Files.readAllBytes(template.toPath());

        ExcelGenerateException exception =
                Assertions.assertThrows(ExcelGenerateException.class, () -> FesodSheet.write(template, SimpleData.class)
                        .withTemplate(template)
                        .sheet()
                        .doWrite(TestDataBuilder.simpleData(1)));

        Assertions.assertTrue(exception.getMessage().startsWith("Template file and output file must be different"));
        Assertions.assertArrayEquals(before, Files.readAllBytes(template.toPath()));
    }

    @Test
    void writeAllowsDistinctTemplateAndOutputFiles() {
        File template = tempDir.resolve("template.xlsx").toFile();
        File output = tempDir.resolve("output.xlsx").toFile();
        FesodSheet.write(template, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(1));

        FesodSheet.write(output, SimpleData.class)
                .withTemplate(template)
                .sheet()
                .doWrite(TestDataBuilder.simpleData(1));

        Assertions.assertTrue(output.length() > 0);
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void writeRejectsHardLinkedOutputWithoutTruncatingTemplate(ExcelFormat format) throws Exception {
        for (boolean inMemory : new boolean[] {false, true}) {
            File template = format.createTempFile("hard-link-template", tempDir.toFile());
            FesodSheet.write(template, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(1));
            Path output = tempDir.resolve("hard-linked-" + format.name() + "-" + inMemory
                    + format.toExcelTypeEnum().getValue());
            Files.createLink(output, template.toPath());
            byte[] before = Files.readAllBytes(template.toPath());

            ExcelGenerateException failure = Assertions.assertThrows(
                    ExcelGenerateException.class, () -> FesodSheet.write(output.toFile(), SimpleData.class)
                            .withTemplate(template)
                            .inMemory(inMemory)
                            .build());

            Assertions.assertArrayEquals(before, Files.readAllBytes(template.toPath()));
            Assertions.assertArrayEquals(before, Files.readAllBytes(output));
            Assertions.assertTrue(failure.getMessage().contains(template.getAbsolutePath()));
            Assertions.assertTrue(
                    failure.getMessage().contains(output.toAbsolutePath().toString()));
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void writeRejectsSymbolicLinkOutputWithoutTruncatingTemplate(ExcelFormat format) throws Exception {
        File template = format.createTempFile("symbolic-link-template", tempDir.toFile());
        FesodSheet.write(template, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(1));
        Path output = tempDir.resolve(
                "symbolic-linked-" + format.name() + format.toExcelTypeEnum().getValue());
        try {
            Files.createSymbolicLink(output, template.toPath());
        } catch (UnsupportedOperationException | IOException e) {
            Assumptions.assumeTrue(false, "Symbolic links are unavailable: " + e.getMessage());
        }
        byte[] before = Files.readAllBytes(template.toPath());

        Assertions.assertThrows(ExcelGenerateException.class, () -> FesodSheet.write(output.toFile(), SimpleData.class)
                .withTemplate(template)
                .build());

        Assertions.assertArrayEquals(before, Files.readAllBytes(template.toPath()));
        Assertions.assertArrayEquals(before, Files.readAllBytes(output));
    }

    @ParameterizedTest
    @ExcelFormatSource(FormatScope.BINARY)
    void writeAllowsNewAndExistingDistinctOutputs(ExcelFormat format) throws Exception {
        File template = format.createTempFile("distinct-template", tempDir.toFile());
        FesodSheet.write(template, SimpleData.class).sheet().doWrite(TestDataBuilder.simpleData(1));
        byte[] before = Files.readAllBytes(template.toPath());
        for (boolean existing : new boolean[] {false, true}) {
            Path output = tempDir.resolve("distinct-" + existing + "-" + format.name()
                    + format.toExcelTypeEnum().getValue());
            if (existing) {
                Files.write(output, new byte[] {1, 2, 3});
            }
            FesodSheet.write(output.toFile(), SimpleData.class)
                    .withTemplate(template)
                    .sheet()
                    .doWrite(TestDataBuilder.simpleData(1));

            try (Workbook workbook = WorkbookFactory.create(output.toFile())) {
                Assertions.assertEquals(1, workbook.getNumberOfSheets());
            }
            Assertions.assertArrayEquals(before, Files.readAllBytes(template.toPath()));
        }
    }
}
