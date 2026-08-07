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

package org.apache.fesod.sheet.examples.read;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.apache.fesod.sheet.examples.ExampleTestBase;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link MultiSheetReadExample}.
 *
 * <p>Verifies that the multi-sheet read example correctly reads the same file multiple times
 * using different listeners, demonstrating sheet-level repeated read capability.
 */
class MultiSheetReadExampleITCase extends ExampleTestBase {

    @Test
    void testMultiSheetRead() {
        assertDoesNotThrow(MultiSheetReadExample::repeatedRead);
    }
}
