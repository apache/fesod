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

package org.apache.fesod.excel.demo.read;

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.read.listener.ReadListener;

/**
 * A data listener example that specifies the header type through generics.
 *
 * @param <T>
 */
@Slf4j
public class GenericHeaderTypeDataListener<T> implements ReadListener<T> {

    private final Class<T> headerClass;

    private GenericHeaderTypeDataListener(Class<T> headerClass) {
        this.headerClass = headerClass;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        log.info("data:{}", data);
        // Execute business logic
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Perform cleanup tasks
    }

    public static <T> GenericHeaderTypeDataListener<T> build(Class<T> excelHeaderClass) {
        return new GenericHeaderTypeDataListener<>(excelHeaderClass);
    }

    public Class<T> getHeaderClass() {
        return headerClass;
    }
}
