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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * 测试poi
 *
 *
 **/
@Slf4j
public class Poi3Test {

    @Test
    public void Encryption(@TempDir Path tempDir) throws Exception {
        // Write out the encrypted version
        try (POIFSFileSystem fs = new POIFSFileSystem();
                FileOutputStream fos = new FileOutputStream(
                        tempDir.resolve(System.currentTimeMillis() + ".xlsx").toFile()); ) {
            String file = TestFileUtil.getPath() + "large" + File.separator + "large07.xlsx";
            EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
            Encryptor enc = info.getEncryptor();
            enc.confirmPassword("foobaa");
            OPCPackage opc = OPCPackage.open(new File(file), PackageAccess.READ_WRITE);
            OutputStream os = enc.getDataStream(fs);
            opc.save(os);
            opc.close();
            fs.writeFilesystem(fos);
        }
    }

    @Test
    public void Encryption2() throws Exception {
        Biff8EncryptionKey.setCurrentUserPassword("incorrect pwd");
        POIFSFileSystem fs = new POIFSFileSystem(new File("src/test/resources/demo/pwd_123.xls"), true);
        Assertions.assertThrows(EncryptedDocumentException.class, () -> new HSSFWorkbook(fs.getRoot(), true));
        Biff8EncryptionKey.setCurrentUserPassword("123");
        HSSFWorkbook hwb = new HSSFWorkbook(
                new POIFSFileSystem(new File("src/test/resources/demo/pwd_123.xls"), true).getRoot(), true);
        Assertions.assertEquals("Sheet1", hwb.getSheetAt(0).getSheetName());
        Biff8EncryptionKey.setCurrentUserPassword(null);
        System.out.println(hwb.getSheetAt(0).getSheetName());
    }
}
