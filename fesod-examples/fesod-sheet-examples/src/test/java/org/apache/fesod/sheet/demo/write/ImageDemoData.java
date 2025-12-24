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

package org.apache.fesod.sheet.demo.write;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;
import org.apache.fesod.sheet.annotation.write.style.ContentRowHeight;
import org.apache.fesod.sheet.converters.string.StringImageConverter;
import org.apache.fesod.sheet.metadata.data.WriteCellData;

/**
 * Image export class
 */
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(100)
@ColumnWidth(100 / 8)
public class ImageDemoData {
    private File file;
    private InputStream inputStream;
    /**
     * If it is a String type, a converter must be specified; String is converted to String by default.
     */
    @ExcelProperty(converter = StringImageConverter.class)
    private String string;

    private byte[] byteArray;
    /**
     * Export by URL
     *
     * @since 2.1.1
     */
    private URL url;

    /**
     * Export by file and set the export location.
     *
     * @since 3.0.0-beta1
     */
    private WriteCellData<Void> writeCellDataFile;
}
