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

import java.io.File;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.testkit.params.ExcelFormatSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Annotation index and name data test using parameterized tests.
 */
@Tag(Tags.ROUND_TRIP)
public class AnnotationIndexAndNameDataTest extends AbstractExcelTest {

    @ParameterizedTest
    @ExcelFormatSource
    void readAndWrite(ExcelFormat format) throws Exception {
        File file = createTempFile("annotationIndexAndName", format);
        FesodSheet.write(file, AnnotationIndexAndNameData.class)
                .sheet()
                .doWrite(TestDataBuilder.annotationIndexAndNameData(1));
        List<AnnotationIndexAndNameData> result = FesodSheet.read(file)
                .head(AnnotationIndexAndNameData.class)
                .sheet()
                .doReadSync();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Item0", result.get(0).getIndex0());
        Assertions.assertEquals("Item1", result.get(0).getIndex1());
        Assertions.assertEquals("Item2", result.get(0).getIndex2());
        Assertions.assertEquals("Item4", result.get(0).getIndex4());
    }
}
