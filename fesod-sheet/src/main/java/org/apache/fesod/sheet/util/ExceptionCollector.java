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

import java.util.function.Function;

/**
 * Collects teardown failures while preserving the first error and attaching later failures as suppressed exceptions.
 */
public final class ExceptionCollector {

    private Throwable failure;

    /**
     * Runs a step and records any failure without interrupting the remaining teardown sequence.
     */
    public void run(ThrowableRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            failure = recordFailure(failure, t);
        }
    }

    /**
     * Returns true if at least one recorded failure exists.
     */
    public boolean hasFailure() {
        return failure != null;
    }

    /**
     * Throws the collected failure transformed into the target exception type.
     */
    public <E extends Throwable> void throwIfPresent(Function<Throwable, E> transformer) throws E {
        if (failure == null) {
            return;
        }
        E transformed = transformer.apply(failure);
        if (transformed != null) {
            throw transformed;
        }
        throw new IllegalStateException("transformer must not return null", failure);
    }

    /**
     * Returns the first recorded failure while attaching any later errors as suppressed exceptions.
     */
    private static Throwable recordFailure(Throwable recorded, Throwable t) {
        if (recorded == null) {
            return t;
        }
        if (t != null && recorded != t) {
            recorded.addSuppressed(t);
        }
        return recorded;
    }

    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Throwable;
    }
}
