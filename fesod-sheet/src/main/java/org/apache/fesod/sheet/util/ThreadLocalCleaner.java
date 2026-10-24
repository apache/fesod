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

package org.apache.fesod.sheet.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

/**
 * Utility class for managing and executing unified {@link ThreadLocal} cleanups.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalCleaner {

    private static final List<Runnable> cleanTasks = new CopyOnWriteArrayList<>();

    /**
     * Registers a cleanup task.
     * <p>NOTE: for internal use only.</p>
     *
     * @param cleaner the cleanup task to register
     */
    public static void register(Runnable cleaner) {
        Validate.notNull(cleaner, "cleaner cannot be null");
        cleanTasks.add(cleaner);
    }

    public static void clearAllThreadLocals() {
        cleanTasks.forEach(Runnable::run);
    }
}
