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

package org.apache.fesod.excel.write.metadata.holder;

import java.util.Collection;
import org.apache.fesod.excel.metadata.ConfigurationHolder;
import org.apache.fesod.excel.write.property.ExcelWriteHeadProperty;

/**
 * Get the corresponding Holder
 *
 *
 **/
public interface WriteHolder extends ConfigurationHolder {
    /**
     * What 'ExcelWriteHeadProperty' does the currently operated cell need to execute
     *
     * @return
     */
    ExcelWriteHeadProperty excelWriteHeadProperty();

    /**
     * Is to determine if a field needs to be ignored
     *
     * @param fieldName
     * @param columnIndex
     * @return
     */
    boolean ignore(String fieldName, Integer columnIndex);

    /**
     * Whether a header is required for the currently operated cell
     *
     * @return
     */
    boolean needHead();

    /**
     * Whether need automatic merge headers.
     *
     * @return
     */
    boolean automaticMergeHead();

    /**
     * Writes the head relative to the existing contents of the sheet. Indexes are zero-based.
     *
     * @return
     */
    int relativeHeadRowIndex();

    /**
     * Data will be order by  {@link #includeColumnFieldNames} or  {@link #includeColumnIndexes}.
     *
     * default is false.
     *
     * @return
     */
    boolean orderByIncludeColumn();

    /**
     * Only output the custom columns.
     *
     * @return
     */
    Collection<Integer> includeColumnIndexes();

    /**
     * Only output the custom columns.
     *
     * @return
     */
    Collection<String> includeColumnFieldNames();

    /**
     * Ignore the custom columns.
     *
     * @return
     */
    Collection<Integer> excludeColumnIndexes();

    /**
     * Ignore the custom columns.
     *
     * @return
     */
    Collection<String> excludeColumnFieldNames();
}
