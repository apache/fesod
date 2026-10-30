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

package org.apache.fesod.sheet.write.builder;

import java.util.function.Supplier;
import org.apache.fesod.sheet.ExcelWriter;

/**
 * Releases writer resources when a terminal builder's data supplier fails.
 */
final class SupplierWriteSupport {
    private SupplierWriteSupport() {}

    static <T> T get(ExcelWriter excelWriter, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (RuntimeException | Error failure) {
            if (excelWriter != null) {
                try {
                    excelWriter.writeContext().finish(true);
                } catch (Throwable cleanupFailure) {
                    if (cleanupFailure != failure) {
                        failure.addSuppressed(cleanupFailure);
                    }
                }
            }
            throw failure;
        }
    }
}
