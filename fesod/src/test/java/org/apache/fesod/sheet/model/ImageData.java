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

package org.apache.fesod.sheet.model;

import java.io.File;
import java.io.InputStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.converters.string.StringImageConverter;

/**
 * Data model for image write tests.
 * Images can be specified as File, InputStream, String (path), or byte array.
 *
 * <p>Note: Image data can only be written to Excel formats (XLSX, XLS),
 * not CSV. The InputStream should be closed after writing.</p>
 *
 * @see org.apache.fesod.sheet.converter.ImageData (deprecated)
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = {"inputStream"}) // Exclude InputStream to avoid hashCode issues
@ContentRowHeight(500)
@ColumnWidth(500 / 8)
public class ImageData {

    /**
     * Image as a File reference.
     */
    private File file;

    /**
     * Image as an InputStream.
     * Note: The caller is responsible for closing this stream.
     */
    private InputStream inputStream;

    /**
     * Image file path as a String.
     * Uses StringImageConverter for conversion.
     */
    @ExcelProperty(converter = StringImageConverter.class)
    private String string;

    /**
     * Image as raw byte array.
     */
    private byte[] byteArray;
}
