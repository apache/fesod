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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;

/**
 * A builder for configuring sheet headers in No-Bean mode.
 *
 * <p>Example - complex headers:</p>
 * <pre>
 * {@code FesodSheet.write(pathname)
 *           .head(builder -> builder
 *              .column("ID", 2)
 *              .columns("User Info", sub -> sub.column("Name").column("Age"))
 *              .column("Others", "Remark")
 *           )
 * // Equivalent to:
 * List<List<String>> head = new ArrayList<>();
 * head.add(new ArrayList<>(Arrays.asList("ID", "ID")));
 * head.add(new ArrayList<>(Arrays.asList("User Info", "Name")));
 * head.add(new ArrayList<>(Arrays.asList("User Info", "Age")));
 * head.add(new ArrayList<>(Arrays.asList("Others", "Remark")));
 *
 * FesodSheet.write(pathname)
 *           .head(head)}
 * </pre>
 *
 * <p>Example - single-level headers:</p>
 * <pre>
 * {@code FesodSheet.write(pathname)
 *           .head(HeadBuilder.forSimple("ID", "Name", "Age", "Remark"))
 * // Equivalent to:
 * List<List<String>> head = new ArrayList<>();
 * head.add(new ArrayList<>(Arrays.asList("ID")));
 * head.add(new ArrayList<>(Arrays.asList("Name")));
 * head.add(new ArrayList<>(Arrays.asList("Age")));
 * head.add(new ArrayList<>(Arrays.asList("Remark")));
 *
 * FesodSheet.write(pathname)
 *     .head(head)}
 * </pre>
 *
 * @see AbstractParameterBuilder#head(Consumer)
 */
public interface HeadBuilder {

    /**
     * Define a single column with a fixed head names (containing at least one column).
     *
     * @param headName the first header name (must not be {@code null})
     * @param subHeadNames optional sublevel header names
     * @return this for builder chains
     */
    HeadBuilder column(String headName, String... subHeadNames);

    /**
     * Define a single column with a repeating header name at multiple levels.
     *
     * @param headName the header name to repeat (must not be {@code null})
     * @param repeat the number of times to repeat the name
     * @return this for builder chains
     */
    HeadBuilder column(String headName, int repeat);

    /**
     * Declare a shared parent header name to group sub-headers.
     *
     * @param parentHeadName the shared parent header name (must not be {@code null})
     * @param subHeadBuilderConsumer the consumer to define sub-headers
     * @return this for builder chains
     */
    default HeadBuilder columns(String parentHeadName, Consumer<HeadBuilder> subHeadBuilderConsumer) {
        return columns(Collections.singletonList(parentHeadName), subHeadBuilderConsumer);
    }

    /**
     * Declare multiple shared parent header names to group sub-headers.
     *
     * @param parentHeadNames  the shared parent header names (must not be {@code null} or empty)
     * @param subHeadBuilderConsumer the consumer to define sub-headers
     * @return this for builder chains
     */
    HeadBuilder columns(List<String> parentHeadNames, Consumer<HeadBuilder> subHeadBuilderConsumer);

    /**
     * Build a simple single-level header list (containing at least one column).
     *
     * @param headName the first header name (must not be {@code null})
     * @param otherHeadNames optional other header names
     * @return a simple single-level header list
     */
    static List<List<String>> forSimple(String headName, String... otherHeadNames) {
        Validate.notNull(headName, "header name must not be null");

        int initialCapacity = ((otherHeadNames == null) ? 0 : otherHeadNames.length) + 1;
        List<List<String>> result = new ArrayList<>(initialCapacity);

        List<String> firstHead = new ArrayList<>(1);
        firstHead.add(headName);
        result.add(firstHead);

        if (otherHeadNames != null) {
            Validate.noNullElements(otherHeadNames, "other header names must not contain null elements");
            for (String otherHeadName : otherHeadNames) {
                List<String> otherHead = new ArrayList<>(1);
                otherHead.add(otherHeadName);
                result.add(otherHead);
            }
        }
        return result;
    }
}
