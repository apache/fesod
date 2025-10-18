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

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.fesod.excel.read.builder.ExcelReaderBuilder;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.write.builder.ExcelWriterBuilder;
import org.apache.fesod.excel.write.metadata.WriteWorkbook;
import org.apache.fesod.spring.boot.FesodTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.util.ReflectionTestUtils;

class FesodAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(FesodAutoConfiguration.class));

    @Test
    void contextProvidesFesodTemplate() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(FesodTemplate.class));
    }

    @Test
    void globalAndSectionPropertiesAreApplied() {
        contextRunner
                .withPropertyValues(
                        "fesod.global.auto-trim=false",
                        "fesod.global.use1904-windowing=true",
                        "fesod.writer.with-bom=false",
                        "fesod.reader.ignore-empty-row=false")
                .run(context -> {
                    FesodTemplate template = context.getBean(FesodTemplate.class);

                    ExcelWriterBuilder writerBuilder = template.writer();
                    WriteWorkbook writeWorkbook =
                            (WriteWorkbook) ReflectionTestUtils.getField(writerBuilder, "writeWorkbook");
                    assertThat(writeWorkbook).isNotNull();
                    assertThat(writeWorkbook.getAutoTrim()).isFalse();
                    assertThat(writeWorkbook.getUse1904windowing()).isTrue();
                    assertThat(writeWorkbook.getWithBom()).isFalse();

                    ExcelReaderBuilder readerBuilder = template.reader();
                    ReadWorkbook readWorkbook =
                            (ReadWorkbook) ReflectionTestUtils.getField(readerBuilder, "readWorkbook");
                    assertThat(readWorkbook).isNotNull();
                    assertThat(readWorkbook.getIgnoreEmptyRow()).isFalse();
                    assertThat(readWorkbook.getAutoTrim()).isFalse();
                });
    }

    @Test
    void customizersAreInvoked() {
        contextRunner
                .withBean(
                        "writerCustomizer",
                        FesodWriterBuilderCustomizer.class,
                        () -> builder -> builder.inMemory(Boolean.TRUE))
                .withBean(
                        "readerCustomizer",
                        FesodReaderBuilderCustomizer.class,
                        () -> builder -> builder.mandatoryUseInputStream(Boolean.TRUE))
                .run(context -> {
                    FesodTemplate template = context.getBean(FesodTemplate.class);

                    ExcelWriterBuilder writerBuilder = template.writer();
                    WriteWorkbook writeWorkbook =
                            (WriteWorkbook) ReflectionTestUtils.getField(writerBuilder, "writeWorkbook");
                    assertThat(writeWorkbook.getInMemory()).isTrue();

                    ExcelReaderBuilder readerBuilder = template.reader();
                    ReadWorkbook readWorkbook =
                            (ReadWorkbook) ReflectionTestUtils.getField(readerBuilder, "readWorkbook");
                    assertThat(readWorkbook.getMandatoryUseInputStream()).isTrue();
                });
    }
}
