/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.   You may obtain a copy of the License at
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
package org.apache.fesod.cli.core;

import org.apache.fesod.cli.core.sheet.SheetProcessor;
import org.apache.fesod.cli.exception.CliException;

import java.util.HashMap;
import java.util.Map;

/**
 * Module registry for managing document processors
 */
public class ModuleRegistry {
    
    private static final Map<String, DocumentProcessor> PROCESSORS = new HashMap<String, DocumentProcessor>();
    
    static {
        // Register sheet processor
        registerProcessor(new SheetProcessor());
    }
    
    public static void registerProcessor(DocumentProcessor processor) {
        PROCESSORS.put(processor.getModuleName(), processor);
    }
    
    public static DocumentProcessor getProcessor(String moduleName) {
        DocumentProcessor processor = PROCESSORS.get(moduleName);
        if (processor == null) {
            throw new CliException(
                "Unsupported module: " + moduleName + 
                ". Available modules: " + String.join(", ", PROCESSORS.keySet())
            );
        }
        return processor;
    }
    
    public static String[] getModuleNames() {
        return PROCESSORS.keySet().toArray(new String[0]);
    }
}

