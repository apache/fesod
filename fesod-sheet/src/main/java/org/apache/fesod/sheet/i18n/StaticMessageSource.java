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
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fesod.sheet.util.Validate;

/**
 * Simple {@link MessageSource} implementation backed by an in-memory map.
 */
public class StaticMessageSource extends AbstractMessageSource {

    private final Map<CachedMessageKey, String> cachedMessages = new ConcurrentHashMap<>();

    /**
     * Associate the given message with the given code.
     *
     * @param code    the code
     * @param message the message
     */
    public void addMessage(String code, String message) {
        addMessage(code, message, Locale.getDefault());
    }

    /**
     * Associate the given message with the given code.
     *
     * @param code    the code
     * @param message the message
     * @param locale  the target locale to use
     */
    public void addMessage(String code, String message, Locale locale) {
        Validate.notNull(code, "Code must not be null");
        Validate.notNull(message, "Message must not be null");
        Validate.notNull(locale, "Locale must not be null");

        cachedMessages.put(new CachedMessageKey(locale, code), message);
    }

    /**
     * Associate the given message values with the given keys as codes.
     *
     * @param messages the messages
     * @param locale   the target locale to use
     */
    public void addMessages(Map<String, String> messages, Locale locale) {
        Validate.notNull(messages, "Messages map must not be null");
        messages.forEach((code, message) -> addMessage(code, message, locale));
    }

    @Override
    protected String doResolveCode(String code, Locale locale) {
        return cachedMessages.get(new CachedMessageKey(locale, code));
    }
}
