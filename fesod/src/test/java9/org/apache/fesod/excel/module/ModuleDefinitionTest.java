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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.module.ModuleDescriptor;
import org.apache.fesod.excel.ExcelReader; // updated to existing class
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Java 9+ only test verifying the module descriptor remains open (or at least present) and
 * has the expected name. Located under src/test/java9 and compiled only when JDK >= 9.
 */
class ModuleDefinitionTest {

    @Test
    @DisplayName("Module descriptor is named and currently open (or skip if running on classpath)")
    void moduleDescriptorIsNamed() {
        Module module = ExcelReader.class.getModule();
        assertNotNull(module, "module should be resolved for main classes");
        String name = module.getName();
        // If name is null, we are on the classpath (unnamed module) – likely an IDE run without module path.
        assumeTrue(name != null, () -> "Running on classpath (unnamed module) – skipping strict module assertions.  " +
                "Configure your IDE test runtime to use the module path to enable this assertion.");
        assertEquals("org.apache.fesod", name, "module name should match descriptor");
        ModuleDescriptor descriptor = module.getDescriptor();
        assertNotNull(descriptor, "descriptor must exist");
        assertTrue(descriptor.isOpen(), "module expected to remain open while reflective access is required");
    }
}
