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

package org.apache.fesod.excel.read.builder;

import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.event.SyncReadListener;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.ReadWorkbook;
import org.apache.fesod.excel.support.ExcelTypeEnum;

/**
 * Builder for CSV file reading
 */
public class CsvReaderBuilder extends AbstractExcelReaderParameterBuilder<CsvReaderBuilder, ReadSheet> {
    private ReadWorkbook readWorkbook;
    private ReadSheet readSheet;
    private CSVFormat.Builder csvFormatBuilder;

    private CsvReaderBuilder() {}

    public CsvReaderBuilder(ReadWorkbook readWorkbook) {
        readWorkbook.setExcelType(ExcelTypeEnum.CSV);
        this.readWorkbook = readWorkbook;
        this.readSheet = new ReadSheet();
        this.csvFormatBuilder = CSVFormat.DEFAULT.builder();
    }

    /**
     * Sets the delimiter character
     *
     * @param delimiter the delimiter character
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder delimiter(String delimiter) {
        if (delimiter != null) {
            this.csvFormatBuilder.setDelimiter(delimiter);
        }
        return this;
    }

    /**
     * Sets the quote character
     *
     * @param quote the quote character
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder quote(Character quote) {
        return quote(quote, QuoteMode.MINIMAL);
    }

    /**
     * Sets the quote character and the quoting behavior
     *
     * @param quote     the quote character
     * @param quoteMode defines the quoting behavior
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder quote(Character quote, QuoteMode quoteMode) {
        if (quote != null) {
            this.csvFormatBuilder.setQuote(quote);
        }
        if (quoteMode != null) {
            this.csvFormatBuilder.setQuoteMode(quoteMode);
        }
        return this;
    }

    /**
     * Sets the line separator
     *
     * @param recordSeparator the line separator
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder recordSeparator(String recordSeparator) {
        if (recordSeparator != null) {
            this.csvFormatBuilder.setRecordSeparator(recordSeparator);
        }
        return this;
    }

    /**
     * Sets the null string
     *
     * @param nullString the String to convert to and from {@code null}
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder nullString(String nullString) {
        if (nullString != null) {
            this.csvFormatBuilder.setNullString(nullString);
        }
        return this;
    }

    /**
     * Sets the escape character.
     *
     * @param escape the Character used to escape special characters in values
     * @return Returns a CsvReaderBuilder object, enabling method chaining
     */
    public CsvReaderBuilder escape(Character escape) {
        if (escape != null) {
            this.csvFormatBuilder.setEscape(escape);
        }
        return this;
    }

    private ExcelReader buildExcelReader() {
        this.csvFormatBuilder.setTrim(this.readWorkbook.getAutoTrim() == null
                || this.readWorkbook.getAutoTrim()
                || Boolean.TRUE.equals(this.readWorkbook.getAutoStrip()));
        if (this.readWorkbook.getIgnoreEmptyRow() != null) {
            this.csvFormatBuilder.setIgnoreEmptyLines(this.readWorkbook.getIgnoreEmptyRow());
        }
        this.readWorkbook.setCsvFormat(this.csvFormatBuilder.build());
        return new ExcelReader(this.readWorkbook);
    }

    public void doRead() {
        if (this.readWorkbook == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.read().csv()' to call this method");
        }
        ExcelReader excelReader = buildExcelReader();
        excelReader.read(this.readSheet);
        excelReader.finish();
    }

    /**
     * synchronous read and returns the results
     *
     * @return Returns a list containing the read data
     */
    public <T> List<T> doReadSync() {
        if (this.readWorkbook == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.read().csv()' to call this method");
        }
        ExcelReader excelReader = buildExcelReader();
        // Register a synchronous read listener
        SyncReadListener syncReadListener = new SyncReadListener();
        registerReadListener(syncReadListener);
        excelReader.read(this.readSheet);
        excelReader.finish();
        return (List<T>) syncReadListener.getList();
    }

    @Override
    protected ReadSheet parameter() {
        return this.readSheet;
    }
}
