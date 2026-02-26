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

package org.apache.fesod.sheet.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/**
 * Tests {@link FileTypeUtils}
 */
class FileTypeUtilsTest {

    private byte[] realJpeg;
    private byte[] realPng;
    private byte[] realSvg;

    @BeforeEach
    void setup() throws Exception {
        realJpeg = loadImage("fesod-logo-jpeg.jpeg");
        realPng = loadImage("fesod-logo-png.png");
        realSvg = loadImage("fesod-logo-svg.svg");
    }

    private byte[] loadImage(String filename) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("images" + File.separator + filename); ) {
            Assertions.assertNotNull(is);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int n;
            while ((n = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, n);
            }
            return buffer.toByteArray();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void test_getImageType_NullOrEmpty(byte[] input) {
        Assertions.assertNull(FileTypeUtils.getImageType(input));
    }

    @Test
    void test_getImageType_tooShort() {
        byte[] input = new byte[] {(byte) 0x00, (byte) 0x01};
        Assertions.assertNull(FileTypeUtils.getImageType(input));
    }

    @Test
    void test_getImageType_safeCopy() {
        // JPEG
        byte[] input = new byte[10];
        input[0] = (byte) 0xFF;
        input[1] = (byte) 0xD8;
        input[2] = (byte) 0xFF;
        input[3] = (byte) 0xE0;

        Assertions.assertDoesNotThrow(() -> {
            ImageData.ImageType type = FileTypeUtils.getImageType(input);
            Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_JPEG, type);
        });
    }

    @Test
    void test_getImageType_JPEG() {
        // JPEG: ffd8ff
        byte[] jpeg = new byte[30];
        jpeg[0] = (byte) 0xFF;
        jpeg[1] = (byte) 0xD8;
        jpeg[2] = (byte) 0xFF;

        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_JPEG, FileTypeUtils.getImageType(jpeg));
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_JPEG, FileTypeUtils.getImageType(realJpeg));
    }

    @Test
    void test_getImageType_PNG() {
        // PNG: 89504e47
        byte[] png = new byte[30];
        png[0] = (byte) 0x89;
        png[1] = (byte) 0x50;
        png[2] = (byte) 0x4E;
        png[3] = (byte) 0x47;

        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_PNG, FileTypeUtils.getImageType(png));
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_PNG, FileTypeUtils.getImageType(realPng));
    }

    @Test
    void test_getImageType_unknown() {
        byte[] unknown = new byte[30];
        Assertions.assertNull(FileTypeUtils.getImageType(unknown));
        Assertions.assertNull(FileTypeUtils.getImageType(realSvg));
    }

    @Test
    void test_getImageTypeFormat_success() {
        byte[] jpeg = new byte[30];
        jpeg[0] = (byte) 0xFF;
        jpeg[1] = (byte) 0xD8;
        jpeg[2] = (byte) 0xFF;

        int typeOfJpeg = FileTypeUtils.getImageTypeFormat(jpeg);
        int typeOfRealJpeg = FileTypeUtils.getImageTypeFormat(realJpeg);
        int typeOfPng = FileTypeUtils.getImageTypeFormat(realPng);
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_JPEG.getValue(), typeOfJpeg);
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_JPEG.getValue(), typeOfRealJpeg);
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_PNG.getValue(), typeOfPng);
    }

    @Test
    void test_getImageTypeFormat_default() {
        byte[] unknown = new byte[30];

        int result = FileTypeUtils.getImageTypeFormat(unknown);

        Assertions.assertEquals(FileTypeUtils.defaultImageType.getValue(), result);
    }

    @Test
    void test_DefaultConfig() {
        Assertions.assertEquals(ImageData.ImageType.PICTURE_TYPE_PNG, FileTypeUtils.defaultImageType);
    }
}
