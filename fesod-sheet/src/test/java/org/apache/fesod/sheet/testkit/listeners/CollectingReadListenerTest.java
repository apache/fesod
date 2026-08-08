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

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(Tags.UNIT)
class CollectingReadListenerTest {

    @Test
    void collectionInvariantWithMultipleInvocations() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();

        listener.invoke("row0", null);
        listener.invoke("row1", null);
        listener.invoke("row2", null);

        Assertions.assertEquals(3, listener.getRowCount());
        List<String> rows = listener.getRows();
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("row0", rows.get(0));
        Assertions.assertEquals("row1", rows.get(1));
        Assertions.assertEquals("row2", rows.get(2));
    }

    @Test
    void getRowsReturnsUnmodifiableView() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();
        listener.invoke("data", null);

        List<String> rows = listener.getRows();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> rows.add("illegal"));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> rows.remove(0));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> rows.clear());
    }

    @Test
    void clearResetsState() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();
        listener.invoke("row0", null);
        listener.invoke("row1", null);
        Assertions.assertEquals(2, listener.getRowCount());

        listener.clear();

        Assertions.assertEquals(0, listener.getRowCount());
        Assertions.assertTrue(listener.getRows().isEmpty());
    }

    @Test
    void getFirstRowReturnsFirstElement() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();
        listener.invoke("first", null);
        listener.invoke("second", null);

        Assertions.assertEquals("first", listener.getFirstRow());
    }

    @Test
    void getFirstRowOnEmptyThrowsNoSuchElement() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();

        NoSuchElementException error =
                Assertions.assertThrows(NoSuchElementException.class, () -> listener.getFirstRow());
        Assertions.assertTrue(error.getMessage().contains("at least one row"));
    }

    @Test
    void doAfterAllAnalysedDoesNothing() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();
        listener.invoke("data", null);

        // should not throw or modify state
        listener.doAfterAllAnalysed(null);

        Assertions.assertEquals(1, listener.getRowCount());
        Assertions.assertEquals("data", listener.getFirstRow());
    }

    @Test
    void emptyListenerHasZeroRows() {
        CollectingReadListener<String> listener = new CollectingReadListener<String>();

        Assertions.assertEquals(0, listener.getRowCount());
        Assertions.assertTrue(listener.getRows().isEmpty());
    }
}
