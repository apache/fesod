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
 * Tests {@link StaticMessageSource}.
 */
@Tag(Tags.UNIT)
class StaticMessageSourceTest {

    private static final String GREETING = "msg.greeting";
    private static final String NAME = "msg.name";
    private static final String PRODUCT = "msg.product";
    private static final String CIRCULAR_A = "msg.circular.a";
    private static final String CIRCULAR_B = "msg.circular.b";

    private StaticMessageSource source;

    @BeforeEach
    void setUp() {
        Locale.setDefault(Locale.ENGLISH);

        source = new StaticMessageSource();
    }

    @Test
    void shouldResolveMessageRegisteredForRequestedLocale() {
        source.addMessage(GREETING, "Hello!", Locale.ENGLISH);

        Assertions.assertEquals("Hello!", source.getMessage(GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldResolveDefaultLocaleMessageWhenLocaleOmitted() {
        source.addMessage(GREETING, "Hello!");

        Assertions.assertEquals("Hello!", source.getMessage(GREETING, null));
    }

    @Test
    void shouldRejectCodeRegisteredOnlyForAnotherLocale() {
        source.addMessage(GREETING, "Ni hao!", Locale.SIMPLIFIED_CHINESE);

        Assertions.assertThrows(NoSuchMessageException.class, () -> source.getMessage(GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldReturnCodeItselfWhenMissingAndCodeAsDefaultEnabled() {
        source.setUseCodeAsDefaultMessage(true);

        Assertions.assertEquals(GREETING, source.getMessage(GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldResolveNestedMessageReferences() {
        source.addMessage(GREETING, "Hello {msg.name}!", Locale.ENGLISH);
        source.addMessage(NAME, "{msg.product} user", Locale.ENGLISH);
        source.addMessage(PRODUCT, "fesod", Locale.ENGLISH);

        Assertions.assertEquals("Hello fesod user!", source.getMessage(GREETING, Locale.ENGLISH));
    }

    @Test
    void shouldInterpolateTemplateWithMessageReferences() {
        source.addMessage(NAME, "fesod", Locale.ENGLISH);

        Assertions.assertEquals("Hello fesod!", source.interpolate("Hello {" + NAME + "}!", Locale.ENGLISH));
    }

    @Test
    void shouldFailOnCircularMessageReferences() {
        source.addMessage(CIRCULAR_A, "{" + CIRCULAR_B + "}", Locale.ENGLISH);
        source.addMessage(CIRCULAR_B, "{" + CIRCULAR_A + "}", Locale.ENGLISH);

        Assertions.assertThrows(IllegalArgumentException.class, () -> source.getMessage(CIRCULAR_A, Locale.ENGLISH));
    }
}
