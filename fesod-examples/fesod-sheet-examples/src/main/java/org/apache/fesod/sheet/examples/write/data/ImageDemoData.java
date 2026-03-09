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

package org.apache.fesod.sheet.examples.write.data;

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
 * Data model demonstrating five ways to export images to Excel cells.
 *
 * <h2>Image Source Types</h2>
 * <p>Each field represents a different way to provide image data to Fesod:</p>
 * <pre>
 * Field             | Type                  | Description
 * ───────────────────|───────────────────────|────────────────────────────────────────
 * file              | File                  | Local file reference
 * inputStream       | InputStream           | Stream from any source
 * string            | String                | File path (requires StringImageConverter)
 * byteArray         | byte[]                | Raw image bytes
 * url               | URL                   | Remote image URL
 * writeCellDataFile | WriteCellData&lt;Void&gt;   | Advanced: multiple images + text in one cell
 * </pre>
 *
 * <h2>Layout Annotations</h2>
 * <ul>
 *   <li>{@code @ContentRowHeight(100)} — Sets row height to 100 points for image visibility.</li>
 *   <li>{@code @ColumnWidth(100 / 8)} — Sets column width (in characters, ~12.5) for image cells.</li>
 * </ul>
 *
 * <h2>Memory Warning</h2>
 * <p>All images are loaded into memory. For large volumes, consider:
 * <ul>
 *   <li>Uploading to cloud storage and using URL references.</li>
 *   <li>Compressing images before export.</li>
 * </ul>
 *
 * @see org.apache.fesod.sheet.examples.write.ImageWriteExample
 * @see StringImageConverter
 * @see WriteCellData
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
     * If string type, a converter must be specified.
     */
    @ExcelProperty(converter = StringImageConverter.class)
    private String string;

    private byte[] byteArray;
    /**
     * Export by URL.
     */
    private URL url;

    /**
     * Export by file and set the export position.
     */
    private WriteCellData<Void> writeCellDataFile;
}
