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
 * {@link ConfigurationProperties} that drive the default behaviour of the Fesod Spring Boot starter.
 */
@ConfigurationProperties(prefix = "fesod")
public class FesodProperties {

    private Global global = new Global();
    private Reader reader = new Reader();
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

    public static class Global {
        private Boolean autoTrim;
        private Boolean autoStrip;
        private Boolean use1904Windowing;
        private Boolean useScientificFormat;
        private Locale locale;
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

    public static class Reader {
        private Boolean ignoreEmptyRow;
        private Boolean autoCloseStream;
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

    public static class Writer {
        private Boolean autoCloseStream;
        private Boolean withBom;
        private Boolean inMemory;
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
