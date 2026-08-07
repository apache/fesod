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

package org.apache.fesod.sheet.examples.advanced;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.examples.advanced.converter.CustomStringStringConverter;
import org.apache.fesod.sheet.examples.advanced.data.CustomConverterData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.read.listener.ReadListener;

/**
 * Demonstrates registering a custom converter at the builder level for read and write.
 *
 * <h2>Scenario</h2>
 * <p>You need the same data transformation applied to ALL fields of a matching type,
 * not just a specific annotated field. For example, adding a "Custom:" prefix to every
 * string column, or encrypting/decrypting all string values.</p>
 *
 * <h2>Key Concepts: Per-Field vs. Global Converter Registration</h2>
 * <table>
 *   <tr><th>Approach</th><th>Scope</th><th>How</th></tr>
 *   <tr>
 *     <td>Per-field</td>
 *     <td>Single field only</td>
 *     <td>{@code @ExcelProperty(converter = MyConverter.class)}</td>
 *   </tr>
 *   <tr>
 *     <td>Global (this example)</td>
 *     <td>All fields matching Java type + Excel type</td>
 *     <td>{@code .registerConverter(new MyConverter())} on the builder</td>
 *   </tr>
 * </table>
 *
 * <h2>How It Works</h2>
 * <ol>
 *   <li><b>Write:</b> The {@link CustomStringStringConverter} is registered on the write builder.
 *       During write, every {@code String} field is transformed (prefixed with "Custom:").</li>
 *   <li><b>Read:</b> The same converter is registered on the read builder.
 *       During read, every string cell is transformed back through the converter.</li>
 * </ol>
 *
 * <h2>Converter Resolution Priority</h2>
 * <pre>
 * 1. Field-level converter (@ExcelProperty(converter = ...))  ← highest
 * 2. Builder-level converter (.registerConverter(...))         ← this example
 * 3. Built-in default converter                                ← lowest
 * </pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.read.ConverterReadExample} — Per-field converter via annotation.</li>
 * </ul>
 *
 * @see CustomStringStringConverter
 * @see org.apache.fesod.sheet.converters.Converter
 */
@Slf4j
public class CustomConverterExample {

    public static void main(String[] args) {
        String fileName = ExampleFileUtil.getTempPath("customConverter" + System.currentTimeMillis() + ".xlsx");
        customConverterWrite(fileName);
        customConverterRead(fileName);
    }

    /**
     * Writes data with a globally registered converter that prefixes all string values.
     *
     * <p>The {@link CustomStringStringConverter} transforms "String0" → "Custom:String0"
     * for every string field in the data model.</p>
     */
    public static void customConverterWrite(String fileName) {
        FesodSheet.write(fileName, CustomConverterData.class)
                .registerConverter(new CustomStringStringConverter())
                .sheet("CustomConverter")
                .doWrite(data());
        log.info("Successfully wrote file with custom converter: {}", fileName);
    }

    /**
     * Reads the previously written file with the same converter registered.
     *
     * <p>The converter's {@code convertToJavaData()} method is applied during read,
     * transforming cell values as they are parsed.</p>
     */
    public static void customConverterRead(String fileName) {
        FesodSheet.read(fileName, CustomConverterData.class, new ReadListener<CustomConverterData>() {
                    @Override
                    public void invoke(CustomConverterData data, AnalysisContext context) {
                        log.info("Read data with custom converter: {}", data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        log.info("Custom converter read completed");
                    }
                })
                .registerConverter(new CustomStringStringConverter())
                .sheet()
                .doRead();
    }

    private static List<CustomConverterData> data() {
        List<CustomConverterData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CustomConverterData data = new CustomConverterData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
