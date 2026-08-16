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

package org.apache.fesod.sheet.read.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.apache.fesod.common.util.MapUtils;

/**
 * Strategy interface for column index resolution and filtering during sheet parsing.
 */
@FunctionalInterface
public interface ColumnIndexResolver {

    /**
     * Default pass-through resolver that performs no column filtering.
     */
    ColumnIndexResolver PASS_THROUGH = columnIndex -> columnIndex;

    /**
     * Resolves the raw column index to a target column index.
     *
     * @param columnIndex raw 0-based column index
     * @return mapped target column index, or {@code null} if the column is not included
     */
    Integer resolve(int columnIndex);

    static ColumnIndexResolver fromInclude(List<Integer> columnIndexes) {
        return new DefaultIncludedColumnIndexResolver(columnIndexes);
    }

    class DefaultIncludedColumnIndexResolver implements ColumnIndexResolver {

        private final Map<Integer, Integer> indexMap;

        DefaultIncludedColumnIndexResolver(List<Integer> columnIndexes) {
            Validate.notEmpty(columnIndexes, "The includeColumnIndexes must not be empty");

            List<Integer> tmpColumnIndexes = new ArrayList<>(columnIndexes);
            this.indexMap = MapUtils.newHashMapWithExpectedSize(tmpColumnIndexes.size());

            for (int targetIndex = 0; targetIndex < tmpColumnIndexes.size(); targetIndex++) {
                this.indexMap.put(tmpColumnIndexes.get(targetIndex), targetIndex);
            }
        }

        @Override
        public Integer resolve(int columnIndex) {
            return indexMap.get(columnIndex);
        }
    }
}
