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

package org.apache.fesod.spring.boot;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.fesod.excel.FastExcelFactory;
import org.apache.fesod.excel.metadata.AbstractParameterBuilder;
import org.apache.fesod.excel.read.builder.AbstractExcelReaderParameterBuilder;
import org.apache.fesod.excel.read.builder.ExcelReaderBuilder;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.write.builder.ExcelWriterBuilder;
import org.apache.fesod.spring.boot.autoconfigure.FesodProperties;
import org.apache.fesod.spring.boot.autoconfigure.FesodReaderBuilderCustomizer;
import org.apache.fesod.spring.boot.autoconfigure.FesodWriterBuilderCustomizer;

/**
 * Convenience facade that exposes the {@link FastExcelFactory} builders as Spring beans
 * with sensible defaults driven by {@link FesodProperties}.
 */
public class FesodTemplate {

    private final FesodProperties properties;
    private final List<FesodWriterBuilderCustomizer> writerCustomizers;
    private final List<FesodReaderBuilderCustomizer> readerCustomizers;

    public FesodTemplate(
            FesodProperties properties,
            List<FesodWriterBuilderCustomizer> writerCustomizers,
            List<FesodReaderBuilderCustomizer> readerCustomizers) {
        this.properties = properties != null ? properties : new FesodProperties();
        this.writerCustomizers = writerCustomizers == null ? Collections.emptyList() : writerCustomizers;
        this.readerCustomizers = readerCustomizers == null ? Collections.emptyList() : readerCustomizers;
    }

    public ExcelWriterBuilder writer() {
        return customizeWriter(FastExcelFactory.write());
    }

    public ExcelWriterBuilder writer(File file) {
        return customizeWriter(FastExcelFactory.write(file));
    }

    public ExcelWriterBuilder writer(File file, Class<?> head) {
        return customizeWriter(FastExcelFactory.write(file, head));
    }

    public ExcelWriterBuilder writer(String pathName) {
        return customizeWriter(FastExcelFactory.write(pathName));
    }

    public ExcelWriterBuilder writer(String pathName, Class<?> head) {
        return customizeWriter(FastExcelFactory.write(pathName, head));
    }

    public ExcelWriterBuilder writer(OutputStream outputStream) {
        return customizeWriter(FastExcelFactory.write(outputStream));
    }

    public ExcelWriterBuilder writer(OutputStream outputStream, Class<?> head) {
        return customizeWriter(FastExcelFactory.write(outputStream, head));
    }

    public ExcelReaderBuilder reader() {
        return customizeReader(FastExcelFactory.read());
    }

    public ExcelReaderBuilder reader(File file) {
        return customizeReader(FastExcelFactory.read(file));
    }

    public ExcelReaderBuilder reader(File file, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(file, readListener));
    }

    public ExcelReaderBuilder reader(File file, Class<?> head, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(file, head, readListener));
    }

    public ExcelReaderBuilder reader(String pathName) {
        return customizeReader(FastExcelFactory.read(pathName));
    }

    public ExcelReaderBuilder reader(String pathName, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(pathName, readListener));
    }

    public ExcelReaderBuilder reader(String pathName, Class<?> head, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(pathName, head, readListener));
    }

    public ExcelReaderBuilder reader(InputStream inputStream) {
        return customizeReader(FastExcelFactory.read(inputStream));
    }

    public ExcelReaderBuilder reader(InputStream inputStream, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(inputStream, readListener));
    }

    public ExcelReaderBuilder reader(InputStream inputStream, Class<?> head, ReadListener<?> readListener) {
        return customizeReader(FastExcelFactory.read(inputStream, head, readListener));
    }

    private ExcelWriterBuilder customizeWriter(ExcelWriterBuilder builder) {
        applyGlobal(builder);
        applyWriterDefaults(builder);
        writerCustomizers.forEach(customizer -> customizer.customize(builder));
        return builder;
    }

    private ExcelReaderBuilder customizeReader(ExcelReaderBuilder builder) {
        applyGlobal(builder);
        applyReaderDefaults(builder);
        readerCustomizers.forEach(customizer -> customizer.customize(builder));
        return builder;
    }

    private void applyGlobal(AbstractParameterBuilder<?, ?> builder) {
        FesodProperties.Global global = properties.getGlobal();
        if (global == null) {
            return;
        }
        if (global.getAutoTrim() != null) {
            builder.autoTrim(global.getAutoTrim());
        }
        if (global.getAutoStrip() != null) {
            builder.autoStrip(global.getAutoStrip());
        }
        if (global.getUse1904Windowing() != null) {
            builder.use1904windowing(global.getUse1904Windowing());
        }
        Locale locale = global.getLocale();
        if (locale != null) {
            builder.locale(locale);
        }
        if (global.getUseScientificFormat() != null && builder instanceof AbstractExcelReaderParameterBuilder) {
            AbstractExcelReaderParameterBuilder<?, ?> readerBuilder =
                    (AbstractExcelReaderParameterBuilder<?, ?>) builder;
            readerBuilder.useScientificFormat(global.getUseScientificFormat());
        }
        if (global.getFiledCacheLocation() != null) {
            builder.filedCacheLocation(global.getFiledCacheLocation());
        }
    }

    private void applyWriterDefaults(ExcelWriterBuilder builder) {
        FesodProperties.Writer writer = properties.getWriter();
        if (writer == null) {
            return;
        }
        if (writer.getAutoCloseStream() != null) {
            builder.autoCloseStream(writer.getAutoCloseStream());
        }
        if (writer.getWithBom() != null) {
            builder.withBom(writer.getWithBom());
        }
        if (writer.getInMemory() != null) {
            builder.inMemory(writer.getInMemory());
        }
        if (writer.getWriteExcelOnException() != null) {
            builder.writeExcelOnException(writer.getWriteExcelOnException());
        }
    }

    private void applyReaderDefaults(ExcelReaderBuilder builder) {
        FesodProperties.Reader reader = properties.getReader();
        if (reader == null) {
            return;
        }
        if (reader.getIgnoreEmptyRow() != null) {
            builder.ignoreEmptyRow(reader.getIgnoreEmptyRow());
        }
        if (reader.getAutoCloseStream() != null) {
            builder.autoCloseStream(reader.getAutoCloseStream());
        }
        if (reader.getMandatoryUseInputStream() != null) {
            builder.mandatoryUseInputStream(reader.getMandatoryUseInputStream());
        }
    }
}
