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

package org.apache.fesod.excel.constant;

import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Used to store constant
 *
 *
 */
public class FastExcelConstants {

    /**
     * Excel by default with 15 to store Numbers, and the double in Java can use to store number 17, led to the accuracy
     * will be a problem. So you need to set up 15 to deal with precision
     */
    public static final MathContext EXCEL_MATH_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);
}
