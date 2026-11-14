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

package org.apache.fesod.sheet.write.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.metadata.data.CommentData;
import org.apache.fesod.sheet.metadata.data.RichTextStringData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Verifies that a comment stays on the cell its anchor points at.
 *
 * <p>Before this change the comment was always re-attached to the cell that carries the data, so an
 * explicit absolute anchor made the rendered position and the owning cell disagree, and POI
 * rejected the comment because the anchor cell already had one.
 */
@Tag(Tags.WRITE)
class CommentAnchorOwnershipTest extends AbstractExcelTest {

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void comment_without_coordinates_stays_on_its_cell(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-plain-", format);
        write(file, format, null, null);

        Assertions.assertEquals(Collections.singleton("B2"), readCommentCells(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void comment_follows_the_absolute_anchor(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-anchor-", format);
        write(file, format, null, 0);

        Assertions.assertEquals(Collections.singleton("A1"), readCommentCells(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void comment_follows_relative_coordinates(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-relative-", format);
        CommentData data = comment("relative");
        data.setRelativeFirstRowIndex(1);
        data.setRelativeFirstColumnIndex(1);
        data.setRelativeLastRowIndex(1);
        data.setRelativeLastColumnIndex(1);

        WriteCellData<String> b2 = new WriteCellData<>("B2");
        b2.setCommentData(data);
        writeRows(file, format, "A1", b2, "C3", false);

        // The data cell is B2 (1, 1), so a +1 relative offset lands on C3.
        Assertions.assertEquals(Collections.singleton("C3"), readCommentCells(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void comment_falls_back_when_the_absolute_index_is_negative(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-negative-", format);
        write(file, format, null, -1);

        // A negative index is not a valid cell, so the data cell B2 is used instead.
        Assertions.assertEquals(Collections.singleton("B2"), readCommentCells(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void two_comments_on_different_cells_do_not_interfere(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-two-", format);
        WriteCellData<String> a1 = new WriteCellData<>("A1");
        a1.setCommentData(comment("first"));
        WriteCellData<String> c3 = new WriteCellData<>("C3");
        c3.setCommentData(comment("second"));
        writeRows(file, format, a1, "B2", c3, false);

        Assertions.assertEquals(new LinkedHashSet<>(Arrays.asList("A1", "C3")), readCommentCells(file));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void anchor_and_ownership_agree_for_an_absolute_anchor(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-agree-", format);
        write(file, format, null, 0);

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<CellAddress, ? extends Comment> comments = sheet.getCellComments();
            Assertions.assertEquals(1, comments.size(), "expected exactly one comment");
            Map.Entry<CellAddress, ? extends Comment> entry =
                    comments.entrySet().iterator().next();
            Assertions.assertEquals("A1", entry.getKey().formatAsString());
            ClientAnchor anchor = entry.getValue().getClientAnchor();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(0, anchor.getRow1(), "anchor row1"),
                    () -> Assertions.assertEquals(0, anchor.getCol1(), "anchor col1"));
        }
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void two_comments_on_the_same_cell_are_rejected_in_memory(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-dup-", format);
        RuntimeException thrown =
                Assertions.assertThrows(RuntimeException.class, () -> write(file, format, "comment on A1", 0, true));
        Assertions.assertTrue(
                messagesOf(thrown).toLowerCase().contains("multiple cell comments"),
                "expected the duplicate comment message, got: " + messagesOf(thrown));
    }

    @ParameterizedTest
    @EnumSource(
            value = ExcelFormat.class,
            names = {"XLSX", "XLS"})
    void two_comments_on_the_same_cell_are_rejected_while_streaming(ExcelFormat format) throws IOException {
        File file = createTempFile("comment-dup-stream-", format);
        // Streaming writes wrap the underlying failure, so only assert that the write fails.
        Assertions.assertThrows(RuntimeException.class, () -> write(file, format, "comment on A1", 0, false));
    }

    private static void write(File file, ExcelFormat format, String commentOnA1, Integer b2Anchor) {
        write(file, format, commentOnA1, b2Anchor, false);
    }

    private static void write(File file, ExcelFormat format, String commentOnA1, Integer b2Anchor, boolean inMemory) {
        Object a1 = commentOnA1 == null ? "A1" : cellWithComment("A1", null, commentOnA1);
        WriteCellData<String> b2 = new WriteCellData<>("B2");
        b2.setCommentData(comment("comment on B2", b2Anchor));
        writeRows(file, format, a1, b2, "C3", inMemory);
    }

    private static void writeRows(File file, ExcelFormat format, Object a1, Object b2, Object c3, boolean inMemory) {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(a1, "B1", "C1"));
        rows.add(Arrays.asList("A2", b2, "C2"));
        rows.add(Arrays.asList("A3", "B3", c3));
        FesodSheet.write(file)
                .excelType(format.toExcelTypeEnum())
                .inMemory(inMemory)
                .sheet()
                .doWrite(rows);
    }

    private static WriteCellData<String> cellWithComment(String text, Integer anchor, String commentText) {
        WriteCellData<String> cell = new WriteCellData<>(text);
        cell.setCommentData(comment(commentText, anchor));
        return cell;
    }

    private static CommentData comment(String text) {
        return comment(text, null);
    }

    private static CommentData comment(String text, Integer anchor) {
        CommentData data = new CommentData();
        data.setAuthor("fesod");
        data.setRichTextStringData(new RichTextStringData(text));
        if (anchor != null) {
            data.setFirstRowIndex(anchor);
            data.setFirstColumnIndex(anchor);
            data.setLastRowIndex(anchor);
            data.setLastColumnIndex(anchor);
        }
        return data;
    }

    private static Set<String> readCommentCells(File file) throws IOException {
        Set<String> cells = new LinkedHashSet<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (CellAddress address : sheet.getCellComments().keySet()) {
                cells.add(address.formatAsString());
            }
        }
        return cells;
    }

    private static String messagesOf(Throwable throwable) {
        StringBuilder messages = new StringBuilder();
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current.getMessage() != null) {
                messages.append(current.getMessage()).append(" | ");
            }
        }
        return messages.toString();
    }
}
