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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.fesod.sheet.converters.url.CidrBlock;
import org.apache.fesod.sheet.converters.url.SchemePolicy;
import org.apache.fesod.sheet.converters.url.UrlImageConverter;
import org.apache.fesod.sheet.converters.url.UrlImageFetchPolicy;
import org.apache.fesod.sheet.metadata.GlobalConfiguration;
import org.apache.fesod.sheet.metadata.data.WriteCellData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link UrlImageConverter}.
 */
class UrlImageConverterTest {

    private static final byte[] PNG_BYTES =
            new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D};

    private final UrlImageConverter converter = new UrlImageConverter();
    private HttpServer server;
    private AtomicInteger requestCount;

    @BeforeEach
    void beforeEach() {
        UrlImageConverter.resetFetchPolicy();
        requestCount = new AtomicInteger();
    }

    @AfterEach
    void afterEach() {
        UrlImageConverter.resetFetchPolicy();
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void test_rejectFileProtocol() {
        IOException exception =
                Assertions.assertThrows(IOException.class, () -> convert(new URL("file:///etc/passwd")));

        Assertions.assertTrue(exception.getMessage().contains("protocol"));
    }

    @Test
    void test_rejectNullSchemePolicy() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> UrlImageFetchPolicy.builder().allowedSchemes(null).build());
    }

    @Test
    void test_httpsOnlyPolicyRejectsHttpUrl() throws Exception {
        URL url = startServer(HttpStatus.OK, PNG_BYTES, "image/png");
        UrlImageConverter.setFetchPolicy(
                UrlImageFetchPolicy.builder().allowedSchemes(SchemePolicy.HTTPS).build());

        IOException exception = Assertions.assertThrows(IOException.class, () -> convert(url));

        Assertions.assertTrue(exception.getMessage().contains("protocol"));
        Assertions.assertEquals(0, requestCount.get());
    }

    @Test
    void test_rejectLoopbackByDefault() throws Exception {
        URL url = startServer(HttpStatus.OK, PNG_BYTES, "image/png");

        IOException exception = Assertions.assertThrows(IOException.class, () -> convert(url));

        Assertions.assertTrue(exception.getMessage().contains("restricted address"));
        Assertions.assertEquals(0, requestCount.get());
    }

    @Test
    void test_allowPrivateHostWhenExplicitlyAllowlisted() throws Exception {
        URL url = startServer(HttpStatus.OK, PNG_BYTES, "image/png");
        UrlImageConverter.setFetchPolicy(UrlImageFetchPolicy.builder()
                .allowPrivateNetwork(true)
                .allowedPrivateHosts(Collections.singleton("127.0.0.1"))
                .build());

        WriteCellData<?> cellData = convert(url);

        Assertions.assertArrayEquals(
                PNG_BYTES, cellData.getImageDataList().get(0).getImage());
        Assertions.assertEquals(1, requestCount.get());
    }

    @Test
    void test_allowPrivateCidrWhenExplicitlyAllowlisted() throws Exception {
        URL url = startServer(HttpStatus.OK, PNG_BYTES, "image/png");
        UrlImageConverter.setFetchPolicy(UrlImageFetchPolicy.builder()
                .allowPrivateNetwork(true)
                .allowedPrivateCidrs(Collections.singleton(CidrBlock.parse("127.0.0.0/8")))
                .build());

        WriteCellData<?> cellData = convert(url);

        Assertions.assertArrayEquals(
                PNG_BYTES, cellData.getImageDataList().get(0).getImage());
    }

    @Test
    void test_rejectNonImageResponse() throws Exception {
        URL url = startServer(HttpStatus.OK, "root:x:0:0".getBytes("UTF-8"), "text/plain");
        UrlImageConverter.setFetchPolicy(allowLoopbackPolicy());

        IOException exception = Assertions.assertThrows(IOException.class, () -> convert(url));

        Assertions.assertTrue(exception.getMessage().contains("supported image type"));
    }

    @Test
    void test_rejectRedirectToNonAllowlistedPrivateHost() throws Exception {
        URL url = startRedirectServer("http://localhost:8080/image.png");
        UrlImageConverter.setFetchPolicy(allowLoopbackPolicy());

        IOException exception = Assertions.assertThrows(IOException.class, () -> convert(url));

        Assertions.assertTrue(exception.getMessage().contains("restricted address"));
        Assertions.assertEquals(1, requestCount.get());
    }

    @Test
    void test_rejectImageLargerThanPolicyLimit() throws Exception {
        byte[] body = Arrays.copyOf(PNG_BYTES, PNG_BYTES.length + 20);
        URL url = startServer(HttpStatus.OK, body, "image/png");
        UrlImageConverter.setFetchPolicy(UrlImageFetchPolicy.builder()
                .allowPrivateNetwork(true)
                .allowedPrivateHosts(Collections.singleton("127.0.0.1"))
                .maxImageBytes(PNG_BYTES.length)
                .build());

        IOException exception = Assertions.assertThrows(IOException.class, () -> convert(url));

        Assertions.assertTrue(exception.getMessage().contains("maximum size"));
    }

    private UrlImageFetchPolicy allowLoopbackPolicy() {
        return UrlImageFetchPolicy.builder()
                .allowPrivateNetwork(true)
                .allowedPrivateHosts(Collections.singleton("127.0.0.1"))
                .build();
    }

    private WriteCellData<?> convert(URL url) throws IOException {
        return converter.convertToExcelData(url, null, new GlobalConfiguration());
    }

    private URL startServer(int status, byte[] body, String contentType) throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            requestCount.incrementAndGet();
            send(exchange, status, body, contentType);
        });
        server.start();
        return serverUrl("/");
    }

    private URL startRedirectServer(String location) throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/", exchange -> {
            requestCount.incrementAndGet();
            exchange.getResponseHeaders().set("Location", location);
            exchange.sendResponseHeaders(HttpStatus.FOUND, -1);
            exchange.close();
        });
        server.start();
        return serverUrl("/");
    }

    private URL serverUrl(String path) throws IOException {
        return new URL("http://127.0.0.1:" + server.getAddress().getPort() + path);
    }

    private void send(HttpExchange exchange, int status, byte[] body, String contentType) throws IOException {
        if (contentType != null) {
            exchange.getResponseHeaders().set("Content-Type", contentType);
        }
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
        exchange.close();
    }

    private static final class HttpStatus {
        private static final int OK = 200;
        private static final int FOUND = 302;

        private HttpStatus() {}
    }
}
