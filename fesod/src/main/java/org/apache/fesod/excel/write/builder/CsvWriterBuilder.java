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

package org.apache.fesod.excel.write.builder;

import java.util.Collection;
import java.util.function.Supplier;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteWorkbook;

/**
 * Builder for CSV file writing
 */
public class CsvWriterBuilder extends AbstractExcelWriterParameterBuilder<CsvWriterBuilder, WriteSheet> {
    private WriteWorkbook writeWorkbook;
    private CSVFormat.Builder csvFormatBuilder;
    private WriteSheet writeSheet;

    private CsvWriterBuilder() {}

    public CsvWriterBuilder(WriteWorkbook writeWorkbook) {
        writeWorkbook.setExcelType(ExcelTypeEnum.CSV);
        this.writeWorkbook = writeWorkbook;
        this.writeSheet = new WriteSheet();
        this.csvFormatBuilder = CSVFormat.DEFAULT.builder();
    }

    /**
     * Sets the delimiter character
     *
     * @param delimiter the delimiter character
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder delimiter(String delimiter) {
        if (delimiter != null) {
            this.csvFormatBuilder.setDelimiter(delimiter);
        }
        return this;
    }

    /**
     * Sets the quote character
     *
     * @param quote the quote character
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder quote(Character quote) {
        return quote(quote, QuoteMode.MINIMAL);
    }

    /**
     * Sets the quote character and the quoting behavior
     *
     * @param quote     the quote character
     * @param quoteMode defines the quoting behavior
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder quote(Character quote, QuoteMode quoteMode) {
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
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder recordSeparator(String recordSeparator) {
        if (recordSeparator != null) {
            this.csvFormatBuilder.setRecordSeparator(recordSeparator);
        }
        return this;
    }

    /**
     * Sets the null string
     *
     * @param nullString the String to convert to and from {@code null}
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder nullString(String nullString) {
        if (nullString != null) {
            this.csvFormatBuilder.setNullString(nullString);
        }
        return this;
    }

    /**
     * Sets the escape character.
     *
     * @param escape the Character used to escape special characters in values
     * @return Returns a CsvWriterBuilder object, enabling method chaining
     */
    public CsvWriterBuilder escape(Character escape) {
        if (escape != null) {
            this.csvFormatBuilder.setEscape(escape);
        }
        return this;
    }

    private ExcelWriter buildExcelWriter() {
        this.csvFormatBuilder.setTrim(this.writeWorkbook.getAutoTrim() == null
                || this.writeWorkbook.getAutoTrim()
                || Boolean.TRUE.equals(this.writeWorkbook.getAutoStrip()));
        if (this.writeWorkbook.getNeedHead() != null) {
            this.csvFormatBuilder.setSkipHeaderRecord(!this.writeWorkbook.getNeedHead());
        }
        this.writeWorkbook.setCsvFormat(this.csvFormatBuilder.build());
        return new ExcelWriter(this.writeWorkbook);
    }

    public void doWrite(Collection<?> data) {
        if (writeWorkbook == null) {
            throw new ExcelGenerateException("Must use 'FastExcelFactory.write().csv()' to call this method");
        }
        ExcelWriter excelWriter = buildExcelWriter();
        excelWriter.write(data, this.writeSheet);
        excelWriter.finish();
    }

    public void doWrite(Supplier<Collection<?>> supplier) {
        doWrite(supplier.get());
    }

    @Override
    protected WriteSheet parameter() {
        return writeSheet;
    }
}
