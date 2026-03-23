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

package org.apache.fesod.sheet.examples.write.data;

import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ContentLoopMerge;

/**
 * Data model demonstrating annotation-based cell merging.
 *
 * <p>The {@code @ContentLoopMerge(eachRow = 2)} annotation on the {@code string} field
 * tells Fesod to merge every 2 consecutive rows in that column. This is useful for
 * category grouping in reports.</p>
 *
 * <h2>Merge Effect</h2>
 * <pre>
 * Row 1: | String0 | 2025-01-01 | 0.56 |  ← merged with row 2
 * Row 2: | (merged)| 2025-01-01 | 0.56 |
 * Row 3: | String1 | 2025-01-01 | 0.56 |  ← merged with row 4
 * Row 4: | (merged)| 2025-01-01 | 0.56 |
 * </pre>
 *
 * <p>For runtime-configurable merging, use {@link org.apache.fesod.sheet.write.merge.LoopMergeStrategy}
 * instead (see {@link org.apache.fesod.sheet.examples.write.MergeWriteExample}).</p>
 *
 * @see ContentLoopMerge
 * @see org.apache.fesod.sheet.write.merge.LoopMergeStrategy
 */
@Getter
@Setter
@EqualsAndHashCode
public class DemoMergeData {
    /**
     * Merge cells every 2 rows in this column.
     */
    @ContentLoopMerge(eachRow = 2)
    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ExcelProperty("Number Title")
    private Double doubleData;
}
