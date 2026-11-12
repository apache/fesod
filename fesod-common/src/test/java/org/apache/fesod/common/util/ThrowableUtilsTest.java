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

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ThrowableUtils}
 */
class ThrowableUtilsTest {

    @Test
    void recordFailure_nothingRecordedYet_returnsTheFailure() {
        IOException failure = new IOException("first");

        Throwable recorded = ThrowableUtils.recordFailure(null, failure);

        Assertions.assertSame(failure, recorded);
        Assertions.assertEquals(0, recorded.getSuppressed().length);
    }

    @Test
    void recordFailure_keepsTheFirstFailureAndSuppressesTheLaterOnes() {
        IOException first = new IOException("first");
        IOException second = new IOException("second");
        OutOfMemoryError third = new OutOfMemoryError("third");

        Throwable recorded = ThrowableUtils.recordFailure(null, first);
        recorded = ThrowableUtils.recordFailure(recorded, second);
        recorded = ThrowableUtils.recordFailure(recorded, third);

        Assertions.assertSame(first, recorded);
        Assertions.assertArrayEquals(new Throwable[] {second, third}, recorded.getSuppressed());
    }

    @Test
    void recordFailure_sameInstanceTwice_doesNotSuppressItself() {
        IOException failure = new IOException("same");

        Throwable recorded = ThrowableUtils.recordFailure(failure, failure);

        Assertions.assertSame(failure, recorded);
        Assertions.assertEquals(0, recorded.getSuppressed().length);
    }

    @Test
    void recordFailure_nullFailure_leavesTheRecordedOneUntouched() {
        IOException recorded = new IOException("recorded");

        Assertions.assertSame(recorded, ThrowableUtils.recordFailure(recorded, null));
        Assertions.assertEquals(0, recorded.getSuppressed().length);
    }

    @Test
    void addSuppressedQuietly_nullArguments_doNothing() {
        IOException primary = new IOException("primary");

        Assertions.assertDoesNotThrow(() -> ThrowableUtils.addSuppressedQuietly(null, primary));
        Assertions.assertDoesNotThrow(() -> ThrowableUtils.addSuppressedQuietly(primary, null));
        Assertions.assertEquals(0, primary.getSuppressed().length);
    }

    @Test
    void addSuppressedQuietly_suppressionDisabledOnPrimary_doesNotThrow() {
        Throwable primary = new Throwable("no suppression", null, false, false) {};

        Assertions.assertDoesNotThrow(() -> ThrowableUtils.addSuppressedQuietly(primary, new IOException("later")));
        Assertions.assertEquals(0, primary.getSuppressed().length);
    }
}
