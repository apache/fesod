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

package org.apache.fesod.sheet.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

/**
 * A {@link MessageSource} implementation backed by a resource bundle.
 */
public class ResourceBundleMessageSource extends AbstractMessageSource {

    /**
     * The default base name.
     */
    private static final String DEFAULT_BASE_NAME = "messages";

    /**
     * The base name of the resource bundle.
     */
    private final String baseName;

    /**
     * The control which gives information for the resource bundle loading process.
     */
    private final ResourceBundle.Control control;

    /**
     * Cache to hold loaded ResourceBundles.
     */
    private final Map<Locale, ResourceBundleHolder> cachedBundles = new ConcurrentHashMap<>();

    /**
     * Cache to hold resolved messages.
     */
    private final Map<CachedMessageKey, MessageHolder> cachedMessages = new ConcurrentHashMap<>();

    public ResourceBundleMessageSource() {
        this(DEFAULT_BASE_NAME, false, null);
    }

    public ResourceBundleMessageSource(String baseName) {
        this(baseName, false, null);
    }

    public ResourceBundleMessageSource(String baseName, boolean useCodeAsDefault) {
        this(baseName, useCodeAsDefault, null);
    }

    public ResourceBundleMessageSource(String baseName, ResourceBundle.Control control) {
        this(baseName, false, control);
    }

    public ResourceBundleMessageSource(String baseName, boolean useCodeAsDefault, ResourceBundle.Control control) {
        Validate.notBlank(baseName, "Base name must not be empty");

        this.baseName = baseName;
        this.control = control;
        setUseCodeAsDefaultMessage(useCodeAsDefault);
    }

    @Override
    protected String doResolveCode(String code, Locale locale) {
        CachedMessageKey key = new CachedMessageKey(locale, code);
        MessageHolder holder = cachedMessages.get(key);
        if (holder != null) {
            return holder.message;
        }

        ResourceBundle bundle = getResourceBundle(locale);
        if (bundle == null) {
            cachedMessages.put(key, MessageHolder.NONE);
            return null;
        }

        holder = getMessageHolder(bundle, code);
        cachedMessages.put(key, holder);
        return holder.message;
    }

    /**
     * Returns a ResourceBundle for the given locale and baseName.
     */
    private ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundleHolder holder = this.cachedBundles.get(locale);
        if (holder != null) {
            return holder.bundle;
        }
        try {
            holder = new ResourceBundleHolder(doGetBundle(locale));
        } catch (MissingResourceException ex) {
            holder = ResourceBundleHolder.NONE;
        }

        this.cachedBundles.put(locale, holder);
        return holder.bundle;
    }

    private ResourceBundle doGetBundle(Locale locale) throws MissingResourceException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ResourceBundleMessageSource.class.getClassLoader();
        }
        if (control == null) {
            return ResourceBundle.getBundle(baseName, locale, classLoader);
        }
        return ResourceBundle.getBundle(baseName, locale, classLoader, control);
    }

    /**
     * Returns the message value for the specified key.
     */
    private MessageHolder getMessageHolder(ResourceBundle bundle, String key) {
        if (!bundle.containsKey(key)) {
            return MessageHolder.NONE;
        }
        try {
            return new MessageHolder(bundle.getString(key));
        } catch (MissingResourceException ex) {
            return MessageHolder.NONE;
        }
    }

    @RequiredArgsConstructor
    private static class ResourceBundleHolder {
        static final ResourceBundleHolder NONE = new ResourceBundleHolder(null);

        final ResourceBundle bundle;
    }

    @RequiredArgsConstructor
    private static class MessageHolder {
        static final MessageHolder NONE = new MessageHolder(null);

        final String message;
    }
}
