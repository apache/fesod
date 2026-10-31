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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ThreadLocalCleaner}.
 */
class ThreadLocalCleanerTest {

    @Test
    void testRegisterNullThrowsException() {
        Assertions.assertThrows(NullPointerException.class, () -> ThreadLocalCleaner.register(null));
    }

    @Test
    void testConcurrentModificationExceptionRegression() throws Exception {
        CountDownLatch iterationStartedLatch = new CountDownLatch(1);
        CountDownLatch registrationDoneLatch = new CountDownLatch(1);

        AtomicBoolean task1Executed = new AtomicBoolean(false);
        AtomicBoolean task2Executed = new AtomicBoolean(false);

        ThreadLocalCleaner.register(() -> {
            task1Executed.set(true);
            iterationStartedLatch.countDown();
            try {
                if (!registrationDoneLatch.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timeout waiting for concurrent registration");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        CompletableFuture<Void> clearFuture = CompletableFuture.runAsync(ThreadLocalCleaner::clearAllThreadLocals);

        Assertions.assertTrue(iterationStartedLatch.await(5, TimeUnit.SECONDS), "Iteration did not start in time");

        ThreadLocalCleaner.register(() -> task2Executed.set(true));
        registrationDoneLatch.countDown();

        Assertions.assertDoesNotThrow(() -> clearFuture.get(5, TimeUnit.SECONDS));
        Assertions.assertTrue(task1Executed.get(), "Task 1 should have executed");

        ThreadLocalCleaner.clearAllThreadLocals();
        Assertions.assertTrue(task2Executed.get(), "Task 2 should execute in subsequent clear");
    }
}
