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

/*
 * Portions of this file are derived from Guava's Lists, Preconditions and CollectPreconditions.
 *
 * Copyright (C) 2007 The Guava Authors
 * Copyright (C) 2008 The Guava Authors
 */

package org.apache.fesod.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;

/**
 * Utility methods for creating and manipulating {@link List} (primarily {@link ArrayList})
 * instances.
 *
 * <p>This class is a zero-dependency replacement for the subset of
 * {@code com.google.common.collect.Lists} that Apache Fesod relies on, so callers do not need to
 * pull in Guava at runtime. All factory methods return a <i>mutable</i> {@code ArrayList} unless
 * otherwise noted.
 *
 * <p>This is a utility class and cannot be instantiated.
 */
public class ListUtils {
    /**
     * Utility classes must never be instantiated.
     */
    private ListUtils() {}

    /**
     * Creates a <i>mutable</i>, empty {@code ArrayList} instance (for Java 6 and earlier).
     *
     * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
     * deprecated. Instead, use the {@code ArrayList} {@linkplain ArrayList#ArrayList() constructor}
     * directly, taking advantage of the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
     *
     * @param <E> the element type of the list
     * @return a new, empty {@code ArrayList}
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the given elements.
     *
     * <p>The resulting list has a capacity sized to the number of elements plus a small padding,
     * so it will not grow when populated exactly to {@code elements.length}.
     *
     * @param <E> the element type of the list
     * @param elements the elements to place into the new list; must not be {@code null}
     *                 (individual elements may be {@code null})
     * @return a new {@code ArrayList} containing the given elements, in order
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public static <E> ArrayList<E> newArrayList(E... elements) {
        checkNotNull(elements);
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the elements produced by the
     * given iterator, in iteration order.
     *
     * <p>If {@code elements} is {@code null}, an empty list is returned.
     *
     * @param <E> the element type of the list
     * @param elements the iterator to consume; may be {@code null}
     * @return a new {@code ArrayList} containing the elements returned by {@code elements}
     */
    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        if (elements != null) {
            while (elements.hasNext()) {
                list.add(elements.next());
            }
        }
        return list;
    }

    /**
     * Creates a <i>mutable</i> {@code ArrayList} instance containing the elements produced by the
     * given iterable, in iteration order.
     *
     * <p><b>Note for Java 7 and later:</b> if {@code elements} is a {@link Collection}, you don't
     * need this method. Use the {@code ArrayList} {@linkplain ArrayList#ArrayList(Collection)
     * constructor} directly, taking advantage of the new <a href="http://goo.gl/iz2Wi">"diamond"
     * syntax</a>.
     *
     * @param <E> the element type of the list
     * @param elements the iterable to copy from; must not be {@code null}
     * @return a new {@code ArrayList} containing the elements of {@code elements}
     * @throws NullPointerException if {@code elements} is {@code null}
     */
    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        checkNotNull(elements); // for GWT
        // Let ArrayList's sizing logic work, if possible
        return (elements instanceof Collection)
                ? new ArrayList<>((Collection<? extends E>) elements)
                : newArrayList(elements.iterator());
    }

    /**
     * Creates an {@code ArrayList} instance backed by an array with the specified initial size;
     * simply delegates to {@link ArrayList#ArrayList(int)}.
     *
     * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
     * deprecated. Instead, use {@code new }{@link ArrayList#ArrayList(int) ArrayList}{@code <>(int)}
     * directly, taking advantage of the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
     * (Unlike here, there is no risk of overload ambiguity, since the {@code ArrayList} constructors
     * very wisely did not accept varargs.)
     *
     * @param <E> the element type of the list
     * @param initialArraySize the exact size of the initial backing array for the returned array
     *                         list ({@code ArrayList} documentation calls this value the
     *                         "capacity")
     * @return a new, empty {@code ArrayList} which is guaranteed not to resize itself unless its
     *         size reaches {@code initialArraySize + 1}
     * @throws IllegalArgumentException if {@code initialArraySize} is negative
     */
    public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
        checkNonnegative(initialArraySize, "initialArraySize");
        return new ArrayList<>(initialArraySize);
    }

    /**
     * Creates an {@code ArrayList} instance to hold {@code estimatedSize} elements, <i>plus</i> an
     * unspecified amount of padding; you almost certainly mean to call
     * {@link #newArrayListWithCapacity} (see that method for further advice on usage).
     *
     * <p><b>Note:</b> This method will soon be deprecated. Even in the rare case that you do want
     * some amount of padding, it's best if you choose your desired amount explicitly.
     *
     * @param <E> the element type of the list
     * @param estimatedSize an estimate of the eventual {@link List#size()} of the new list
     * @return a new, empty {@code ArrayList}, sized appropriately to hold the estimated number of
     *         elements
     * @throws IllegalArgumentException if {@code estimatedSize} is negative
     */
    public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
        return new ArrayList<>(computeArrayListCapacity(estimatedSize));
    }

    /**
     * Computes the backing-array capacity for an {@code ArrayList} expected to hold {@code arraySize}
     * elements, adding a small padding (roughly 10%) plus a constant of 5 to avoid repeated
     * re-allocations.
     *
     * <p>The result is saturated to {@link Integer#MAX_VALUE} so that an overflowing calculation
     * cannot produce a negative capacity.
     *
     * @param arraySize the expected number of elements; must not be negative
     * @return the capacity to use for the backing array
     * @throws IllegalArgumentException if {@code arraySize} is negative
     */
    static int computeArrayListCapacity(int arraySize) {
        checkNonnegative(arraySize, "arraySize");
        return IntUtils.saturatedCast(5L + arraySize + (arraySize / 10));
    }

    /**
     * Validates that the given value is non-negative.
     *
     * @param value the value to check
     * @param name the name to use in the exception message when {@code value} is negative
     * @return {@code value}, if non-negative
     * @throws IllegalArgumentException if {@code value} is negative
     */
    static int checkNonnegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
        }
        return value;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param <T> the type of the reference
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T extends @NonNull Object> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }
}
