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

package org.apache.fesod.sheet.testkit.params;

import java.util.Arrays;
import java.util.stream.Stream;
import org.apache.fesod.sheet.testkit.enums.ApiMode;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Backing {@link ArgumentsProvider} for {@link ExcelFormatSource}.
 *
 * <p>This is an internal implementation detail, referenced indirectly via the
 * {@link ArgumentsSource} meta-annotation on {@link ExcelFormatSource}; not intended
 * for direct use.
 *
 * @see ExcelFormatSource
 */
@API(status = API.Status.INTERNAL)
public class ExcelFormatArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        ExcelFormatSource source = AnnotationSupport.findAnnotation(context.getElement(), ExcelFormatSource.class)
                .orElseThrow(
                        () -> new IllegalStateException("ExcelFormatArgumentsProvider requires @ExcelFormatSource"));

        ExcelFormat[] formats = source.value() == FormatScope.BINARY
                ? new ExcelFormat[] {ExcelFormat.XLSX, ExcelFormat.XLS}
                : ExcelFormat.values();

        // Drop any format that lacks a required capability, mirroring what
        // Assumptions.assumeTrue(format.supportsXxx()) did inline.
        FormatCapability[] required = source.requires();
        Stream<ExcelFormat> filtered = required.length == 0
                ? Stream.of(formats)
                : Stream.of(formats).filter(f -> Arrays.stream(required).allMatch(c -> c.supportedBy(f)));

        if (!source.withApiMode()) {
            return filtered.map(Arguments::of);
        }
        Stream.Builder<Arguments> builder = Stream.builder();
        filtered.forEach(format -> {
            for (ApiMode mode : ApiMode.values()) {
                builder.add(Arguments.of(format, mode));
            }
        });
        return builder.build();
    }
}
