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

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.StringJoiner;
import org.apache.fesod.quarkus.runtime.SpreadsheetReader;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Path("/fesod")
public class FesodResource {
    @Inject
    SpreadsheetReader reader;

    @POST
    @Consumes("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Produces(MediaType.TEXT_PLAIN)
    public String readInvoices(InputStream workbook) {
        StringJoiner batches = new StringJoiner("|");
        reader.readFirstSheet(workbook, InvoiceRow.class, rows -> {
            StringJoiner batch = new StringJoiner(",");
            for (InvoiceRow row : rows) {
                batch.add(row.getId() + ":" + row.getAmount());
            }
            batches.add(batch.toString());
        });
        return batches.toString();
    }

    public static class InvoiceRow {
        @ExcelProperty("Invoice")
        private String id;

        @ExcelProperty("Amount")
        private Integer amount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }
}
