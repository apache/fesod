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
 * Demonstrates reading and writing password-protected Excel files.
 *
 * <h2>Scenario</h2>
 * <p>You need to protect sensitive data (financial reports, personal information)
 * with Excel's built-in password encryption. Fesod supports both writing encrypted
 * files and reading them with the correct password.</p>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code .password("xxx")} — Available on both read and write builders.
 *       For write, it encrypts the output file. For read, it decrypts the input file.</li>
 *   <li>Uses Excel's native encryption (AES for .xlsx), compatible with all Excel versions.</li>
 *   <li>Without the correct password, reading will throw an
 *       {@code EncryptedDocumentException}.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * // Write with password
 * FesodSheet.write(fileName).password("secret").head(MyData.class).sheet().doWrite(data);
 *
 * // Read with password
 * FesodSheet.read(fileName, MyData.class, listener).password("secret").sheet().doRead();
 * }</pre>
 *
 * <h2>Security Notes</h2>
 * <ul>
 *   <li>Excel password protection encrypts the file content, making it unreadable
 *       without the password.</li>
 *   <li>In production, avoid hardcoding passwords — use configuration or secrets management.</li>
 * </ul>
 *
 * <h2>Related Examples</h2>
 * <ul>
 *   <li>{@link org.apache.fesod.sheet.examples.write.BasicWriteExample} — Write without encryption.</li>
 *   <li>{@link org.apache.fesod.sheet.examples.read.BasicReadExample} — Read without encryption.</li>
 * </ul>
 *
 * @see FesodSheet#write(String)
 * @see FesodSheet#read(String, Class, org.apache.fesod.sheet.read.listener.ReadListener)
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
     * Writes an Excel file encrypted with the given password.
     *
     * <p>The output file can only be opened in Excel (or read by Fesod) with
     * the matching password. The encryption is applied at the file level.</p>
     *
     * @param fileName output file path
     * @param password encryption password
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
     * Reads a password-protected Excel file.
     *
     * <p>The password must match the one used during write. If incorrect,
     * an {@code EncryptedDocumentException} will be thrown.</p>
     *
     * @param fileName input file path
     * @param password decryption password
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
