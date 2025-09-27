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

package org.apache.fesod.excel.converter;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.util.FileUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConverterDataTest {

    private static File file07;
    private static File file03;
    private static File fileCsv;
    private static File fileImage07;
    private static File fileImage03;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("converter07.xlsx");
        file03 = TestFileUtil.createNewFile("converter03.xls");
        fileCsv = TestFileUtil.createNewFile("converterCsv.csv");
        fileImage07 = TestFileUtil.createNewFile("converterImage07.xlsx");
        fileImage03 = TestFileUtil.createNewFile("converterImage03.xls");
    }

    @Test
    public void t01ReadAndWrite07() throws Exception {
        readAndWrite(file07);
    }

    @Test
    public void t02ReadAndWrite03() throws Exception {
        readAndWrite(file03);
    }

    @Test
    public void t03ReadAndWriteCsv() throws Exception {
        readAndWrite(fileCsv);
    }

    private void readAndWrite(File file) throws Exception {
        FastExcel.write(file, ConverterWriteData.class).sheet().doWrite(data());
        FastExcel.read(file, ConverterReadData.class, new ConverterDataListener())
                .sheet()
                .doRead();
    }

    @Test
    public void t11ReadAllConverter07() {
        readAllConverter("converter" + File.separator + "converter07.xlsx");
    }

    @Test
    public void t12ReadAllConverter03() {
        readAllConverter("converter" + File.separator + "converter03.xls");
    }

    @Test
    public void t13ReadAllConverterCsv() {
        readAllConverter("converter" + File.separator + "converterCsv.csv");
    }

    @Test
    public void t21WriteImage07() throws Exception {
        writeImage(fileImage07);
    }

    @Test
    public void t22WriteImage03() throws Exception {
        writeImage(fileImage03);
    }

    private void writeImage(File file) throws Exception {
        InputStream inputStream = null;
        try {
            List<ImageData> list = new ArrayList<>();
            ImageData imageData = new ImageData();
            list.add(imageData);
            String imagePath = TestFileUtil.getPath() + "converter" + File.separator + "img.jpg";
            imageData.setByteArray(FileUtils.readFileToByteArray(new File(imagePath)));
            imageData.setFile(new File(imagePath));
            imageData.setString(imagePath);
            inputStream = FileUtils.openInputStream(new File(imagePath));
            imageData.setInputStream(inputStream);
            FastExcel.write(file, ImageData.class).sheet().doWrite(list);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void readAllConverter(String fileName) {
        FastExcel.read(TestFileUtil.readFile(fileName), ReadAllConverterData.class, new ReadAllConverterDataListener())
                .sheet()
                .doRead();
    }

    private List<ConverterWriteData> data() throws Exception {
        List<ConverterWriteData> list = new ArrayList<ConverterWriteData>();
        ConverterWriteData converterWriteData = new ConverterWriteData();
        converterWriteData.setDate(TestUtil.TEST_DATE);
        converterWriteData.setLocalDate(TestUtil.TEST_LOCAL_DATE);
        converterWriteData.setLocalDateTime(TestUtil.TEST_LOCAL_DATE_TIME);
        converterWriteData.setBooleanData(Boolean.TRUE);
        converterWriteData.setBigDecimal(BigDecimal.ONE);
        converterWriteData.setBigInteger(BigInteger.ONE);
        converterWriteData.setLongData(1L);
        converterWriteData.setIntegerData(1);
        converterWriteData.setShortData((short) 1);
        converterWriteData.setByteData((byte) 1);
        converterWriteData.setDoubleData(1.0);
        converterWriteData.setFloatData((float) 1.0);
        converterWriteData.setString("测试");
        converterWriteData.setCellData(new WriteCellData<>("自定义"));
        list.add(converterWriteData);
        return list;
    }
}
