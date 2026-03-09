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

package org.apache.fesod.sheet.examples.write;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoStyleData;

/**
 * Demonstrates how to customize header and content styles in Excel output.
 *
 * <h2>Scenario</h2>
 * <p>You're generating an Excel report that needs branded colors, custom font sizes,
 * or specific cell formatting to match corporate style guides.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Class-level annotations</b> — Apply default styles to all columns:
 *     <ul>
 *       <li>{@code @HeadStyle} / {@code @HeadFontStyle} — Header row background color and font.</li>
 *       <li>{@code @ContentStyle} / {@code @ContentFontStyle} — Data row background color and font.</li>
 *     </ul>
 *   </li>
 *   <li><b>Field-level annotations</b> — Override class-level styles for specific columns.
 *       In the example, the "String Title" column has distinct colors and a larger font
 *       than the other columns.</li>
 *   <li><b>Color values</b> — Use Excel's indexed color system (0-63). Common values:
 *       10 = dark green, 14 = dark teal, 17 = light yellow, 40 = light blue.
 *       See {@link org.apache.poi.ss.usermodel.IndexedColors} for the full palette.</li>
 * </ul>
 *
 * <h2>Style Hierarchy</h2>
 * <pre>
 * Field-level @HeadStyle / @ContentStyle
 *     │ (if present, overrides class-level)
 *     ↓
 * Class-level @HeadStyle / @ContentStyle
 *     │ (if present, overrides Fesod default)
 *     ↓
 * Fesod default styles
 * </pre>
 *
 * <h2>Expected Output</h2>
 * <p>An Excel file where:
 * <ul>
 *   <li>Header row: dark green background (color 10), 20pt font for all columns,
 *       except "String Title" which has dark teal (14) and 30pt font.</li>
 *   <li>Content rows: light yellow background (color 17), 20pt font for all columns,
 *       except "String Title" which has light blue (40) and 30pt font.</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link BasicWriteExample} — Write with default styles.</li>
 *   <li>{@link MergeWriteExample} — Combine merging with styles.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.write.handlers.CustomCellWriteHandler}
 *       — Programmatic style customization via handlers.</li>
 * </ul>
 *
 * @see DemoStyleData
 * @see org.apache.fesod.sheet.annotation.write.style.HeadStyle
 * @see org.apache.fesod.sheet.annotation.write.style.ContentStyle
 */
@Slf4j
public class StyleWriteExample {

    public static void main(String[] args) {
        styleWrite();
    }

    /**
     * Writes an Excel file with custom header and content styles.
     *
     * <p>Styles are defined entirely through annotations on {@link DemoStyleData}.
     * No programmatic style code is needed — Fesod reads the annotations and applies
     * them automatically during write.</p>
     */
    public static void styleWrite() {
        String fileName = ExampleFileUtil.getTempPath("styleWrite" + System.currentTimeMillis() + ".xlsx");

        FesodSheet.write(fileName, DemoStyleData.class).sheet("Template").doWrite(data());
        log.info("Successfully wrote file: {}", fileName);
    }

    private static List<DemoStyleData> data() {
        List<DemoStyleData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoStyleData data = new DemoStyleData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
