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

package org.apache.fesod.sheet.write.view;

import java.lang.reflect.Field;

/**
 * Strategy interface for determining whether Sheet writing should
 * apply view-based matcher and whether a given field belongs to
 * the active view(s).
 */
public interface WriteViewMatcher {

    /**
     * Returns whether any view constraints are active for the current
     * Sheet writing operation.
     */
    boolean hasViews();

    /**
     * Returns whether the given field is included in the active view(s)
     * based on its {@code @ExcelView} annotation.
     */
    boolean matches(Field field);
}
