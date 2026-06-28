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
import java.util.NoSuchElementException;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;

/**
 * A generic, reusable {@link AnalysisEventListener} that simply collects every row into a list.
 *
 * <p>Unlike the legacy per-test listeners, this class contains <b>no assertions</b> and
 * <b>no logging</b>. Assertions belong in the test method; data collection belongs here.
 *
 * @param <T> the row model type
 */
public class CollectingReadListener<T> extends AnalysisEventListener<T> {

    private final List<T> rows = new ArrayList<T>();

    @Override
    public void invoke(T data, AnalysisContext context) {
        rows.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // intentionally empty — no assertions, no logging
    }

    /**
     * Returns an unmodifiable view of all collected rows.
     */
    public List<T> getRows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * Returns the number of collected rows.
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Returns the first collected row.
     *
     * @throws NoSuchElementException if no rows have been collected
     */
    public T getFirstRow() {
        if (rows.isEmpty()) {
            throw new NoSuchElementException("Expected at least one row, but CollectingReadListener collected none");
        }
        return rows.get(0);
    }

    /**
     * Clears all collected rows, allowing reuse of the same listener instance.
     */
    public void clear() {
        rows.clear();
    }
}
