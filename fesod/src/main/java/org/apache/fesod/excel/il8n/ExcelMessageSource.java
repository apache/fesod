package org.apache.fesod.excel.il8n;

import java.util.Locale;

/**
 * IExcelMessageSource
 *
 * @author GGBOUD
 * @date 2025/10/20
 */
public interface ExcelMessageSource {

    /**
     * resolveCode 转换code
     *
     * @param code String
     * @param locale Locale
     * @return String
     */
    String resolveCode(String code, Locale locale);


    /**
     * addMessage 增加词条
     *
     * @param code String
     * @param locale Locale
     * @param msg String
     */
    void addMessage(String code, Locale locale, String msg);
}
