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

package org.apache.fesod.sheet.fill;

import java.time.LocalDate;
import lombok.Data;

@Data
public class LoopRowFillingMergeModel {

    /**
     * Cell case: merge across rows
     */
    private Integer no;

    /**
     * Cell case: normal
     */
    private String string1;

    /**
     * Cell case: normal
     */
    private String string2;

    /**
     * Cell case: merge across rows
     */
    private String string3;

    /**
     * Cell case: merge across columns
     */
    private LocalDate localDate1;

    /**
     * Cell case: merge across columns
     */
    private LocalDate localDate2;

    /**
     * Cell case: merge across rows and columns
     */
    private Long long1;

    /**
     * Cell case: normal
     */
    private Long long2;
}
