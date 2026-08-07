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

package org.apache.fesod.sheet.examples.advanced;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.apache.fesod.sheet.examples.ExampleTestBase;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LargeFileWriteExample}.
 *
 * <p>Verifies the large-file write example which writes 100,000 rows in batches using
 * {@code SXSSFWorkbook} with compressed temporary files to reduce disk usage.
 *
 * <p><strong>Note:</strong> This test may take several seconds due to the volume of data.
 */
class LargeFileWriteExampleITCase extends ExampleTestBase {

    @Test
    void testCompressedTemporaryFile() {
        assertDoesNotThrow(LargeFileWriteExample::compressedTemporaryFile);
    }
}
