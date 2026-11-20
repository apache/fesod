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

package org.apache.fesod.common.util;

/**
 * Helpers for carrying failures through a best-effort cleanup sequence without losing the one that
 * matters: the first failure is kept as the primary and every later one is attached to it as a
 * suppressed exception, the same way try-with-resources reports a failing {@code close()}.
 */
public final class ThrowableUtils {

    private ThrowableUtils() {}

    /**
     * Records {@code failure} against what a cleanup sequence has collected so far.
     *
     * @param recorded the failure recorded so far, or {@code null} if nothing has failed yet
     * @param failure  the failure that just happened
     * @return the failure to keep carrying: {@code recorded} with {@code failure} attached as suppressed,
     *         or {@code failure} itself if nothing was recorded before
     */
    public static Throwable recordFailure(Throwable recorded, Throwable failure) {
        if (recorded == null) {
            return failure;
        }
        addSuppressedQuietly(recorded, failure);
        return recorded;
    }

    /**
     * Attaches {@code suppressed} to {@code primary} as a suppressed exception, ignoring nulls, self
     * references and any failure of the bookkeeping itself. This runs on error paths where the JVM may
     * already be short of memory, and bookkeeping must never displace the exception being propagated.
     *
     * @param primary    the exception being propagated
     * @param suppressed the failure to attach to it
     */
    public static void addSuppressedQuietly(Throwable primary, Throwable suppressed) {
        if (primary == null || suppressed == null || primary == suppressed) {
            return;
        }
        try {
            primary.addSuppressed(suppressed);
        } catch (Throwable ignore) {
            // nothing sensible can be done here without risking the primary exception
        }
    }
}
