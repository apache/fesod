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

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.fesod.sheet.exception.NoSuchMessageException;

/**
 * Abstract implementation of the {@link MessageSource} interface,
 * providing common functionality for message resolution, lookup, and interpolation.
 */
public abstract class AbstractMessageSource implements MessageSource {

    private static final char PREFIX = '{';
    private static final char SUFFIX = '}';
    private static final char ESCAPE = '\\';

    /**
     * whether to use the message code as default message.
     */
    @Getter
    @Setter
    private boolean useCodeAsDefaultMessage = false;

    /**
     * Resolve a message for the given code and locale.
     *
     * @param code the message code
     * @param locale the target locale to use
     * @return the resolved message
     * @throws NoSuchMessageException if the message is not found
     */
    @Override
    public String getMessage(String code, Locale locale) throws NoSuchMessageException {
        String msg = getMessageInternal(code, locale, new LinkedHashSet<>(4));
        if (msg != null) {
            return msg;
        }
        throw new NoSuchMessageException(code, locale);
    }

    /**
     * Interpolate the given message template.
     *
     * @param messageTemplate the message template
     * @param locale the target locale to use
     * @return the interpolated message
     */
    @Override
    public String interpolate(String messageTemplate, Locale locale) {
        if (StringUtils.isEmpty(messageTemplate)) {
            return null;
        }
        return replaceParameters(messageTemplate, locale, new LinkedHashSet<>(4));
    }

    /**
     * Resolve the raw message template for the given code.
     *
     * @param code the message code
     * @param locale the locale to use
     * @return the raw message
     */
    protected abstract String doResolveCode(String code, Locale locale);

    private String getRawMessage(String code, Locale locale) {
        if (code == null) {
            return null;
        }
        return doResolveCode(code, locale == null ? Locale.getDefault() : locale);
    }

    private String getMessageInternal(String code, Locale locale, Set<String> visitedParameters) {
        String msg = getRawMessage(code, locale);
        if (msg != null) {
            return replaceParameters(msg, locale, visitedParameters);
        }
        return isUseCodeAsDefaultMessage() ? code : null;
    }

    private String replaceParameters(String message, Locale locale, Set<String> visitedParameters) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        StringBuilder buf = new StringBuilder(message);
        int parentheses = 0;
        int startIndex = -1;

        for (int i = 0; i < buf.length(); i++) {
            char c = buf.charAt(i);

            // Handle redundant escape characters
            if (c == ESCAPE && i + 1 < buf.length()) {
                char next = buf.charAt(i + 1);
                if (next == PREFIX || next == SUFFIX || next == ESCAPE) {
                    buf.deleteCharAt(i);
                    continue;
                }
            }
            if (c == PREFIX) {
                if (startIndex == -1) {
                    startIndex = i;
                }
                parentheses++;
            } else if (c == SUFFIX && startIndex != -1) {
                parentheses--;
                if (parentheses == 0) {
                    int endIndex = i;
                    String parameter = buf.substring(startIndex + 1, endIndex);

                    if (!visitedParameters.add(parameter)) {
                        throw new IllegalArgumentException("Circular reference detected: '{"
                                + String.join(" -> ", visitedParameters) + " -> " + parameter + "}'");
                    }

                    try {
                        // Handle nested parameter
                        parameter = replaceParameters(parameter, locale, visitedParameters);
                        String value = getMessageInternal(parameter, locale, visitedParameters);

                        if (value != null) {
                            buf.replace(startIndex, endIndex + 1, value);
                            i = startIndex + value.length() - 1;
                        }
                    } finally {
                        visitedParameters.remove(parameter);
                    }

                    startIndex = -1;
                }
            }
        }
        return buf.toString();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    protected static final class CachedMessageKey {
        final Locale locale;
        final String code;
    }
}
