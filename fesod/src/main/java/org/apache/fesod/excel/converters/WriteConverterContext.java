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

package org.apache.fesod.excel.converters;

import lombok.*;
import org.apache.fesod.excel.context.WriteContext;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;

/**
 * write converter context
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WriteConverterContext<T> {

    /**
     * Java Data.NotNull.
     */
    private T value;

    /**
     * Java row level record
     */
    private Object record;

    /**
     * Content property.Nullable.
     */
    private ExcelContentProperty contentProperty;

    /**
     * write context
     */
    private WriteContext writeContext;

    @SuppressWarnings("unchecked")
    public <E extends Object> E getRecord() {
        if (record == null) {
            return null;
        }
        return (E) record;
    }
}
