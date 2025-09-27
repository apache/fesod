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

package org.apache.fesod.excel.temp.read;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.demo.read.Sample;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

public class WebStreamReadTest {

    private ClientAndServer mockServer;

    @BeforeEach
    public void startServer() {
        this.mockServer = ClientAndServer.startClientAndServer();
    }

    @Test
    public void urlLoadInputStreamTest() throws IOException {
        mockServer
                .when(request().withMethod("GET").withPath("/sample.xlsx"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .withBody(Files.readAllBytes(Paths.get("src/test/resources/web/io.xlsx"))));
        URL url = new URL("http://localhost:" + mockServer.getPort() + "/sample.xlsx");
        InputStream is = url.openStream();
        List<String> body = new LinkedList<>();
        FastExcel.read(is, Sample.class, new ReadListener<Sample>() {
                    @Override
                    public void invoke(Sample data, AnalysisContext context) {
                        body.add(data.getHeader());
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();
        Assertions.assertTrue(body.contains("body"));
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }
}
