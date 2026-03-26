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

package org.apache.fesod.sheet.examples.fill;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.fill.data.FillData;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;

/**
 * Demonstrates filling data into a pre-designed Excel template with placeholders.
 *
 * <h2>Scenario</h2>
 * <p>You have a pre-formatted Excel template (designed by a non-developer) with
 * placeholder variables like {@code {name}} and {@code {number}}. You want to fill in
 * actual data at runtime without rebuilding the layout programmatically.</p>
 *
 * <h2>Template Format</h2>
 * <p>The template file ({@code templates/simple.xlsx}) contains cells with placeholders:</p>
 * <pre>
 * Template:        | Name: {name}  | Score: {number} |
 *                       ↓                  ↓
 * Filled result:   | Name: Zhang San | Score: 5.2 |
 * </pre>
 *
 * <h2>Two Data Sources</h2>
 * <ul>
 *   <li><b>Object-based</b> — Create a {@link FillData} object. Fesod maps field names
 *       to template placeholders automatically.</li>
 *   <li><b>Map-based</b> — Use a {@code Map<String, Object>} where keys match placeholder names.
 *       Useful when the structure is dynamic or you don't want to create a POJO.</li>
 * </ul>
 *
 * <h2>Placeholder Syntax</h2>
 * <ul>
 *   <li>{@code {fieldName}} — Replaced by a single value (for simple fills).</li>
 *   <li>{@code {.fieldName}} — Replaced by a list of values (for list fills,
 *       see {@link FillComplexExample}).</li>
 * </ul>
 *
 * <h2>When to Use Fill vs. Write</h2>
 * <ul>
 *   <li><b>Fill</b> — When you have a pre-designed template with specific formatting,
 *       merged cells, formulas, charts, or complex layouts.</li>
 *   <li><b>Write</b> — When generating a simple tabular report from scratch.</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link FillComplexExample} — Fill templates with list data (multiple rows).</li>
 *   <li>{@link org.apache.fesod.sheet.examples.write.BasicWriteExample} — Write from scratch.</li>
 * </ul>
 *
 * @see FesodSheet#write(String)
 * @see FillData
 */
@Slf4j
public class FillBasicExample {

    public static void main(String[] args) {
        simpleFill();
    }

    /**
     * Fills a simple template using both object-based and map-based data sources.
     *
     * <p>Demonstrates two equivalent approaches:
     * <ol>
     *   <li><b>Object fill</b> — Uses a {@link FillData} POJO. The field names must match
     *       the placeholder names in the template.</li>
     *   <li><b>Map fill</b> — Uses a {@code Map<String, Object>}. The map keys must match
     *       the placeholder names. More flexible for dynamic structures.</li>
     * </ol>
     * Both produce identical output files.</p>
     */
    public static void simpleFill() {
        String templateFileName = ExampleFileUtil.getExamplePath("templates" + File.separator + "simple.xlsx");

        // Option 1: Fill based on an object
        String fileName = ExampleFileUtil.getTempPath("simpleFill" + System.currentTimeMillis() + ".xlsx");
        FillData fillData = new FillData();
        fillData.setName("Zhang San");
        fillData.setNumber(5.2);
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(fillData);
        log.info("Successfully wrote file: {}", fileName);

        // Option 2: Fill based on a Map
        fileName = ExampleFileUtil.getTempPath("simpleFillMap" + System.currentTimeMillis() + ".xlsx");
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Zhang San");
        map.put("number", 5.2);
        FesodSheet.write(fileName).withTemplate(templateFileName).sheet().doFill(map);
        log.info("Successfully wrote file: {}", fileName);
    }
}
