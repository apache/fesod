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

package org.apache.fesod.sheet.testkit.util;

import java.util.List;
import org.apache.fesod.sheet.model.SimpleData;
import org.junit.jupiter.api.Assertions;

/**
 * Common assertion patterns and utility methods for test assertions.
 * Provides reusable assertion logic to reduce duplication in test methods.
 */
public class AssertionHelper {

    /**
     * Asserts that two SimpleData objects are equal, considering only the specified fields.
     */
    public static void assertSimpleDataEquals(SimpleData expected, SimpleData actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null || actual == null) {
            Assertions.fail("One of the SimpleData objects is null: expected=" + expected + ", actual=" + actual);
        }

        Assertions.assertEquals(expected.getName(), actual.getName(), "SimpleData names do not match");
        Assertions.assertEquals(expected.getNumber(), actual.getNumber(), "SimpleData numbers do not match");
        Assertions.assertEquals(expected.getInteger(), actual.getInteger(), "SimpleData integers do not match");
    }

    /**
     * Asserts that two lists of SimpleData objects are equal in content and order.
     */
    public static void assertSimpleDataListEquals(List<SimpleData> expected, List<SimpleData> actual) {
        Assertions.assertEquals(expected.size(), actual.size(), "List sizes do not match");

        for (int i = 0; i < expected.size(); i++) {
            assertSimpleDataEquals(expected.get(i), actual.get(i));
        }
    }

    /**
     * Asserts that a list of SimpleData objects has the expected count.
     */
    public static void assertSimpleDataListSize(List<SimpleData> actual, int expectedSize) {
        Assertions.assertEquals(
                expectedSize, actual.size(), "Expected list size " + expectedSize + " but was " + actual.size());
    }

    /**
     * Asserts that the first element in a SimpleData list matches the expected values.
     */
    public static void assertFirstSimpleData(List<SimpleData> list, String expectedName, Double expectedNumber) {
        Assertions.assertFalse(list.isEmpty(), "List is empty");
        SimpleData first = list.get(0);
        Assertions.assertEquals(expectedName, first.getName(), "First element name mismatch");
        Assertions.assertEquals(expectedNumber, first.getNumber(), "First element number mismatch");
    }

    /**
     * Asserts that the last element in a SimpleData list matches the expected values.
     */
    public static void assertLastSimpleData(List<SimpleData> list, String expectedName, Double expectedNumber) {
        Assertions.assertFalse(list.isEmpty(), "List is empty");
        SimpleData last = list.get(list.size() - 1);
        Assertions.assertEquals(expectedName, last.getName(), "Last element name mismatch");
        Assertions.assertEquals(expectedNumber, last.getNumber(), "Last element number mismatch");
    }

    /**
     * Checks if a list contains a SimpleData with the specified name.
     */
    public static boolean containsSimpleDataWithName(List<SimpleData> list, String name) {
        return list.stream().anyMatch(data -> name.equals(data.getName()));
    }

    /**
     * Asserts that a list contains a SimpleData with the specified name.
     */
    public static void assertContainsSimpleDataWithName(List<SimpleData> list, String name) {
        boolean found = containsSimpleDataWithName(list, name);
        Assertions.assertTrue(found, "List does not contain SimpleData with name: " + name);
    }

    /**
     * Generic assertion that checks if an exception message contains expected text.
     */
    public static void assertExceptionMessageContains(Throwable exception, String expectedText) {
        String message = exception.getMessage();
        Assertions.assertNotNull(message, "Exception message is null");
        Assertions.assertTrue(
                message.contains(expectedText),
                "Exception message '" + message + "' does not contain expected text: " + expectedText);
    }

    /**
     * Asserts that a value is within a specified range (inclusive).
     */
    public static void assertInRange(double value, double min, double max) {
        Assertions.assertTrue(
                value >= min && value <= max, "Value " + value + " is not within range [" + min + ", " + max + "]");
    }

    /**
     * Asserts that a value is within a specified range (inclusive).
     */
    public static void assertInRange(int value, int min, int max) {
        Assertions.assertTrue(
                value >= min && value <= max, "Value " + value + " is not within range [" + min + ", " + max + "]");
    }
}
