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

package org.apache.fesod.spring.boot.autoconfigure;

import java.util.Locale;
import org.apache.fesod.excel.enums.CacheLocationEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Root configuration properties for Fesod Spring Boot starter.
 * <p>
 * All properties are bound under the prefix {@code fesod}.
 * Example (application.yml):
 * <pre>
 * fesod:
 *   global:
 *     auto-trim: true
 *     locale: en_US
 *   reader:
 *     ignore-empty-row: true
 *   writer:
 *     in-memory: true
 * </pre>
 */
@ConfigurationProperties(prefix = "fesod")
public class FesodProperties {

    /** Global shared configuration applied to both reading and writing. */
    private Global global = new Global();
    /** Reader specific configuration. */
    private Reader reader = new Reader();
    /** Writer specific configuration. */
    private Writer writer = new Writer();

    public Global getGlobal() {
        return global;
    }

    public void setGlobal(Global global) {
        this.global = global;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Global configuration options that affect both reading and writing operations.
     */
    public static class Global {
        /**
         * Whether to automatically trim leading and trailing whitespace from cell string values.
         */
        private Boolean autoTrim;
        /**
         * Whether to automatically remove invisible / special control characters (strip) from cell values.
         */
        private Boolean autoStrip;
        /**
         * Whether to interpret dates using the 1904 date windowing (common in older Mac Excel files).
         */
        private Boolean use1904Windowing;
        /**
         * Whether numeric values which are large or small should be written using scientific notation.
         */
        private Boolean useScientificFormat;
        /**
         * The default locale to use for formatting and parsing (e.g. number, date patterns). If not set, JVM default is used.
         */
        private Locale locale;
        /**
         * Where to cache temporary file based data structures when writing / reading large workbooks.
         */
        private CacheLocationEnum filedCacheLocation;

        public Boolean getAutoTrim() {
            return autoTrim;
        }

        public void setAutoTrim(Boolean autoTrim) {
            this.autoTrim = autoTrim;
        }

        public Boolean getAutoStrip() {
            return autoStrip;
        }

        public void setAutoStrip(Boolean autoStrip) {
            this.autoStrip = autoStrip;
        }

        public Boolean getUse1904Windowing() {
            return use1904Windowing;
        }

        public void setUse1904Windowing(Boolean use1904Windowing) {
            this.use1904Windowing = use1904Windowing;
        }

        public Boolean getUseScientificFormat() {
            return useScientificFormat;
        }

        public void setUseScientificFormat(Boolean useScientificFormat) {
            this.useScientificFormat = useScientificFormat;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public CacheLocationEnum getFiledCacheLocation() {
            return filedCacheLocation;
        }

        public void setFiledCacheLocation(CacheLocationEnum filedCacheLocation) {
            this.filedCacheLocation = filedCacheLocation;
        }
    }

    /**
     * Reader specific configuration options.
     */
    public static class Reader {
        /**
         * Whether empty rows (all cells empty) should be ignored during reading.
         */
        private Boolean ignoreEmptyRow;
        /**
         * Whether the underlying input stream should be closed automatically after reading completes.
         */
        private Boolean autoCloseStream;
        /**
         * Whether reading must be performed strictly from an InputStream (instead of file path / other sources).
         */
        private Boolean mandatoryUseInputStream;

        public Boolean getIgnoreEmptyRow() {
            return ignoreEmptyRow;
        }

        public void setIgnoreEmptyRow(Boolean ignoreEmptyRow) {
            this.ignoreEmptyRow = ignoreEmptyRow;
        }

        public Boolean getAutoCloseStream() {
            return autoCloseStream;
        }

        public void setAutoCloseStream(Boolean autoCloseStream) {
            this.autoCloseStream = autoCloseStream;
        }

        public Boolean getMandatoryUseInputStream() {
            return mandatoryUseInputStream;
        }

        public void setMandatoryUseInputStream(Boolean mandatoryUseInputStream) {
            this.mandatoryUseInputStream = mandatoryUseInputStream;
        }
    }

    /**
     * Writer specific configuration options.
     */
    public static class Writer {
        /**
         * Whether the underlying output stream should be closed automatically after writing completes.
         */
        private Boolean autoCloseStream;
        /**
         * Whether to write a UTF-8 BOM (Byte Order Mark) for CSV/text outputs where applicable.
         */
        private Boolean withBom;
        /**
         * Whether writing operations should be performed entirely in memory (might increase memory usage for large files).
         */
        private Boolean inMemory;
        /**
         * Whether the Excel file should still be written/flushed when an exception occurs during writing.
         */
        private Boolean writeExcelOnException;

        public Boolean getAutoCloseStream() {
            return autoCloseStream;
        }

        public void setAutoCloseStream(Boolean autoCloseStream) {
            this.autoCloseStream = autoCloseStream;
        }

        public Boolean getWithBom() {
            return withBom;
        }

        public void setWithBom(Boolean withBom) {
            this.withBom = withBom;
        }

        public Boolean getInMemory() {
            return inMemory;
        }

        public void setInMemory(Boolean inMemory) {
            this.inMemory = inMemory;
        }

        public Boolean getWriteExcelOnException() {
            return writeExcelOnException;
        }

        public void setWriteExcelOnException(Boolean writeExcelOnException) {
            this.writeExcelOnException = writeExcelOnException;
        }
    }
}
