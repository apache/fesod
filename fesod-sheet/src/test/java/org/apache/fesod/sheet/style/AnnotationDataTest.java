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

package org.apache.fesod.sheet.style;

import static org.apache.fesod.sheet.testkit.params.FormatCapability.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.assertions.ExcelAssertions;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.apache.fesod.sheet.testkit.params.FormatScope;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Test annotation write/read for all formats using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class AnnotationDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("annotation", format);
        FesodSheet.write().file(file).head(AnnotationData.class).sheet().doWrite(dataStyle());

        if (format != ExcelFormat.CSV) {
            try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
                ea.sheet(0)
                        .hasColumnWidth(0, 50 * 256)
                        .row(0)
                        .hasHeight((short) 1000)
                        .and()
                        .row(1)
                        .hasHeight((short) 2000);
            }
        }
    }

    @ParameterizedTest
    @ExcelFormatSource(value = FormatScope.BINARY, requires = STYLES)
    void writeStyle(ExcelFormat format) throws Exception {
        File file = createTempFile("annotationStyle", format);
        FesodSheet.write().file(file).head(AnnotationStyleData.class).sheet().doWrite(dataStyle());

        try (ExcelAssertions ea = ExcelAssertions.assertThat(file)) {
            ea.sheet(0)
                    .row(0)
                    .cell(0)
                    .hasFillColor(new byte[] {-1, 0, -1})
                    .hasFontColor(new byte[] {-1, -52, 0})
                    .hasFontSize((short) 40)
                    .and()
                    .cell(1)
                    .hasFillColor(new byte[] {-1, 0, 0})
                    .hasFontColor(new byte[] {0, -1, -1})
                    .hasFontSize((short) 20)
                    .and()
                    .and()
                    .row(1)
                    .cell(0)
                    .hasFillColor(new byte[] {0, -52, -1})
                    .hasFontColor(new byte[] {0, 0, -1})
                    .hasFontSize((short) 50)
                    .and()
                    .cell(1)
                    .hasFillColor(new byte[] {0, -128, 0})
                    .hasFontColor(new byte[] {-64, -64, -64})
                    .hasFontSize((short) 30);
        }
    }

    private List<AnnotationStyleData> dataStyle() {
        List<AnnotationStyleData> list = new ArrayList<>();
        AnnotationStyleData data = new AnnotationStyleData();
        data.setString("string");
        data.setString1("string1");
        list.add(data);
        return list;
    }
}
