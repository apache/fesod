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

package org.apache.fesod.excel.charset;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.metadata.data.ReadCellData;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * charset
 *
 *
 */
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CharsetDataTest {
    private static final Charset GBK = Charset.forName("GBK");
    private static File fileCsvGbk;
    private static File fileCsvUtf8;
    private static File fileCsvError;

    @BeforeAll
    public static void init() {
        fileCsvGbk = TestFileUtil.createNewFile("charset" + File.separator + "fileCsvGbk.csv");
        fileCsvUtf8 = TestFileUtil.createNewFile("charset" + File.separator + "fileCsvUtf8.csv");
        fileCsvError = TestFileUtil.createNewFile("charset" + File.separator + "fileCsvError.csv");
    }

    @Test
    public void t01ReadAndWriteCsv() {
        readAndWrite(fileCsvGbk, GBK);
        readAndWrite(fileCsvUtf8, StandardCharsets.UTF_8);
    }

    @Test
    public void t02ReadAndWriteCsvError() {
        FastExcel.write(fileCsvError, CharsetData.class).charset(GBK).sheet().doWrite(data());
        FastExcel.read(fileCsvError, CharsetData.class, new ReadListener<CharsetData>() {

                    private final List<CharsetData> dataList = Lists.newArrayList();

                    @Override
                    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                        String head = headMap.get(0).getStringValue();
                        Assertions.assertNotEquals("姓名", head);
                    }

                    @Override
                    public void invoke(CharsetData data, AnalysisContext context) {
                        dataList.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .charset(StandardCharsets.UTF_8)
                .sheet()
                .doRead();
    }

    private void readAndWrite(File file, Charset charset) {
        FastExcel.write(file, CharsetData.class).charset(charset).sheet().doWrite(data());
        FastExcel.read(file, CharsetData.class, new ReadListener<CharsetData>() {

                    private final List<CharsetData> dataList = Lists.newArrayList();

                    @Override
                    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                        String head = headMap.get(0).getStringValue();
                        Assertions.assertEquals("姓名", head);
                    }

                    @Override
                    public void invoke(CharsetData data, AnalysisContext context) {
                        dataList.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        Assertions.assertEquals(dataList.size(), 10);
                        CharsetData charsetData = dataList.get(0);
                        Assertions.assertEquals("姓名0", charsetData.getName());
                        Assertions.assertEquals(0, (long) charsetData.getAge());
                    }
                })
                .charset(charset)
                .sheet()
                .doRead();
    }

    private List<CharsetData> data() {
        List<CharsetData> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            CharsetData data = new CharsetData();
            data.setName("姓名" + i);
            data.setAge(i);
            list.add(data);
        }
        return list;
    }
}
