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

package org.apache.fesod.sheet.enums;

/**
 * Header merge strategy for Excel writing.
 * <p>
 * When {@code headerMergeStrategy} is not set (null), the behavior is determined by
 * {@code automaticMergeHead} for backward compatibility:
 * {@code automaticMergeHead == true} → {@code AUTO}, {@code automaticMergeHead == false} → {@code NONE}.
 * </p>
 *
 * @see org.apache.fesod.excel.write.metadata.WriteBasicParameter#getHeaderMergeStrategy()
 * @see org.apache.fesod.excel.write.builder.AbstractExcelWriterParameterBuilder#headerMergeStrategy(HeaderMergeStrategy)
 */
public enum HeaderMergeStrategy {
    /**
     * No automatic merge
     */
    NONE,

    /**
     * Only horizontal merge (same cells in the same row)
     */
    HORIZONTAL_ONLY,

    /**
     * Only vertical merge (same cells in the same column).
     */
    VERTICAL_ONLY,

    /**
     * Only full rectangle merge (all cells must form a complete rectangle with the same name)
     */
    FULL_RECTANGLE,

    /**
     * Auto merge (default behavior for backward compatibility).
     */
    AUTO
}
