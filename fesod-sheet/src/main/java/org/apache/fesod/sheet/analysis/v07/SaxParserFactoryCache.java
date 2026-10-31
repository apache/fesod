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
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.analysis.v07;

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParserFactory;

/**
 * Thread-local cache of configured {@link SAXParserFactory} instances. {@code SAXParserFactory} is not
 * thread-safe, so factories are cached per thread rather than shared; re-creating a factory per parse
 * re-runs the whole JAXP service lookup and catalog feature initialization, which showed up as
 * allocation churn on every analysis run.
 */
final class SaxParserFactoryCache {

    private static final ThreadLocal<SAXParserFactory> DEFAULT_FACTORY = ThreadLocal.withInitial(() -> {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        configure(factory);
        return factory;
    });

    /**
     * Named factory implementations requested through {@code saxParserFactoryName}, cached per thread
     * per name.
     */
    private static final Map<String, ThreadLocal<SAXParserFactory>> NAMED_FACTORIES = new HashMap<>();

    private SaxParserFactoryCache() {}

    /**
     * Returns the cached factory for the requested implementation name, or the default factory when
     * the name is null or empty.
     */
    static SAXParserFactory get(String factoryName) {
        if (factoryName == null || factoryName.isEmpty()) {
            return DEFAULT_FACTORY.get();
        }
        ThreadLocal<SAXParserFactory> cached;
        synchronized (NAMED_FACTORIES) {
            cached = NAMED_FACTORIES.get(factoryName);
            if (cached == null) {
                cached = ThreadLocal.withInitial(() -> {
                    SAXParserFactory factory = SAXParserFactory.newInstance(factoryName, null);
                    configure(factory);
                    return factory;
                });
                NAMED_FACTORIES.put(factoryName, cached);
            }
        }
        return cached.get();
    }

    private static void configure(SAXParserFactory factory) {
        setFeatureQuietly(factory, "http://apache.org/xml/features/disallow-doctype-decl", true);
        setFeatureQuietly(factory, "http://xml.org/sax/features/external-general-entities", false);
        setFeatureQuietly(factory, "http://xml.org/sax/features/external-parameter-entities", false);
    }

    private static void setFeatureQuietly(SAXParserFactory factory, String name, boolean value) {
        try {
            factory.setFeature(name, value);
        } catch (Throwable ignore) {
            // parser implementations may reject optional hardening features; matches previous behavior
        }
    }
}
