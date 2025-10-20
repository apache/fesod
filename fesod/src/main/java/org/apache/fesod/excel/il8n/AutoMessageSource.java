package org.apache.fesod.excel.il8n;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ExcelMessageSource
 *
 * @author GGBOUD
 * @date 2025/10/20
 */
public class AutoMessageSource implements ExcelMessageSource {
    private final static Map<String, Map<Locale, String>> MESSAGE_MAP = new HashMap<>();

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
        Assert.notNull(code, "Code must not be null");
        Assert.notNull(locale, "Locale must not be null");
        Assert.notNull(msg, "Message must not be null");
        MESSAGE_MAP.computeIfAbsent(code, (key) -> new HashMap<>(4)).put(locale, msg);
    }

    public void addMessages(Map<String, String> messages, Locale locale) {
        Assert.notNull(messages, "Messages Map must not be null");
        messages.forEach((code, msg) -> addMessage(code, locale, msg));
    }
}
