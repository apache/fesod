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

package org.apache.fesod.sheet.converters.url;

import java.net.IDN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Security policy for fetching images from URL values.
 */
@Getter
@EqualsAndHashCode
public final class UrlImageFetchPolicy {

    public static final int DEFAULT_MAX_REDIRECTS = 3;
    public static final int DEFAULT_MAX_IMAGE_BYTES = 10 * 1024 * 1024;

    private static final UrlImageFetchPolicy DEFAULT = builder().build();

    private final boolean allowPrivateNetwork;
    private final Set<String> allowedPrivateHosts;
    private final List<CidrBlock> allowedPrivateCidrs;
    private final Set<String> allowedSchemes;
    private final int maxRedirects;
    private final int maxImageBytes;

    private UrlImageFetchPolicy(Builder builder) {
        this.allowPrivateNetwork = builder.allowPrivateNetwork;
        this.allowedPrivateHosts = Collections.unmodifiableSet(normalizeHosts(builder.allowedPrivateHosts));
        this.allowedPrivateCidrs = Collections.unmodifiableList(new ArrayList<>(builder.allowedPrivateCidrs));
        this.allowedSchemes = Collections.unmodifiableSet(new HashSet<>(builder.schemePolicy.getSchemes()));
        this.maxRedirects = builder.maxRedirects;
        this.maxImageBytes = builder.maxImageBytes;
    }

    public static UrlImageFetchPolicy defaultPolicy() {
        return DEFAULT;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static Set<String> normalizeHosts(Collection<String> hosts) {
        Set<String> result = new HashSet<>();
        for (String host : hosts) {
            if (host == null) {
                continue;
            }
            String normalized = normalizeHost(host);
            if (!normalized.isEmpty()) {
                result.add(normalized);
            }
        }
        return result;
    }

    static String normalizeHost(String host) {
        String normalized = host.trim().toLowerCase(Locale.ROOT);
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.isEmpty()) {
            return normalized;
        }
        return IDN.toASCII(normalized);
    }

    public static final class Builder {
        private boolean allowPrivateNetwork;
        private Set<String> allowedPrivateHosts = Collections.emptySet();
        private List<CidrBlock> allowedPrivateCidrs = Collections.emptyList();
        private SchemePolicy schemePolicy = SchemePolicy.HTTP_OR_HTTPS;
        private int maxRedirects = DEFAULT_MAX_REDIRECTS;
        private int maxImageBytes = DEFAULT_MAX_IMAGE_BYTES;

        private Builder() {}

        public Builder allowPrivateNetwork(boolean allowPrivateNetwork) {
            this.allowPrivateNetwork = allowPrivateNetwork;
            return this;
        }

        public Builder allowedPrivateHosts(Collection<String> allowedPrivateHosts) {
            if (allowedPrivateHosts == null) {
                this.allowedPrivateHosts = Collections.emptySet();
            } else {
                this.allowedPrivateHosts = new HashSet<>(allowedPrivateHosts);
            }
            return this;
        }

        public Builder allowedPrivateCidrs(Collection<CidrBlock> allowedPrivateCidrs) {
            if (allowedPrivateCidrs == null) {
                this.allowedPrivateCidrs = Collections.emptyList();
            } else {
                this.allowedPrivateCidrs = new ArrayList<>(allowedPrivateCidrs);
            }
            return this;
        }

        public Builder allowedSchemes(SchemePolicy schemePolicy) {
            if (schemePolicy == null) {
                throw new IllegalArgumentException("Scheme policy can not be null");
            }
            this.schemePolicy = schemePolicy;
            return this;
        }

        public Builder maxRedirects(int maxRedirects) {
            this.maxRedirects = maxRedirects;
            return this;
        }

        public Builder maxImageBytes(int maxImageBytes) {
            this.maxImageBytes = maxImageBytes;
            return this;
        }

        public UrlImageFetchPolicy build() {
            if (maxRedirects < 0) {
                throw new IllegalArgumentException("Max redirects can not be negative");
            }
            if (maxImageBytes <= 0) {
                throw new IllegalArgumentException("Max image bytes must be positive");
            }
            return new UrlImageFetchPolicy(this);
        }
    }
}
