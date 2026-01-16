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

package org.apache.fesod.sheet.testkit.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;

/**
 * A reusable read listener that collects data without performing assertions.
 * Assertions should be done in the test method after reading completes.
 *
 * <p>This follows the Single Responsibility Principle - the listener's job is
 * only to collect data, not to validate it.</p>
 *
 * @param <T> the data type
 */
public class CollectingReadListener<T> extends AnalysisEventListener<T> {

    private final List<T> data = new ArrayList<>();
    private final Consumer<T> onInvoke;
    private Map<Integer, String> headMap;
    private int invokeCount = 0;

    public CollectingReadListener() {
        this(t -> {});
    }

    /**
     * Creates a listener with a callback for each row.
     * Useful for logging or tracking progress without assertions.
     */
    public CollectingReadListener(Consumer<T> onInvoke) {
        this.onInvoke = onInvoke;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
    }

    @Override
    public void invoke(T row, AnalysisContext context) {
        onInvoke.accept(row);
        data.add(row);
        invokeCount++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // Intentionally empty - assertions happen in test methods
    }

    // ==================== Accessors ====================

    public List<T> getData() {
        return Collections.unmodifiableList(data);
    }

    public Map<Integer, String> getHeadMap() {
        return headMap != null ? Collections.unmodifiableMap(headMap) : null;
    }

    public int size() {
        return data.size();
    }

    public int getInvokeCount() {
        return invokeCount;
    }

    public T getFirst() {
        return data.isEmpty() ? null : data.get(0);
    }

    public T getLast() {
        return data.isEmpty() ? null : data.get(data.size() - 1);
    }
}
