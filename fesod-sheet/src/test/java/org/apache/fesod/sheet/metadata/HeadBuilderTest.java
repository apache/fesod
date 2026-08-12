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

package org.apache.fesod.sheet.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DefaultHeadBuilder} and {@link HeadBuilder}.
 */
@Tag(Tags.UNIT)
class HeadBuilderTest {

    static Consumer<HeadBuilder> withEmpty() {
        return b -> {};
    }

    @Test
    void forSimple_singleName() {
        List<List<String>> head = HeadBuilder.forSimple("ID");

        Assertions.assertEquals(1, head.size());
        Assertions.assertEquals(Arrays.asList("ID"), head.get(0));
    }

    @Test
    void forSimple_multipleNames() {
        List<List<String>> head = HeadBuilder.forSimple("ID", "Name", "Age");

        Assertions.assertEquals(3, head.size());
        Assertions.assertEquals(Arrays.asList("ID"), head.get(0));
        Assertions.assertEquals(Arrays.asList("Name"), head.get(1));
        Assertions.assertEquals(Arrays.asList("Age"), head.get(2));
    }

    @Test
    void forSimple_nullFirstNameThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> HeadBuilder.forSimple(null, "Name"));
    }

    @Test
    void forSimple_nullOtherNameThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> HeadBuilder.forSimple("ID", "Name", null));
    }

    @Test
    void define_nullConsumerThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> DefaultHeadBuilder.define(null));
    }

    @Test
    void define_emptyConsumerReturnsEmptyHead() {
        List<List<String>> head = DefaultHeadBuilder.define(withEmpty());

        Assertions.assertNotNull(head);
        Assertions.assertTrue(head.isEmpty());
    }

    @Test
    void column_singleName() {
        List<List<String>> head = DefaultHeadBuilder.define(b -> b.column("ID"));

        Assertions.assertEquals(1, head.size());
        Assertions.assertEquals(Arrays.asList("ID"), head.get(0));
    }

    @Test
    void column_withSubHeadNames() {
        List<List<String>> head = DefaultHeadBuilder.define(b -> b.column("A", "B", "C"));

        Assertions.assertEquals(1, head.size());
        Assertions.assertEquals(Arrays.asList("A", "B", "C"), head.get(0));
    }

    @Test
    void column_nullHeadNameThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> DefaultHeadBuilder.define(b -> b.column(null, "B")));
    }

    @Test
    void column_nullSubHeadNameThrows() {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> DefaultHeadBuilder.define(b -> b.column("A", "B", null)));
    }

    @Test
    void column_repeatOnce() {
        List<List<String>> head = DefaultHeadBuilder.define(b -> b.column("X", 1));

        Assertions.assertEquals(Arrays.asList("X"), head.get(0));
    }

    @Test
    void column_repeatMultiple() {
        List<List<String>> head = DefaultHeadBuilder.define(b -> b.column("X", 3));

        Assertions.assertEquals(1, head.size());
        Assertions.assertEquals(Arrays.asList("X", "X", "X"), head.get(0));
    }

    @Test
    void column_repeatZeroThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DefaultHeadBuilder.define(b -> b.column("X", 0)));
    }

    @Test
    void column_repeatNegativeThrows() {
        Assertions.assertThrows(
                IllegalArgumentException.class, () -> DefaultHeadBuilder.define(b -> b.column("X", -2)));
    }

    @Test
    void column_repeat_nullHeadNameThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> DefaultHeadBuilder.define(b -> b.column(null, 2)));
    }

    @Test
    void columns_singleParentAppliesPrefixToSubColumns() {
        List<List<String>> head = DefaultHeadBuilder.define(
                b -> b.columns("User Info", sub -> sub.column("Name").column("Age")));

        Assertions.assertEquals(2, head.size());
        Assertions.assertEquals(Arrays.asList("User Info", "Name"), head.get(0));
        Assertions.assertEquals(Arrays.asList("User Info", "Age"), head.get(1));
    }

    @Test
    void columns_multipleParentNamesStackAsPrefix() {
        List<List<String>> head = DefaultHeadBuilder.define(
                b -> b.columns(Arrays.asList("P", "Q"), sub -> sub.column("A").column("B")));

        Assertions.assertEquals(2, head.size());
        Assertions.assertEquals(Arrays.asList("P", "Q", "A"), head.get(0));
        Assertions.assertEquals(Arrays.asList("P", "Q", "B"), head.get(1));
    }

    @Test
    void columns_prefixesAreRestoredAfterBlock() {
        List<List<String>> head = DefaultHeadBuilder.define(
                b -> b.columns("P", sub -> sub.column("A")).column("B"));

        Assertions.assertEquals(2, head.size());
        Assertions.assertEquals(Arrays.asList("P", "A"), head.get(0));
        Assertions.assertEquals(Arrays.asList("B"), head.get(1));
    }

    @Test
    void columns_nestedParentsConcatenate() {
        List<List<String>> head = DefaultHeadBuilder.define(
                b -> b.columns("Outer", outer -> outer.columns("Inner", inner -> inner.column("Leaf"))));

        Assertions.assertEquals(1, head.size());
        Assertions.assertEquals(Arrays.asList("Outer", "Inner", "Leaf"), head.get(0));
    }

    @Test
    void columns_emptyParentListIsNotAllowed() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DefaultHeadBuilder.define(b -> b.columns(Collections.emptyList(), sub -> sub.column("A"))));
    }

    @Test
    void columns_nullParentNamesThrows() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> DefaultHeadBuilder.define(b -> b.columns((List<String>) null, withEmpty())));
    }

    @Test
    void columns_nullElementInParentNamesThrows() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DefaultHeadBuilder.define(b -> b.columns(Arrays.asList("P", null), withEmpty())));
    }

    @Test
    void columns_nullConsumerThrows() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> DefaultHeadBuilder.define(b -> b.columns(Arrays.asList("P"), (Consumer<HeadBuilder>) null)));
    }

    @Test
    void complexExampleFromJavadoc() {
        List<List<String>> head = DefaultHeadBuilder.define(b -> b.column("ID", 2)
                .columns("User Info", sub -> sub.column("Name").column("Age"))
                .column("Others", "Remark"));

        Assertions.assertEquals(4, head.size());
        Assertions.assertEquals(Arrays.asList("ID", "ID"), head.get(0));
        Assertions.assertEquals(Arrays.asList("User Info", "Name"), head.get(1));
        Assertions.assertEquals(Arrays.asList("User Info", "Age"), head.get(2));
        Assertions.assertEquals(Arrays.asList("Others", "Remark"), head.get(3));
    }
}
