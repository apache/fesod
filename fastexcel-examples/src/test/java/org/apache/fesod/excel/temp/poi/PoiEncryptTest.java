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

package org.apache.fesod.excel.temp.poi;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.temp.data.EncryptData;
import org.apache.fesod.excel.temp.data.SimpleData;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 *
 */
public class PoiEncryptTest {
    @Test
    public void encrypt() throws Exception {

        XSSFWorkbook workbook = new XSSFWorkbook();
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook);

        Sheet sheet = sxssfWorkbook.createSheet("sheet1");
        sheet.createRow(0).createCell(0).setCellValue("T2");

        POIFSFileSystem fs = new POIFSFileSystem();
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);

        Encryptor enc = info.getEncryptor();
        enc.confirmPassword("123456");

        // write the workbook into the encrypted OutputStream
        OutputStream encos = enc.getDataStream(fs);
        sxssfWorkbook.write(encos);
        sxssfWorkbook.dispose();
        sxssfWorkbook.close();
        encos.close(); // this is necessary before writing out the FileSystem

        OutputStream os =
                new FileOutputStream(TestFileUtil.createNewFile("encrypt" + System.currentTimeMillis() + ".xlsx"));
        fs.writeFilesystem(os);
        os.close();
        fs.close();
    }

    @Test
    public void encryptExcel() throws Exception {
        FastExcel.write(
                        TestFileUtil.createNewFile("encryptv2" + System.currentTimeMillis() + ".xlsx"),
                        EncryptData.class)
                .password("123456")
                .sheet()
                .doWrite(data());
    }

    private List<SimpleData> data() {
        List<SimpleData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SimpleData simpleData = new SimpleData();
            simpleData.setName("姓名" + i);
            list.add(simpleData);
        }
        return list;
    }
}
