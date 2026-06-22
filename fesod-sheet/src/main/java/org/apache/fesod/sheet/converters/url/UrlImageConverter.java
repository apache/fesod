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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.apache.fesod.sheet.converters.Converter;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.ImageData;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.apache.fesod.sheet.metadata.property.ExcelContentProperty;
import org.apache.fesod.sheet.util.FileTypeUtils;

/**
 * Url and image converter
 *
 *
 */
public class UrlImageConverter implements Converter<URL> {
    public static int urlConnectTimeout = 1000;
    public static int urlReadTimeout = 5000;

    private static volatile UrlImageFetchPolicy fetchPolicy = UrlImageFetchPolicy.defaultPolicy();

    public static UrlImageFetchPolicy getFetchPolicy() {
        return fetchPolicy;
    }

    public static void setFetchPolicy(UrlImageFetchPolicy fetchPolicy) {
        if (fetchPolicy == null) {
            throw new IllegalArgumentException("Fetch policy can not be null");
        }
        UrlImageConverter.fetchPolicy = fetchPolicy;
    }

    public static void resetFetchPolicy() {
        fetchPolicy = UrlImageFetchPolicy.defaultPolicy();
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return URL.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            URL value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws IOException {
        byte[] bytes = readImage(value, fetchPolicy);
        ImageData.ImageType imageType = FileTypeUtils.getImageType(bytes);
        if (imageType == null) {
            throw new IOException("URL image data is not a supported image type");
        }
        return new WriteCellData<>(bytes);
    }

    private byte[] readImage(URL value, UrlImageFetchPolicy policy) throws IOException {
        URL currentUrl = value;
        for (int redirectCount = 0; redirectCount <= policy.getMaxRedirects(); redirectCount++) {
            validateUrl(currentUrl, policy);
            HttpURLConnection connection = openConnection(currentUrl);
            try {
                int responseCode = connection.getResponseCode();
                if (isRedirect(responseCode)) {
                    if (redirectCount == policy.getMaxRedirects()) {
                        throw new IOException("URL image request exceeded redirect limit");
                    }
                    currentUrl = resolveRedirect(currentUrl, connection.getHeaderField("Location"));
                    continue;
                }
                if (responseCode < HttpURLConnection.HTTP_OK || responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                    throw new IOException("URL image request failed with HTTP status " + responseCode);
                }
                int contentLength = connection.getContentLength();
                if (contentLength > policy.getMaxImageBytes()) {
                    throw new IOException("URL image data exceeds maximum size");
                }
                try (InputStream inputStream = connection.getInputStream()) {
                    return readLimited(inputStream, policy.getMaxImageBytes());
                }
            } finally {
                connection.disconnect();
            }
        }
        throw new IOException("URL image request exceeded redirect limit");
    }

    private HttpURLConnection openConnection(URL value) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) value.openConnection();
        connection.setConnectTimeout(urlConnectTimeout);
        connection.setReadTimeout(urlReadTimeout);
        connection.setInstanceFollowRedirects(false);
        return connection;
    }

    private void validateUrl(URL value, UrlImageFetchPolicy policy) throws IOException {
        String protocol = value.getProtocol();
        if (protocol == null || !policy.getAllowedSchemes().contains(protocol.toLowerCase(Locale.ROOT))) {
            throw new IOException("URL image protocol is not allowed");
        }
        String host = value.getHost();
        if (host == null || host.trim().isEmpty()) {
            throw new IOException("URL image host is required");
        }

        String normalizedHost;
        try {
            normalizedHost = UrlImageFetchPolicy.normalizeHost(host);
        } catch (IllegalArgumentException e) {
            throw new IOException("URL image host is invalid", e);
        }

        InetAddress[] addresses = InetAddress.getAllByName(normalizedHost);
        if (addresses.length == 0) {
            throw new IOException("URL image host can not be resolved");
        }
        for (InetAddress address : addresses) {
            if (isRestrictedAddress(address) && !isAllowedPrivateAddress(normalizedHost, address, policy)) {
                throw new IOException("URL image host resolves to a restricted address");
            }
        }
    }

    private boolean isAllowedPrivateAddress(String normalizedHost, InetAddress address, UrlImageFetchPolicy policy) {
        if (!policy.isAllowPrivateNetwork()) {
            return false;
        }
        if (policy.getAllowedPrivateHosts().contains(normalizedHost)) {
            return true;
        }
        for (CidrBlock cidrBlock : policy.getAllowedPrivateCidrs()) {
            if (cidrBlock.contains(address)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRestrictedAddress(InetAddress address) {
        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()
                || isRestrictedIpv4Address(address)
                || isRestrictedIpv6Address(address);
    }

    private boolean isRestrictedIpv4Address(InetAddress address) {
        if (!(address instanceof Inet4Address)) {
            return false;
        }
        byte[] bytes = address.getAddress();
        int first = bytes[0] & 0xFF;
        int second = bytes[1] & 0xFF;
        return first == 0
                || first == 10
                || first == 127
                || (first == 100 && second >= 64 && second <= 127)
                || (first == 169 && second == 254)
                || (first == 172 && second >= 16 && second <= 31)
                || (first == 192 && second == 168)
                || first >= 224;
    }

    private boolean isRestrictedIpv6Address(InetAddress address) {
        if (!(address instanceof Inet6Address)) {
            return false;
        }
        byte[] bytes = address.getAddress();
        int first = bytes[0] & 0xFF;
        int second = bytes[1] & 0xFF;
        return first == 0
                || (first == 0xFC || first == 0xFD)
                || (first == 0xFE && (second & 0xC0) == 0x80)
                || first == 0xFF;
    }

    private boolean isRedirect(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                || responseCode == 307
                || responseCode == 308;
    }

    private URL resolveRedirect(URL currentUrl, String location) throws IOException {
        if (location == null || location.trim().isEmpty()) {
            throw new IOException("URL image redirect location is missing");
        }
        try {
            return new URL(currentUrl, location);
        } catch (MalformedURLException e) {
            throw new IOException("URL image redirect location is invalid", e);
        }
    }

    private byte[] readLimited(InputStream inputStream, int maxBytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(Math.min(maxBytes, 8192));
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            total += read;
            if (total > maxBytes) {
                throw new IOException("URL image data exceeds maximum size");
            }
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toByteArray();
    }
}
