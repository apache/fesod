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

package org.apache.fesod.sheet.write.view;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.fesod.sheet.annotation.write.ExcelView;

/**
 * View matcher that resolves view-based on string
 * identifiers declared in {@code @ExcelView#asNames()}.
 */
@EqualsAndHashCode
public class NameBasedViewMatcher implements WriteViewMatcher {

    private final Collection<String> expectedGroups;

    public NameBasedViewMatcher(String... expectedGroups) {
        this(ArrayUtils.isEmpty(expectedGroups) ? Collections.emptyList() : Arrays.asList(expectedGroups));
    }

    public NameBasedViewMatcher(Collection<String> expectedGroups) {
        if (CollectionUtils.isEmpty(expectedGroups)) {
            throw new IllegalArgumentException("Name-based view groups must not be empty");
        }
        this.expectedGroups = Collections.unmodifiableCollection(expectedGroups);
    }

    @Override
    public boolean matches(Field field) {
        String[] fieldGroups = Optional.ofNullable(field.getAnnotation(ExcelView.class))
                .map(ExcelView::asNames)
                .orElse(new String[0]);

        if (ArrayUtils.isEmpty(fieldGroups)) {
            return false;
        }

        return Arrays.stream(fieldGroups).anyMatch(fieldGroup -> expectedGroups.stream()
                .anyMatch(expectedGroup -> expectedGroup.equals(fieldGroup)));
    }
}
