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

package org.apache.fesod.excel.excel.util;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.util.DateUtils;

/**
 * test util
 *
 *
 */
@Slf4j
public class TestUtil {

    public static final Date TEST_DATE;
    public static final LocalDate TEST_LOCAL_DATE = LocalDate.of(2020, 1, 1);
    public static final LocalDateTime TEST_LOCAL_DATE_TIME = LocalDateTime.of(2020, 1, 1, 1, 1, 1);

    static {
        try {
            TEST_DATE = DateUtils.parseDate("2020-01-01 01:01:01");
        } catch (ParseException e) {
            log.error("init TestUtil error.", e);
            throw new RuntimeException(e);
        }
    }
}
