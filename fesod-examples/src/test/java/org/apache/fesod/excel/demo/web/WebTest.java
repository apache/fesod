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

package org.apache.fesod.excel.demo.web;

import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Web read and write examples
 *
 *
 **/
@Controller
public class WebTest {

    @Autowired
    private UploadDAO uploadDAO;

    /**
     * File download (returns an Excel with partial data if failed)
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link DownloadData}
     * <p>
     * 2. Set the return parameters
     * <p>
     * 3. Write directly. Note that the OutputStream will be automatically closed when finish is called. It's fine to close the stream outside as well
     */
    @GetMapping("download")
    public void download(HttpServletResponse response) throws IOException {
        // Note: Some students reported that using Swagger causes various issues, please use browser directly or use
        // Postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // Here URLEncoder.encode can prevent Chinese character encoding issues, which is unrelated to FastExcel
        String fileName = URLEncoder.encode("test", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        FastExcel.write(response.getOutputStream(), DownloadData.class)
                .sheet("Template")
                .doWrite(data());
    }

    /**
     * File download that returns JSON when failed (by default, returns an Excel with partial data when failed)
     *
     * @since 2.1.1
     */
    @GetMapping("downloadFailedUsingJson")
    public void downloadFailedUsingJson(HttpServletResponse response) throws IOException {
        // Note: Some students reported that using Swagger causes various issues, please use browser directly or use
        // Postman
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // Here URLEncoder.encode can prevent Chinese character encoding issues, which is unrelated to FastExcel
            String fileName = URLEncoder.encode("test", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // Here we need to set not to close the stream
            FastExcel.write(response.getOutputStream(), DownloadData.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Template")
                    .doWrite(data());
        } catch (Exception e) {
            // Reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "Failed to download file: " + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    /**
     * File upload
     * <p>
     * 1. Create the entity object corresponding to Excel. Refer to {@link UploadData}
     * <p>
     * 2. Since Excel is read row by row by default, you need to create a row-by-row callback listener for Excel. Refer to {@link UploadDataListener}
     * <p>
     * 3. Read directly
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        FastExcel.read(file.getInputStream(), UploadData.class, new UploadDataListener(uploadDAO))
                .sheet()
                .doRead();
        return "success";
    }

    private List<DownloadData> data() {
        List<DownloadData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DownloadData data = new DownloadData();
            data.setString("String" + 0);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
