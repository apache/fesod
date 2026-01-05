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

package org.apache.fesod.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link IoUtils}
 */
class IoUtilsTest {

    private byte[] data;
    private static final int DATA_SIZE = 1024 * 4 + 10;

    @BeforeEach
    void setup() {
        data = new byte[DATA_SIZE];
        new Random().nextBytes(data);
    }

    @Test
    void test_toByteArray() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        byte[] actual = IoUtils.toByteArray(inputStream);

        Assertions.assertArrayEquals(data, actual);
    }

    @Test
    void test_toByteArray_with_size() throws IOException {
        int size = DATA_SIZE - 10;
        InputStream inputStream = new ByteArrayInputStream(data);

        byte[] actual = IoUtils.toByteArray(inputStream, size);

        byte[] expected = Arrays.copyOf(data, size);
        Assertions.assertEquals(size, actual.length);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    void test_toByteArray_with_zero_size() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(data);
        byte[] actual = IoUtils.toByteArray(inputStream, 0);
        Assertions.assertEquals(0, actual.length);
    }

    @Test
    void test_toByteArray_size_mismatch_exception() {
        byte[] smallData = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(smallData);

        IOException exception = Assertions.assertThrows(IOException.class, () -> {
            IoUtils.toByteArray(inputStream, 20);
        });
        Assertions.assertTrue(exception.getMessage().contains("Unexpected read size"));
    }

    @Test
    void test_toByteArray_negative_size() {
        InputStream inputStream = new ByteArrayInputStream(data);
        Assertions.assertThrows(IllegalArgumentException.class, () -> IoUtils.toByteArray(inputStream, -1));
    }

    @Test
    void test_copy_stream() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int bytesCopied = IoUtils.copy(inputStream, out);

        Assertions.assertEquals(data.length, bytesCopied);
        Assertions.assertArrayEquals(data, out.toByteArray());
    }

    @Test
    void test_copy_empty_stream() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int bytesCopied = IoUtils.copy(inputStream, out);

        Assertions.assertEquals(0, bytesCopied);
        Assertions.assertEquals(0, out.size());
    }
}
