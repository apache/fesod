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

package org.apache.fesod.sheet.converters;

import java.util.Collections;
import java.util.Set;

/**
 * Column binding contract for converters that apply only to specific spreadsheet column(s).
 * <p>
 * A converter that does not implement this interface is considered a global converter.
 * Implementations typically need to override either {@link #columnIndex()} for a single column
 * or {@link #columnIndexes()} for multiple columns.
 * </p>
 * <b>WARNING:</b> Providing no valid column index ({@code null} or an empty set) renders this converter
 * completely ineffective, it will match no column and will <b>not</b> act as a global fallback.
 * Do not implement this interface if a global converter is intended.
 */
public interface ColumnBinding {

    /**
     * The column index (0-based) to which this converter is bound.
     */
    default Integer columnIndex() {
        return null;
    }

    /**
     * The column indexes (0-based) to which this converter is bound.
     */
    default Set<Integer> columnIndexes() {
        Integer result = columnIndex();
        return result == null ? Collections.emptySet() : Collections.singleton(result);
    }
}
