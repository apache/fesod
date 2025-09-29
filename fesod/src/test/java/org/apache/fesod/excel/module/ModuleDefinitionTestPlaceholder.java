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

package org.apache.fesod.excel.module;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

/**
 * Placeholder to keep Java 8 test compilation green. The real module descriptor test
 * (requiring Java 9+) lives under src/test/java9 with class name {@code ModuleDefinitionTest}.
 * Disabled for all JRE >= 9 via @DisabledForJreRange to remain future-proof.
 */
@DisabledForJreRange(min = JRE.JAVA_9)
class ModuleDefinitionTestPlaceholder {

    @Test
    void noop() {
        // Intentionally empty.
    }
}
