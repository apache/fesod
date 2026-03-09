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

package org.apache.fesod.sheet.examples.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.fesod.sheet.FesodSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Spring MVC controller demonstrating Excel download and upload in a web application.
 *
 * <h2>Scenario</h2>
 * <p>A typical enterprise application where users can:
 * <ul>
 *   <li><b>Download</b> — Export data as an Excel file via HTTP GET (browser download).</li>
 *   <li><b>Upload</b> — Import data from an uploaded Excel file via HTTP POST (multipart form).</li>
 * </ul>
 *
 * <h2>Download Flow ({@code GET /download})</h2>
 * <pre>
 * Browser GET /download
 *     │
 *     ├─ Set Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 *     ├─ Set Content-Disposition: attachment;filename=test.xlsx
 *     └─ FesodSheet.write(response.getOutputStream(), ...).sheet().doWrite(data)
 *         └─ Excel bytes streamed directly to HTTP response (no temp file)
 * </pre>
 *
 * <h2>Upload Flow ({@code POST /upload})</h2>
 * <pre>
 * Browser POST /upload (multipart/form-data)
 *     │
 *     └─ FesodSheet.read(file.getInputStream(), UploadData.class, listener).sheet().doRead()
 *         └─ UploadDataListener processes rows in batches → saves to database
 * </pre>
 *
 * <h2>Key Implementation Details</h2>
 * <ul>
 *   <li><b>Streaming download:</b> Writes directly to {@code response.getOutputStream()},
 *       avoiding temporary files and enabling large exports.</li>
 *   <li><b>Filename encoding:</b> Uses {@code URLEncoder} with UTF-8 for Chinese/special
 *       characters in the filename.</li>
 *   <li><b>Upload listener:</b> Creates a new {@link UploadDataListener} per request with
 *       the injected {@link UploadDAO} (Spring-managed). The listener itself is NOT a bean.</li>
 * </ul>
 *
 * <h2>curl Test Commands</h2>
 * <pre>{@code
 * # Download
 * curl -o test.xlsx http://localhost:8080/download
 *
 * # Upload
 * curl -F "file=@test.xlsx" http://localhost:8080/upload
 * }</pre>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.write.BasicWriteExample} — Write to file.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.read.BasicReadExample} — Read from file.</li>
 * </ul>
 *
 * @see UploadDataListener
 * @see DownloadData
 */
@Controller
public class WebExampleController {

    @Autowired
    private UploadDAO uploadDAO;

    /**
     * Downloads an Excel file as an HTTP response.
     *
     * <p>Writes directly to the response output stream — no temporary file is created.
     * The response headers are configured for browser download with a UTF-8 encoded filename.</p>
     *
     * <p><b>Important:</b> Set {@code Content-Type} and {@code Content-Disposition} headers
     * BEFORE writing to the output stream. Once bytes are written, headers cannot be modified.</p>
     *
     * @param response the HTTP servlet response
     * @throws IOException if writing to the output stream fails
     */
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("test", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        FesodSheet.write(response.getOutputStream(), DownloadData.class)
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * Uploads and parses an Excel file from a multipart form submission.
     *
     * <p>Uses {@code file.getInputStream()} to read the uploaded file directly from
     * the multipart data, avoiding extra disk I/O. The {@link UploadDataListener}
     * processes rows in batches and persists via the injected {@link UploadDAO}.</p>
     *
     * @param file the uploaded multipart file
     * @return "success" on completion
     * @throws IOException if reading the input stream fails
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        FesodSheet.read(file.getInputStream(), UploadData.class, new UploadDataListener(uploadDAO))
                .sheet()
                .doRead();
        return "success";
    }

    private List<DownloadData> data() {
        List<DownloadData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DownloadData data = new DownloadData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
