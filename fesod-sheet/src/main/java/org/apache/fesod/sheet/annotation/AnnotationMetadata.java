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

package org.apache.fesod.sheet.annotation;

import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A wrapper class for resolved annotation instance.
 */
@EqualsAndHashCode
@Getter(AccessLevel.PACKAGE)
class AnnotationMetadata {

    private final AnnotationAttributes attributes;
    private final List<AliasFor> aliases;

    AnnotationMetadata(AnnotationAttributes attributes, List<AliasFor> aliases) {
        this.attributes = attributes;
        this.aliases = aliases;
    }

    void applyAliasFor(AliasFor aliasFor) {
        if (!attributes.isAnnotationTypeEqual(aliasFor.getTarget())) {
            return;
        }

        attributes.put(aliasFor.getAttribute(), aliasFor.getValue());
        propagateAliasValue(aliasFor);
    }

    /**
     * Propagates the applied alias value to downstream chained aliases.
     * <p>
     * If any alias declared on this annotation originates from the parent's target attribute,
     * its value is updated so that the newly assigned value cascades to the next nesting level.
     */
    private void propagateAliasValue(AliasFor parentAliasFor) {
        for (AliasFor current : aliases) {
            if (Objects.equals(current.getCustomAttribute(), parentAliasFor.getAttribute())) {
                current.setValue(parentAliasFor.getValue());
            }
        }
    }
}
