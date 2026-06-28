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

package org.apache.fesod.sheet.testkit.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.listeners.CollectingReadListener;

/**
 * Encapsulates write-then-read boilerplate for round-trip tests.
 *
 * <p>Provides File-based, Stream-based, and sync read helpers that eliminate
 * the repetitive write/read code found in every existing test class.
 */
public final class RoundTripHelper {

    private RoundTripHelper() {}

    // ---- Same model for write and read ----

    /**
     * Writes data to file, then reads it back using the same model class.
     */
    public static <T> List<T> writeAndRead(File file, Class<T> clazz, List<? extends T> data) {
        write(file, clazz, data);
        return read(file, clazz);
    }

    // ---- Different write/read models ----

    /**
     * Writes data using {@code writeClazz}, then reads it back using {@code readClazz}.
     * Supports asymmetric models (e.g., WriteCellData vs ReadCellData).
     */
    public static <W, R> List<R> writeAndRead(File file, Class<W> writeClazz, List<W> data, Class<R> readClazz) {
        write(file, writeClazz, data);
        return read(file, readClazz);
    }

    // ---- Stream-based round trip ----

    /**
     * Writes data via OutputStream, then reads via InputStream.
     * Requires explicit {@code ExcelFormat} because the stream API needs the type hint.
     */
    public static <T> List<T> writeAndReadViaStream(
            File file, ExcelFormat format, Class<T> clazz, List<? extends T> data) throws Exception {
        try (OutputStream os = new FileOutputStream(file)) {
            FesodSheet.write(os, clazz)
                    .excelType(format.toExcelTypeEnum())
                    .sheet()
                    .doWrite(data);
        }
        try (InputStream is = new FileInputStream(file)) {
            CollectingReadListener<T> listener = new CollectingReadListener<T>();
            FesodSheet.read(is, clazz, listener)
                    .excelType(format.toExcelTypeEnum())
                    .sheet()
                    .doRead();
            return listener.getRows();
        }
    }

    /**
     * Writes data via OutputStream, then reads using a different model via InputStream.
     */
    public static <W, R> List<R> writeAndReadViaStream(
            File file, ExcelFormat format, Class<W> writeClazz, List<W> data, Class<R> readClazz) throws Exception {
        try (OutputStream os = new FileOutputStream(file)) {
            FesodSheet.write(os, writeClazz)
                    .excelType(format.toExcelTypeEnum())
                    .sheet()
                    .doWrite(data);
        }
        try (InputStream is = new FileInputStream(file)) {
            CollectingReadListener<R> listener = new CollectingReadListener<R>();
            FesodSheet.read(is, readClazz, listener)
                    .excelType(format.toExcelTypeEnum())
                    .sheet()
                    .doRead();
            return listener.getRows();
        }
    }

    // ---- Sync read round trip ----

    /**
     * Writes data to file, then reads synchronously via {@code doReadSync()}.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> writeAndReadSync(File file, Class<T> clazz, List<? extends T> data) {
        write(file, clazz, data);
        List<Object> raw = FesodSheet.read(file).head(clazz).sheet().doReadSync();
        List<T> result = new ArrayList<T>(raw.size());
        for (Object obj : raw) {
            result.add((T) obj);
        }
        return result;
    }

    // ---- API-mode-aware round trip ----

    /**
     * Dispatches to File-based or Stream-based round-trip based on {@code mode}.
     */
    public static <T> List<T> writeAndRead(
            File file, ExcelFormat format, ApiMode mode, Class<T> clazz, List<? extends T> data) throws Exception {
        if (mode == ApiMode.STREAM) {
            return writeAndReadViaStream(file, format, clazz, data);
        }
        return writeAndRead(file, clazz, data);
    }

    /**
     * Dispatches to File-based or Stream-based round-trip with asymmetric models.
     */
    public static <W, R> List<R> writeAndRead(
            File file, ExcelFormat format, ApiMode mode, Class<W> writeClazz, List<W> data, Class<R> readClazz)
            throws Exception {
        if (mode == ApiMode.STREAM) {
            return writeAndReadViaStream(file, format, writeClazz, data, readClazz);
        }
        return writeAndRead(file, writeClazz, data, readClazz);
    }

    // ---- Primitives ----

    /**
     * Writes data to a file using the File-based API.
     */
    public static <T> void write(File file, Class<T> clazz, List<? extends T> data) {
        FesodSheet.write(file, clazz).sheet().doWrite(data);
    }

    /**
     * Reads data from a file using the File-based API with a {@link CollectingReadListener}.
     */
    public static <T> List<T> read(File file, Class<T> clazz) {
        CollectingReadListener<T> listener = new CollectingReadListener<T>();
        FesodSheet.read(file, clazz, listener).sheet().doRead();
        return listener.getRows();
    }
}
