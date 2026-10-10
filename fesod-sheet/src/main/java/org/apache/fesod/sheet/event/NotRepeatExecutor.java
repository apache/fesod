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

package org.apache.fesod.sheet.event;

/**
 * Marks an executor (typically a write handler) that must run at most once per event, even when
 * several instances of the same logical handler are registered.
 *
 * <p>Executors implementing this interface are deduplicated by the value returned from
 * {@link #uniqueValue()}: among all registered executors sharing the same unique value, only one
 * will be executed for a given event. Use {@link Order} to control which one wins.</p>
 */
public interface NotRepeatExecutor {

    /**
     * Returns the identifier used to deduplicate executors. Executors that report the same value
     * are considered equivalent and only one of them will be executed.
     *
     * @return a non-{@code null} unique identifier for this executor; two executors returning the
     *         same value will be treated as the same executor
     */
    String uniqueValue();
}
