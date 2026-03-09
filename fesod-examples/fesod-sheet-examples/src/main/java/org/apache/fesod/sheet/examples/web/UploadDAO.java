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

package org.apache.fesod.sheet.examples.web;

import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Mock DAO (Data Access Object) for web upload persistence.
 *
 * <p>In production, this would be a real Spring {@code @Repository} with database
 * operations (e.g., JDBC batch insert, MyBatis mapper, or JPA repository).
 * The {@link UploadDataListener} calls {@code save()} every 100 rows.
 *
 * <p><b>Spring lifecycle:</b> This is a Spring-managed singleton bean ({@code @Repository}).
 * It's safe as a singleton because it holds no mutable state — unlike the listener,
 * which must be created new for each upload.</p>
 *
 * @see UploadDataListener
 * @see org.apache.fesod.sheet.examples.web.WebExampleController
 */
@Repository
public class UploadDAO {

    public void save(List<UploadData> list) {
        // In actual use, you can use batch insert here.
    }
}
