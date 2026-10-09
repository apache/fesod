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

package org.apache.fesod.sheet.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.apache.fesod.sheet.converters.url.SchemePolicy;
import org.apache.fesod.sheet.converters.url.UrlImageFetchPolicy;
import org.apache.fesod.sheet.testkit.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link UrlImageFetchPolicy}.
 */
@Tag(Tags.UNIT)
class UrlImageFetchPolicyTest {

    @Test
    void test_defaultPolicyDisablesRemoteFetchingAndSupportsHttpConfiguration() {
        UrlImageFetchPolicy policy = UrlImageFetchPolicy.defaultPolicy();

        Assertions.assertTrue(policy.getAllowedHosts().isEmpty());
        Assertions.assertTrue(policy.getAllowedSchemes().containsAll(SchemePolicy.HTTP_OR_HTTPS.getSchemes()));
    }

    @Test
    void test_normalizeAllowedHostsForExactMatching() {
        UrlImageFetchPolicy policy = UrlImageFetchPolicy.builder()
                .allowedHosts(Arrays.asList(
                        " Images.Example.COM. ",
                        "images.example.com",
                        "BÜCHER.example",
                        "IMAGE_SERVICE.internal",
                        "[2001:db8::1]",
                        "2001:0DB8:0:0:0:0:0:1",
                        "::1"))
                .build();

        Assertions.assertEquals(
                new HashSet<>(Arrays.asList(
                        "images.example.com",
                        "xn--bcher-kva.example",
                        "image_service.internal",
                        "2001:db8:0:0:0:0:0:1",
                        "0:0:0:0:0:0:0:1")),
                policy.getAllowedHosts());
        Assertions.assertFalse(policy.getAllowedHosts().contains("cdn.images.example.com"));
        Assertions.assertFalse(policy.getAllowedHosts().contains("images.example.com.attacker.test"));
    }

    @Test
    void test_rejectNullAllowedHosts() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> UrlImageFetchPolicy.builder()
                .allowedHosts(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> UrlImageFetchPolicy.builder()
                .allowedHosts(Collections.singleton(null))
                .build());
    }

    @Test
    void test_rejectInvalidAllowedHosts() {
        for (String host : Arrays.asList(
                "",
                "*.example.com",
                "https://images.example.com",
                "user@images.example.com",
                "images.example.com:80",
                "[::1]:80",
                "image service.internal",
                "image\tservice.internal")) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> UrlImageFetchPolicy.builder()
                    .allowedHosts(Collections.singleton(host))
                    .build());
        }
    }
}
