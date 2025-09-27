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

package org.apache.fesod.excel.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.excel.metadata.data.ImageData;

/**
 * file type utils
 *
 *
 */
public class FileTypeUtils {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    private static final int IMAGE_TYPE_MARK_LENGTH = 28;

    private static final Map<String, ImageData.ImageType> FILE_TYPE_MAP;

    /**
     * Default image type
     */
    public static ImageData.ImageType defaultImageType = ImageData.ImageType.PICTURE_TYPE_PNG;

    static {
        FILE_TYPE_MAP = new HashMap<>();
        FILE_TYPE_MAP.put("ffd8ff", ImageData.ImageType.PICTURE_TYPE_JPEG);
        FILE_TYPE_MAP.put("89504e47", ImageData.ImageType.PICTURE_TYPE_PNG);
    }

    public static int getImageTypeFormat(byte[] image) {
        ImageData.ImageType imageType = getImageType(image);
        if (imageType != null) {
            return imageType.getValue();
        }
        return defaultImageType.getValue();
    }

    public static ImageData.ImageType getImageType(byte[] image) {
        if (image == null || image.length <= IMAGE_TYPE_MARK_LENGTH) {
            return null;
        }
        byte[] typeMarkByte = new byte[IMAGE_TYPE_MARK_LENGTH];
        System.arraycopy(image, 0, typeMarkByte, 0, IMAGE_TYPE_MARK_LENGTH);
        return FILE_TYPE_MAP.get(encodeHexStr(typeMarkByte));
    }

    private static String encodeHexStr(byte[] data) {
        final int len = data.length;
        final char[] out = new char[len << 1];
        // two characters from the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }
}
