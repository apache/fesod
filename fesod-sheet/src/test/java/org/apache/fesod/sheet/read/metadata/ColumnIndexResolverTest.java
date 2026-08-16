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

package org.apache.fesod.sheet.read.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ColumnIndexResolver}.
 */
@Tag(Tags.UNIT)
class ColumnIndexResolverTest {

    @Test
    void shouldThrowWhenListIsEmptyOrNull() {
        Assertions.assertThrows(NullPointerException.class, () -> ColumnIndexResolver.fromInclude(null));
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> ColumnIndexResolver.fromInclude(Collections.emptyList()));
    }

    @Test
    void passThroughShouldReturnOriginalIndex() {
        ColumnIndexResolver resolver = ColumnIndexResolver.PASS_THROUGH;

        Assertions.assertEquals(0, resolver.resolve(0));
        Assertions.assertEquals(5, resolver.resolve(5));
        Assertions.assertEquals(100, resolver.resolve(100));
    }

    @Test
    void defaultResolverShouldMapAndFilterColumns() {
        List<Integer> includeColumns = Arrays.asList(0, 2, 5);
        ColumnIndexResolver resolver = ColumnIndexResolver.fromInclude(includeColumns);

        Assertions.assertNotSame(ColumnIndexResolver.PASS_THROUGH, resolver);

        Assertions.assertEquals(0, resolver.resolve(0));
        Assertions.assertEquals(1, resolver.resolve(2));
        Assertions.assertEquals(2, resolver.resolve(5));

        Assertions.assertNull(resolver.resolve(1));
        Assertions.assertNull(resolver.resolve(3));
        Assertions.assertNull(resolver.resolve(4));
        Assertions.assertNull(resolver.resolve(6));
        Assertions.assertNull(resolver.resolve(99));
    }

    @Test
    void defaultResolverShouldPreserveCustomColumnOrder() {
        List<Integer> customOrderColumns = Arrays.asList(5, 2, 0);
        ColumnIndexResolver resolver = ColumnIndexResolver.fromInclude(customOrderColumns);

        Assertions.assertEquals(0, resolver.resolve(5));
        Assertions.assertEquals(1, resolver.resolve(2));
        Assertions.assertEquals(2, resolver.resolve(0));

        Assertions.assertNull(resolver.resolve(1));
        Assertions.assertNull(resolver.resolve(3));
    }

    @Test
    void shouldSupportCustomLambdaResolver() {
        ColumnIndexResolver evenColumnResolver = rawIndex -> (rawIndex % 2 == 0) ? rawIndex / 2 : null;

        Assertions.assertEquals(0, evenColumnResolver.resolve(0));
        Assertions.assertEquals(1, evenColumnResolver.resolve(2));
        Assertions.assertEquals(2, evenColumnResolver.resolve(4));

        Assertions.assertNull(evenColumnResolver.resolve(1));
        Assertions.assertNull(evenColumnResolver.resolve(3));
    }
}
