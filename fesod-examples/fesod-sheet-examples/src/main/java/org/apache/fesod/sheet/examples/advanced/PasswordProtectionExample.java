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

package org.apache.fesod.sheet.examples.advanced;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.examples.util.ExampleFileUtil;
import org.apache.fesod.sheet.examples.write.data.DemoData;
import org.apache.fesod.sheet.read.listener.ReadListener;

/**
 * Example demonstrating how to write and read password-protected Excel files.
 */
@Slf4j
public class PasswordProtectionExample {

    public static void main(String[] args) {
        String fileName = ExampleFileUtil.getTempPath("password" + System.currentTimeMillis() + ".xlsx");
        String password = "password123";

        log.info("Starting password protection example...");
        passwordWrite(fileName, password);
        passwordRead(fileName, password);
    }

    /**
     * Write a password-protected Excel file.
     */
    public static void passwordWrite(String fileName, String password) {
        FesodSheet.write(fileName)
                .password(password)
                .head(DemoData.class)
                .sheet("PasswordSheet")
                .doWrite(data());
        log.info("Successfully wrote password-protected file: {}", fileName);
    }

    /**
     * Read a password-protected Excel file.
     */
    public static void passwordRead(String fileName, String password) {
        FesodSheet.read(fileName, DemoData.class, new ReadListener<DemoData>() {
                    @Override
                    public void invoke(DemoData data, AnalysisContext context) {
                        log.info("Read password-protected data: {}", data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        log.info("Password-protected file read completed");
                    }
                })
                .password(password)
                .sheet()
                .doRead();
    }

    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("String" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
}
