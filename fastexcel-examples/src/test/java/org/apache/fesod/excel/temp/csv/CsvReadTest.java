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

package org.apache.fesod.excel.temp.csv;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class CsvReadTest {

    @Test
    public void write() throws Exception {
        Appendable out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(TestFileUtil.createNewFile("csvWrite1.csv"))));
        CSVPrinter printer = CSVFormat.DEFAULT.withHeader("userId", "userName").print(out);
        for (int i = 0; i < 10; i++) {
            printer.printRecord("userId" + i, "userName" + i);
        }
        printer.flush();
        printer.close();
    }

    @Test
    public void read1() throws Exception {
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withNullString("")
                .parse(new FileReader("src/test/resources/poi/last_row_number_xssf_date_test.csv"));
        for (CSVRecord record : records) {
            String lastName = record.get(0);
            String firstName = record.get(1);
            log.info("row:{},{}", lastName, firstName);
        }
    }

    @Test
    public void csvWrite() throws Exception {
        // 写法1
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".csv";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        FastExcel.write(fileName, CsvData.class).sheet().doWrite(data());

        // 读
        List<Object> list = FastExcel.read(fileName).sheet(0).headRowNumber(0).doReadSync();
        log.info("数据：{}", list.size());
        for (Object data : list) {
            log.info("返回数据：{}", JSON.toJSONString(data));
        }
    }

    @Test
    public void writev2() throws Exception {
        // 写法1
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".csv";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        FastExcel.write(fileName, CsvData.class).sheet().doWrite(data());

        FastExcel.read(fileName, CsvData.class, new CsvDataListener()).sheet().doRead();
    }

    @Test
    public void writeFile() throws Exception {
        FileMagic fileMagic = FileMagic.valueOf(new File("src/test/resources/poi/last_row_number_xssf_date_test.csv"));
        Assertions.assertEquals(FileMagic.UNKNOWN, fileMagic);
        log.info("{}", fileMagic);
    }

    private List<CsvData> data() {
        List<CsvData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CsvData data = new CsvData();
            data.setString("字符,串" + i);
            // data.setDate(new Date());
            data.setDoubleData(0.56);
            data.setIgnore("忽略" + i);
            list.add(data);
        }
        return list;
    }

    @Test
    public void read() {
        //
        // Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        // for (CSVRecord record : records) {
        //    String lastName = record.get("id");
        //    String firstName = record.get("name");
        //    System.out.println(lastName);
        //    System.out.println(firstName);
        // }

    }
}
