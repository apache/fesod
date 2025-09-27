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

package org.apache.fesod.excel.write.metadata;

/**
 * A row of data.
 *
 *
 */
public interface RowData {

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @param index
     * @return data
     */
    Object get(int index);

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <code>Integer.MAX_VALUE</code> elements, returns
     * <code>Integer.MAX_VALUE</code>.
     *
     * @return the number of elements in this collection
     */
    int size();

    /**
     * Returns <code>true</code> if this collection contains no elements.
     *
     * @return <code>true</code> if this collection contains no elements
     */
    boolean isEmpty();
}
