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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.module.ModuleDescriptor;
import java.lang.reflect.Field;
import org.apache.fesod.excel.ExcelReader;
import org.junit.jupiter.api.Test;

class ModuleDefinitionTest {

    @Test
    void moduleDescriptorIsNamedAndOpen() {
        Module module = ExcelReader.class.getModule();
        assertNotNull(module, "module should be resolved");
        assertEquals("org.apache.fesod", module.getName(), "module name should match descriptor");
        ModuleDescriptor descriptor = module.getDescriptor();
        assertNotNull(descriptor, "module should have a descriptor");
        assertTrue(descriptor.isOpen(), "module must stay open to support reflection");
        assertTrue(module.isOpen("org.apache.fesod.excel"), "root package should be open for reflection");
    }

    @Test
    void reflectiveAccessToInternalFieldSucceeds() throws Exception {
        Field field = ExcelReader.class.getDeclaredField("excelAnalyser");
        assertDoesNotThrow(() -> field.setAccessible(true), "reflection should not require add-opens");
    }
}
