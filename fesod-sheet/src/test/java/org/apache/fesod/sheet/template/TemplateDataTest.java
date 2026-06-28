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

package org.apache.fesod.sheet.template;

import static org.apache.fesod.sheet.testkit.params.FormatCapability.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test template write/read for binary Excel formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class TemplateDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY, requires = TEMPLATES)
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("template", format);
        String templateName = "template" + File.separator + "template" + (format == ExcelFormat.XLSX ? "07" : "03")
                + format.getExtension();
        File template = readFile(templateName);
        FesodSheet.write(file, TemplateData.class)
                .withTemplate(template)
                .sheet()
                .doWrite(TestDataBuilder.templateData(2));
        List<TemplateData> result = FesodSheet.read(file)
                .head(TemplateData.class)
                .headRowNumber(3)
                .sheet()
                .doReadSync();
        assertEquals(2, result.size());
        assertEquals("String0", result.get(0).getString0());
        assertEquals("String1", result.get(1).getString0());
    }
}
