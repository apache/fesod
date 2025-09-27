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

package org.apache.fesod.excel.exception;

/**
 * Throw the exception when you need to stop
 * This exception will only stop the parsing of the current sheet. If you want to stop the entire excel parsing, please
 * use ExcelAnalysisStopException.
 *
 * The org.apache.fesod.excel.read.listener.ReadListener#doAfterAllAnalysed(org.apache.fesod.excel.context.AnalysisContext) method
 * is called after the call is stopped.
 *
 *
 * @see ExcelAnalysisStopException
 * @since 3.3.4
 */
public class ExcelAnalysisStopSheetException extends ExcelAnalysisException {

    public ExcelAnalysisStopSheetException() {}

    public ExcelAnalysisStopSheetException(String message) {
        super(message);
    }

    public ExcelAnalysisStopSheetException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelAnalysisStopSheetException(Throwable cause) {
        super(cause);
    }
}
