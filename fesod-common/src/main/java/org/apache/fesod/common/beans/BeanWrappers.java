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

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.fesod.common.util.ValidateUtils;

/**
 * This class is to be used provide access to the default {@link BeanWrapper} instances.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanWrappers {

    private static BeanWrapperProvider provider;

    static {
        ServiceLoader<BeanWrapperProvider> loader = ServiceLoader.load(BeanWrapperProvider.class);

        BeanWrapperProvider tmpProvider = null;
        for (BeanWrapperProvider candidate : loader) {
            if (tmpProvider == null || candidate.getOrder() < tmpProvider.getOrder()) {
                tmpProvider = candidate;
            }
        }

        if (tmpProvider == null) {
            throw new ServiceConfigurationError("No valid BeanWrapperProvider found on the classpath");
        }
        provider = tmpProvider;
    }

    public static BeanWrapper create(Object bean) {
        if (bean == null) {
            return null;
        }
        return provider.create(bean);
    }

    public static void setProvider(BeanWrapperProvider provider) {
        BeanWrappers.provider = ValidateUtils.notNull(provider, "BeanWrapperProvider cannot be null");
    }
}
