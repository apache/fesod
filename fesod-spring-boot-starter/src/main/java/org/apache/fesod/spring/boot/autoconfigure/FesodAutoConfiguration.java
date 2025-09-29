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

import java.util.List;
import java.util.stream.Collectors;
import org.apache.fesod.excel.FastExcelFactory;
import org.apache.fesod.spring.boot.FesodTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(FastExcelFactory.class)
@EnableConfigurationProperties(FesodProperties.class)
public class FesodAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FesodTemplate fesodTemplate(
            FesodProperties properties,
            ObjectProvider<FesodWriterBuilderCustomizer> writerCustomizers,
            ObjectProvider<FesodReaderBuilderCustomizer> readerCustomizers) {
        List<FesodWriterBuilderCustomizer> writer =
                writerCustomizers.orderedStream().collect(Collectors.toList());
        List<FesodReaderBuilderCustomizer> reader =
                readerCustomizers.orderedStream().collect(Collectors.toList());
        return new FesodTemplate(properties, writer, reader);
    }
}
