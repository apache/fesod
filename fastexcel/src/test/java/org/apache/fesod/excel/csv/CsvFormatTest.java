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

package org.apache.fesod.excel.csv;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.fesod.excel.ExcelReader;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.metadata.csv.CsvConstant;
import org.apache.fesod.excel.metadata.csv.CsvWorkbook;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.ReadWorkbookHolder;
import org.apache.fesod.excel.read.metadata.holder.csv.CsvReadWorkbookHolder;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.DateUtils;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class CsvFormatTest {

    private static File csvFile;
    private static final String CSV_BASE = "csv" + File.separator;
    private static final String STRING_PREFIX = "String";
    private static List<CsvData> csvDataList;

    @BeforeAll
    public static void init() {
        csvDataList = dataList(10, STRING_PREFIX);
    }

    @Test
    public void testSimple() {

        csvFile = TestFileUtil.readFile(CSV_BASE + "simple.csv");
        doTest(false, csvFile, null, null, null, null, null);

        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-simple.csv");
        doTest(true, csvFile, null, null, null, null, null);

        // sheet
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-sheet-simple.csv");
        FastExcel.write(csvFile, CsvData.class).sheet().doWrite(csvDataList);
        List<CsvData> dataList = FastExcel.read(csvFile, CsvData.class, new CsvDataListener())
                .sheet()
                .doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0).getString());
    }

    @Test
    public void testDelimiter() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-delimiter.csv");

        log.info("{} delimiter", CsvConstant.AT);
        doTest(true, csvFile, CsvConstant.AT, null, null, null, null);

        log.info("{} delimiter", CsvConstant.TAB);
        doTest(true, csvFile, CsvConstant.TAB, null, null, null, null);
    }

    @Test
    public void testQuote() {
        csvFile = TestFileUtil.readFile(CSV_BASE + "simple-quote.csv");
        doTest(false, csvFile, null, '"', null, null, null);

        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-quote.csv");
        doTest(true, csvFile, null, '"', null, null, null);
    }

    @Test
    public void testNullString() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-null-string.csv");
        doTest(true, csvFile, null, null, null, CsvConstant.UNICODE_EMPTY, null);
        doTest(true, csvFile, null, null, null, CsvConstant.SQL_NULL_STRING, null);
    }

    @Test
    public void testRecordSeparator() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-record-separator.csv");
        doTest(true, csvFile, null, '"', CsvConstant.LF, null, null);
        doTest(true, csvFile, null, '"', CsvConstant.CR, null, null);
    }

    @Test
    public void testEscape() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-escape.csv");
        doTest(true, csvFile, null, null, null, null, CsvConstant.BACKSLASH);
        doTest(true, csvFile, null, null, null, null, CsvConstant.DOUBLE_QUOTE);
    }

    @Test
    public void testNoHead() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-no-head.csv");
        FastExcel.write(csvFile, CsvData.class).needHead(false).csv().doWrite(csvDataList);
        List<Object> dataList = FastExcel.read(csvFile, new CsvDataListener())
                .headRowNumber(0)
                .csv()
                .doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0));
    }

    @Test
    public void testAutoTrim() {
        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-auto-trim.csv");
        FastExcel.write(csvFile, CsvData.class)
                .autoTrim(Boolean.FALSE)
                .csv()
                .doWrite(dataList(10, " " + STRING_PREFIX));
        List<Object> dataList = FastExcel.read(csvFile, CsvData.class, new CsvDataListener())
                .autoTrim(Boolean.FALSE)
                .csv()
                .doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0));
    }

    @Test
    public void testHolder() {
        CSVFormat csvFormat =
                CSVFormat.DEFAULT.builder().setDelimiter(CsvConstant.AT).build();

        csvFile = TestFileUtil.createNewFile(CSV_BASE + "csv-delimiter.csv");
        try (ExcelWriter excelWriter = FastExcel.write(csvFile, CsvData.class)
                .excelType(ExcelTypeEnum.CSV)
                .build()) {
            WriteWorkbookHolder writeWorkbookHolder = excelWriter.writeContext().writeWorkbookHolder();
            Workbook workbook = writeWorkbookHolder.getWorkbook();
            if (workbook instanceof CsvWorkbook) {
                CsvWorkbook csvWorkbook = (CsvWorkbook) workbook;
                csvWorkbook.setCsvFormat(csvFormat);
                writeWorkbookHolder.setWorkbook(csvWorkbook);
            }
            WriteSheet writeSheet = FastExcel.writerSheet(0).build();
            excelWriter.write(csvDataList, writeSheet);
        }

        csvFile = TestFileUtil.readFile(CSV_BASE + "csv-delimiter.csv");
        try (ExcelReader excelReader =
                FastExcel.read(csvFile, CsvData.class, new CsvDataListener()).build()) {
            ReadWorkbookHolder readWorkbookHolder =
                    excelReader.analysisContext().readWorkbookHolder();
            if (readWorkbookHolder instanceof CsvReadWorkbookHolder) {
                CsvReadWorkbookHolder csvReadWorkbookHolder = (CsvReadWorkbookHolder) readWorkbookHolder;
                csvReadWorkbookHolder.setCsvFormat(csvFormat);
            }
            ReadSheet readSheet = FastExcel.readSheet(0).build();
            excelReader.read(readSheet);
        }
    }

    @Test
    public void writeWithCommonCsv() {
        csvFile = TestFileUtil.readFile(CSV_BASE + "write-common-csv.csv");
        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setQuote(CsvConstant.DOUBLE_QUOTE)
                .setQuoteMode(QuoteMode.ALL)
                .build();
        writeWithCommonCsv(csvFile, csvFormat, dataList(10, STRING_PREFIX));
    }

    private void doTest(
            boolean isCreate,
            File csvFile,
            String delimiter,
            Character quote,
            String recordSeparator,
            String nullString,
            Character escapse) {
        if (isCreate) {
            String appendStr = "";
            if (quote != null) {
                appendStr = appendStr + quote;
            }
            if (escapse != null) {
                appendStr = appendStr + escapse;
            }
            if (StringUtils.isNotBlank(appendStr)) {
                csvDataList = dataList(10, STRING_PREFIX + appendStr);
            }
            FastExcel.write(csvFile, CsvData.class)
                    .csv()
                    .delimiter(delimiter)
                    .quote(quote, QuoteMode.MINIMAL)
                    .nullString(nullString)
                    .recordSeparator(recordSeparator)
                    .escape(escapse)
                    .doWrite(csvDataList);
        }

        List<CsvData> dataList = FastExcel.read(csvFile, CsvData.class, new CsvDataListener())
                .csv()
                .delimiter(delimiter)
                .quote(quote, QuoteMode.MINIMAL)
                .nullString(nullString)
                .recordSeparator(recordSeparator)
                .escape(escapse)
                .doReadSync();
        Assertions.assertEquals(10, dataList.size());
        Assertions.assertNotNull(dataList.get(0).getString());
    }

    private static List<CsvData> dataList(int size, String prefix) {
        List<CsvData> dataList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CsvData data = new CsvData();
            data.setString(prefix + i);
            data.setDate(i < 6 ? new Date() : null);
            data.setDoubleData(99.001000D);
            dataList.add(data);
        }
        return dataList;
    }

    private void writeWithCommonCsv(File csvFile, CSVFormat csvFormat, List<CsvData> dataList) {
        try {
            Appendable out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(csvFile.toPath())));
            CSVPrinter printer = csvFormat.print(out);
            for (CsvData data : dataList) {
                // format date
                printer.printRecord(
                        data.getString(),
                        DateUtils.format(data.getDate(), DateUtils.DATE_FORMAT_19),
                        data.getDoubleData());
            }
            printer.flush();
            printer.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
