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

package org.apache.fesod.sheet.examples.quickstart;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.apache.fesod.sheet.examples.ExampleTestBase;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link SimpleWriteExample}.
 *
 * <p>Verifies that the quickstart write example can produce an Excel file. The example writes
 * 10 rows of {@code DemoData} to a temp file via {@code ExampleFileUtil.getTempPath()}.
 */
class SimpleWriteExampleITCase extends ExampleTestBase {

    @Test
    void testSimpleWrite() {
        assertDoesNotThrow(SimpleWriteExample::simpleWrite);
    }

    @Test
    void testMain() {
        assertDoesNotThrow(() -> SimpleWriteExample.main(new String[] {}));
    }
}
