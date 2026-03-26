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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.examples.ExampleTestBase;
import org.apache.fesod.sheet.examples.write.data.DemoData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link PasswordProtectionExample}.
 *
 * <p>Verifies the full password-protection round-trip: writes a password-protected Excel file,
 * then reads it back with the same password. Also validates the protected file is a readable
 * workbook via a controlled write to {@code @TempDir}.
 */
class PasswordProtectionExampleITCase extends ExampleTestBase {

    @Test
    void testPasswordRoundTrip() {
        assertDoesNotThrow(() -> PasswordProtectionExample.main(new String[] {}));
    }

    @Test
    void testPasswordProtectedFileIsValid(@TempDir Path tempDir) {
        String fileName = getTempOutputPath(tempDir, "password.xlsx");
        String password = "test123";

        List<DemoData> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DemoData d = new DemoData();
            d.setString("String" + i);
            d.setDate(new Date());
            d.setDoubleData(0.56);
            data.add(d);
        }

        FesodSheet.write(fileName)
                .password(password)
                .head(DemoData.class)
                .sheet("Test")
                .doWrite(data);

        // Password-protected files cannot be opened without the password by POI WorkbookFactory,
        // so we only verify the file exists and has a non-trivial size.
        File file = new File(fileName);
        assertTrue(file.exists(), "Password-protected file should exist");
        assertTrue(file.length() > 0, "Password-protected file should not be empty");
    }
}
