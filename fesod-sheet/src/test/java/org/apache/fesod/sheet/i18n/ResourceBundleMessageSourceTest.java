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

package org.apache.fesod.sheet.i18n;

import java.util.Locale;
import org.apache.fesod.sheet.exception.NoSuchMessageException;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ResourceBundleMessageSource}.
 */
@Tag(Tags.UNIT)
class ResourceBundleMessageSourceTest {

    private static final String BASENAME = "i18n.messages";
    private static final String EXTRA_BASENAME = "i18n.extra";
    private static final String MISSING_BASENAME = "i18n.does-not-exist";

    private static final String GREETING = "fesod.msg.greeting";
    private static final String TARGET = "fesod.msg.target";
    private static final String PLACEHOLDER_GREETING = "fesod.msg.placeholder.greeting";
    private static final String CIRCULAR_A = "fesod.msg.circular.a";
    private static final String ESCAPED = "fesod.msg.escaped";
    private static final String EXTRA_ONLY = "fesod.msg.extra.only";
    private static final String UNKNOWN_CODE = "fesod.msg.unknown";

    private ResourceBundleMessageSource source;

    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.ENGLISH);

        source = new ResourceBundleMessageSource(BASENAME);
    }

    @Test
    void shouldResolveMessageFromBundleMatchingRequestedLocale() {
        Assertions.assertEquals("Hi", source.getMessage(GREETING, Locale.US));
        Assertions.assertEquals("你好", source.getMessage(GREETING, Locale.SIMPLIFIED_CHINESE));
    }

    @Test
    void shouldFallBackToRootBundleWhenNoVariantMatchesLocale() {
        Assertions.assertEquals("Hello", source.getMessage(GREETING, Locale.GERMAN));
    }

    @Test
    void shouldInterpolateReferencedMessagesFromBundle() {
        Assertions.assertEquals("Hello World", source.getMessage(PLACEHOLDER_GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldKeepEscapedPlaceholderLiteral() {
        Assertions.assertEquals("Hello {" + TARGET + "}", source.getMessage(ESCAPED, Locale.ENGLISH));
    }

    @Test
    void shouldFailOnCircularMessageReferences() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> source.getMessage(CIRCULAR_A, Locale.ENGLISH));
    }

    @Test
    void shouldResolveMessagesFromConfiguredBaseName() {
        ResourceBundleMessageSource extraSource = new ResourceBundleMessageSource(EXTRA_BASENAME);

        Assertions.assertEquals("Extra", extraSource.getMessage(GREETING, Locale.ENGLISH));
        Assertions.assertEquals("OnlyInExtra", extraSource.getMessage(EXTRA_ONLY, Locale.ENGLISH));
    }

    @Test
    void shouldThrowNoSuchMessageExceptionWhenBundleIsMissing() {
        ResourceBundleMessageSource missingSource = new ResourceBundleMessageSource(MISSING_BASENAME);

        Assertions.assertThrows(NoSuchMessageException.class, () -> missingSource.getMessage(GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldReturnCodeItselfWhenMissingAndCodeAsDefaultEnabled() {
        ResourceBundleMessageSource defaultingSource = new ResourceBundleMessageSource(BASENAME, true);

        Assertions.assertEquals(UNKNOWN_CODE, defaultingSource.getMessage(UNKNOWN_CODE, Locale.ENGLISH));
    }
}
