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

package org.apache.fesod.excel.demo.read;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.fesod.excel.i18n.ExcelMessageSource;

/**
 * AutoMessageSource
 *
 * @author GGBOUD
 * @date 2025/10/20
 */
public class AutoMessageSource implements ExcelMessageSource {
    private static final Map<String, Map<Locale, String>> MESSAGE_MAP = new HashMap<>();

    @Override
    public String resolveCode(String code, Locale locale) {
        Map<Locale, String> localeMap = MESSAGE_MAP.get(code);
        if (localeMap == null) {
            return code;
        } else {
            String message = localeMap.get(locale);
            return message == null ? code : message;
        }
    }

    @Override
    public void addMessage(String code, Locale locale, String msg) {
        MESSAGE_MAP.computeIfAbsent(code, (key) -> new HashMap<>(4)).put(locale, msg);
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        messages.forEach((code, msg) -> addMessage(code, locale, msg));
    }
}
