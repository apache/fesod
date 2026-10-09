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
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
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

    private final Set<String> allowedHosts;
    private final boolean allowPrivateNetwork;
    private final Set<String> allowedPrivateHosts;
    private final List<CidrBlock> allowedPrivateCidrs;
    private final Set<String> allowedSchemes;
    private final int maxRedirects;
    private final int maxImageBytes;

    private UrlImageFetchPolicy(Builder builder) {
        this.allowedHosts = Collections.unmodifiableSet(normalizeAllowedHosts(builder.allowedHosts));
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
        if (normalized.indexOf(':') >= 0 || normalized.indexOf('[') >= 0 || normalized.indexOf(']') >= 0) {
            return normalizeIpv6Host(normalized);
        }
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.isEmpty()) {
            return normalized;
        }
        return IDN.toASCII(normalized);
    }

    private static String normalizeIpv6Host(String host) {
        boolean startsWithBracket = host.startsWith("[");
        boolean endsWithBracket = host.endsWith("]");
        if (startsWithBracket != endsWithBracket) {
            throw new IllegalArgumentException("IPv6 host brackets are invalid");
        }

        String literal = startsWithBracket ? host.substring(1, host.length() - 1) : host;
        if (literal.isEmpty() || literal.indexOf('[') >= 0 || literal.indexOf(']') >= 0 || literal.indexOf('%') >= 0) {
            throw new IllegalArgumentException("IPv6 host is invalid");
        }

        try {
            URI uri = new URI("http://[" + literal + "]/");
            if (uri.getHost() == null) {
                throw new IllegalArgumentException("IPv6 host is invalid");
            }
            InetAddress address = InetAddress.getByName(literal);
            if (!(address instanceof Inet6Address)) {
                throw new IllegalArgumentException("IPv6 host is invalid");
            }
            return address.getHostAddress().toLowerCase(Locale.ROOT);
        } catch (URISyntaxException | UnknownHostException e) {
            throw new IllegalArgumentException("IPv6 host is invalid", e);
        }
    }

    private static Set<String> normalizeAllowedHosts(Collection<String> hosts) {
        Set<String> result = new HashSet<>();
        for (String host : hosts) {
            if (host == null) {
                throw new IllegalArgumentException("Allowed host can not be null");
            }
            String normalized = normalizeAllowedHost(host);
            result.add(normalized);
        }
        return result;
    }

    private static String normalizeAllowedHost(String host) {
        String candidate = host.trim();
        if (candidate.isEmpty()) {
            throw new IllegalArgumentException("Allowed host can not be blank");
        }
        if (candidate.indexOf('*') >= 0) {
            throw new IllegalArgumentException("Allowed host wildcards are not supported");
        }
        if (candidate.indexOf('/') >= 0
                || candidate.indexOf('\\') >= 0
                || candidate.indexOf('@') >= 0
                || candidate.indexOf('?') >= 0
                || candidate.indexOf('#') >= 0) {
            throw new IllegalArgumentException("Allowed host must not contain URL components");
        }
        for (int i = 0; i < candidate.length(); i++) {
            char character = candidate.charAt(i);
            if (Character.isWhitespace(character)
                    || Character.isSpaceChar(character)
                    || Character.isISOControl(character)) {
                throw new IllegalArgumentException("Allowed host must not contain whitespace or control characters");
            }
        }
        try {
            String normalized = normalizeHost(candidate);
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Allowed host can not be blank");
            }
            return normalized;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Allowed host is invalid", e);
        }
    }

    public static final class Builder {
        private Set<String> allowedHosts = Collections.emptySet();
        private boolean allowPrivateNetwork;
        private Set<String> allowedPrivateHosts = Collections.emptySet();
        private List<CidrBlock> allowedPrivateCidrs = Collections.emptyList();
        private SchemePolicy schemePolicy = SchemePolicy.HTTP_OR_HTTPS;
        private int maxRedirects = DEFAULT_MAX_REDIRECTS;
        private int maxImageBytes = DEFAULT_MAX_IMAGE_BYTES;

        private Builder() {}

        /**
         * Allows remote image fetching only for the configured exact hosts.
         *
         * @param allowedHosts exact host names or IP literals without scheme, port, path, or wildcard; URL ports are
         *     not evaluated
         * @return this builder
         */
        public Builder allowedHosts(Collection<String> allowedHosts) {
            if (allowedHosts == null) {
                throw new IllegalArgumentException("Allowed hosts can not be null");
            }
            this.allowedHosts = new HashSet<>(allowedHosts);
            return this;
        }

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
