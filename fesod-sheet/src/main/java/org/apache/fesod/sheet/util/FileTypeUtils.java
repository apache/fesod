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

package org.apache.fesod.sheet.util;

import java.util.EnumMap;
import java.util.Map;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.apache.poi.poifs.filesystem.FileMagic;

/**
 * file type utils
 *
 *
 */
public class FileTypeUtils {

    private static final int IMAGE_TYPE_MARK_MIN_LENGTH = 3;

    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47};

    private static final Map<FileMagic, ImageData.ImageType> FILE_TYPE_MAP;

    /**
     * Default image type
     */
    public static ImageData.ImageType defaultImageType = ImageData.ImageType.PICTURE_TYPE_PNG;

    static {
        FILE_TYPE_MAP = new EnumMap<>(FileMagic.class);
        FILE_TYPE_MAP.put(FileMagic.JPEG, ImageData.ImageType.PICTURE_TYPE_JPEG);
        FILE_TYPE_MAP.put(FileMagic.PNG, ImageData.ImageType.PICTURE_TYPE_PNG);
        FILE_TYPE_MAP.put(FileMagic.WMF, ImageData.ImageType.PICTURE_TYPE_WMF);
        FILE_TYPE_MAP.put(FileMagic.EMF, ImageData.ImageType.PICTURE_TYPE_EMF);
        FILE_TYPE_MAP.put(FileMagic.BMP, ImageData.ImageType.PICTURE_TYPE_DIB);
    }

    public static int getImageTypeFormat(byte[] image) {
        ImageData.ImageType imageType = getImageType(image);
        if (imageType != null) {
            return imageType.getValue();
        }
        return defaultImageType.getValue();
    }

    public static ImageData.ImageType getImageType(byte[] image) {
        if (image == null || image.length < IMAGE_TYPE_MARK_MIN_LENGTH) {
            return null;
        }
        if (startsWith(image, JPEG_SIGNATURE)) {
            return ImageData.ImageType.PICTURE_TYPE_JPEG;
        }
        if (startsWith(image, PNG_SIGNATURE)) {
            return ImageData.ImageType.PICTURE_TYPE_PNG;
        }
        ImageData.ImageType imageType = FILE_TYPE_MAP.get(FileMagic.valueOf(image));
        if (imageType != null) {
            return imageType;
        }
        if (isDib(image)) {
            return ImageData.ImageType.PICTURE_TYPE_DIB;
        }
        return null;
    }

    private static boolean startsWith(byte[] image, byte[] signature) {
        if (image.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (image[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isDib(byte[] image) {
        if (image.length < 4) {
            return false;
        }
        int headerSize = readLittleEndianInt(image);
        switch (headerSize) {
            case 12:
            case 40:
            case 52:
            case 56:
            case 108:
            case 124:
                return image.length >= headerSize;
            default:
                return false;
        }
    }

    private static int readLittleEndianInt(byte[] data) {
        return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 24);
    }
}
