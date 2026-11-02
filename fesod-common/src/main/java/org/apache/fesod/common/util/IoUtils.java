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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for working with byte streams.
 *
 * <p>All methods are static and this class cannot be instantiated. No method closes the streams it
 * is given, so releasing the stream stays the caller's responsibility.
 *
 * <p>Typical usage:
 *
 * <pre>
 *   try (InputStream in = Files.newInputStream(path)) {
 *       byte[] content = IoUtils.toByteArray(in);
 *   }
 * </pre>
 */
public class IoUtils {
    /**
     * The value ({@value}) returned by {@link InputStream#read()} and {@link InputStream#read(byte[])}
     * to signal that no more bytes are available, i.e. the end of the stream has been reached.
     */
    public static final int EOF = -1;

    /**
     * The size of the buffer (4 KiB) used to move bytes from one stream to another.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Utility classes must never be instantiated.
     */
    private IoUtils() {}

    /**
     * Reads the whole remaining content of a stream into a newly allocated byte array.
     *
     * <p>The stream is drained until it reports {@link #EOF}, therefore the length is only known afterward. The stream is not closed by this method.
     *
     * <pre>
     *   IoUtils.toByteArray(new ByteArrayInputStream(new byte[] {1, 2})) = {1, 2}
     *   IoUtils.toByteArray(new ByteArrayInputStream(new byte[0]))       = {}
     * </pre>
     *
     * @param input the stream to drain, must not be {@code null}
     * @return a byte array holding every byte read from {@code input}, empty if nothing was
     * available
     * @throws IOException if the stream cannot be read
     * @see #copy(InputStream, OutputStream)
     * @see #toByteArray(InputStream, int)
     */
    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Reads exactly {@code size} bytes from a stream into a newly allocated byte array.
     *
     * <p>Prefer this overload over {@link #toByteArray(InputStream)} when the length is already
     * known, since the result can be allocated once instead of growing a buffer while reading. The
     * stream is not closed and is only read up to the requested amount, so it can still be used
     * afterward.
     *
     * @param input the stream to read from, must not be {@code null}
     * @param size the exact number of bytes to read; {@code 0} is legal and yields an empty array
     * @return a byte array of length {@code size}
     * @throws IllegalArgumentException if {@code size} is negative
     * @throws IOException if the stream reaches {@link #EOF} before {@code size} bytes were read,
     * or if it cannot be read
     */
    public static byte[] toByteArray(final InputStream input, final int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }
        if (size == 0) {
            return new byte[0];
        }
        final byte[] data = new byte[size];
        int offset = 0;
        int read;
        while (offset < size && (read = input.read(data, offset, size - offset)) != EOF) {
            offset += read;
        }
        if (offset != size) {
            throw new IOException("Unexpected read size. current: " + offset + ", expected: " + size);
        }
        return data;
    }

    /**
     * Copies all remaining bytes from one stream to another using a 4 KiB buffer.
     *
     * <p>Neither stream is closed by this method, and {@code output} is not flushed either, so the
     * caller has to flush or close it to make the copied bytes visible.
     *
     * @param input the stream to read from, must not be {@code null}
     * @param output the stream to write to, must not be {@code null}
     * @return the number of bytes copied, or {@code -1} if more than {@link Integer#MAX_VALUE} bytes
     * were copied and the count no longer fits into an {@code int}
     * @throws IOException if {@code input} cannot be read or {@code output} cannot be written
     */
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
}
