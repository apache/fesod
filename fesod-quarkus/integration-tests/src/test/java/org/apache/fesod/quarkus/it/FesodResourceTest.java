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

package org.apache.fesod.quarkus.it;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FesodResourceTest {

    @Test
    void readsFirstSheetInConfiguredBatches() throws IOException {
        RestAssured.given()
                .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(invoiceWorkbook())
                .when()
                .post("/fesod")
                .then()
                .statusCode(200)
                .body(Matchers.is("A:10,B:20|C:30,D:40|E:50"));
    }

    private byte[] invoiceWorkbook() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet invoices = workbook.createSheet("Invoices");
            Row header = invoices.createRow(0);
            header.createCell(0).setCellValue("Invoice");
            header.createCell(1).setCellValue("Amount");
            for (int index = 1; index <= 5; index++) {
                Row row = invoices.createRow(index);
                row.createCell(0).setCellValue(Character.toString('A' + index - 1));
                row.createCell(1).setCellValue(index * 10);
            }
            Sheet other = workbook.cloneSheet(0);
            other.getRow(1).getCell(0).setCellValue("Other sheet");
            workbook.write(output);
            return output.toByteArray();
        }
    }
}
