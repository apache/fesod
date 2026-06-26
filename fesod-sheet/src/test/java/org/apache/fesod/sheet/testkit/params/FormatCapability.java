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

import java.util.function.Predicate;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;

/**
 * A format-level capability that an {@link ExcelFormat} may or may not support, used by
 * {@link ExcelFormatSource#requires()} to filter out unsupported formats declaratively
 * (replacing inline {@code Assumptions.assumeTrue(format.supportsXxx())} guards).
 */
public enum FormatCapability {
    /** Sentinel meaning "no capability requirement". */
    NONE(f -> true),

    /** Format can embed images. */
    IMAGES(ExcelFormat::supportsImages),

    /** Format supports cell styles (fill, font, borders, ...). */
    STYLES(ExcelFormat::supportsStyles),

    /** Format supports write templates. */
    TEMPLATES(ExcelFormat::supportsTemplates),

    /** Format supports workbook encryption. */
    ENCRYPTION(ExcelFormat::supportsEncryption);

    private final Predicate<ExcelFormat> predicate;

    FormatCapability(Predicate<ExcelFormat> predicate) {
        this.predicate = predicate;
    }

    /**
     * @param format the format to test
     * @return {@code true} if {@code format} supports this capability
     */
    public boolean supportedBy(ExcelFormat format) {
        return predicate.test(format);
    }
}
