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

package org.apache.fesod.excel.metadata.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * image
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class ImageData extends ClientAnchorData {

    /**
     * image
     */
    private byte[] image;

    /**
     * image type
     */
    private ImageType imageType;

    @Getter
    public enum ImageType {
        /**
         * Extended windows meta file
         */
        PICTURE_TYPE_EMF(2),
        /**
         * Windows Meta File
         */
        PICTURE_TYPE_WMF(3),
        /**
         * Mac PICT format
         */
        PICTURE_TYPE_PICT(4),
        /**
         * JPEG format
         */
        PICTURE_TYPE_JPEG(5),
        /**
         * PNG format
         */
        PICTURE_TYPE_PNG(6),
        /**
         * Device independent bitmap
         */
        PICTURE_TYPE_DIB(7),
        ;

        int value;

        ImageType(int value) {
            this.value = value;
        }
    }
}
