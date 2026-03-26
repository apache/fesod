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

package org.apache.fesod.sheet.examples.read.data;

import java.util.List;

/**
 * Mock Data Access Object (DAO) simulating database persistence for examples.
 *
 * <p>In production, replace the {@code save()} method body with actual database operations
 * (e.g., JDBC batch insert, MyBatis, or JPA). The batch pattern used in
 * {@link org.apache.fesod.sheet.examples.read.listeners.DemoDataListener} calls this
 * DAO every 100 rows to balance memory usage and database round-trips.</p>
 *
 * <p><b>Example production implementation:</b></p>
 * <pre>{@code
 * public void save(List<DemoData> list) {
 *     // Using Spring JdbcTemplate batch insert
 *     jdbcTemplate.batchUpdate(
 *         "INSERT INTO demo (string, date, double_data) VALUES (?, ?, ?)",
 *         list, list.size(),
 *         (ps, data) -> {
 *             ps.setString(1, data.getString());
 *             ps.setDate(2, new java.sql.Date(data.getDate().getTime()));
 *             ps.setDouble(3, data.getDoubleData());
 *         });
 * }
 * }</pre>
 *
 * @see org.apache.fesod.sheet.examples.read.listeners.DemoDataListener
 */
public class DemoDAO {

    public void save(List<DemoData> list) {
        // In actual use, you can use batch insert here.
    }
}
