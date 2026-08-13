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

package org.apache.fesod.common.beans;

import java.util.Map;
import java.util.Set;

/**
 * An interface for classes that can access named properties.
 */
public interface BeanWrapper {

    /**
     * Get the current value of the property.
     *
     * @param propertyName the name of the property to get the value
     * @return the value of the property
     */
    Object getProperty(String propertyName);

    /**
     * Set the value to current property.
     *
     * @param propertyName the name of the property to set the value
     * @param value the value for setting
     */
    void setProperty(String propertyName, Object value);

    /**
     * Batch set from a {@link Map}.
     *
     * @param properties a Map to take properties from
     */
    void setProperties(Map<String, Object> properties);

    /**
     * Returns a {@link Set} of the bean property names.
     *
     * @return a {@link Set} of the bean property names
     */
    Set<String> getPropertyNames();

    boolean containsProperty(String propertyName);

    int getPropertySize();

    /**
     * Returns the type of the named property.
     *
     * @param propertyName the name of the property
     * @return the {@link Class} of the property, or {@code null} if no such property exists
     */
    Class<?> getPropertyType(String propertyName);

    /**
     * Returns the type of the wrapped bean instance.
     */
    default Class<?> getWrappedClass() {
        return unwrap().getClass();
    }

    /**
     * Returns the wrapped bean object.
     */
    Object unwrap();
}
