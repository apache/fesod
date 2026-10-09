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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.fesod.sheet.support.ExcelTypeEnum;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Password protection applied when a workbook is finished: xlsx is encrypted into the output stream or the output
 * file, and the xls password is cleared afterwards.
 */
final class WorkbookEncryption {

    private final WriteWorkbookHolder writeWorkbookHolder;

    WorkbookEncryption(WriteWorkbookHolder writeWorkbookHolder) {
        this.writeWorkbookHolder = writeWorkbookHolder;
    }

    /**
     * Clears encryption settings for older Excel formats.
     */
    void clearEncrypt03() {
        if (!isEncrypted(ExcelTypeEnum.XLS)) {
            return;
        }
        Biff8EncryptionKey.setCurrentUserPassword(null);
    }

    /**
     * Checks whether the workbook is password-protected and written as the given type.
     */
    private boolean isEncrypted(ExcelTypeEnum excelType) {
        return !StringUtils.isEmpty(writeWorkbookHolder.getPassword())
                && excelType.equals(writeWorkbookHolder.getExcelType());
    }

    /**
     * Encrypts the output stream for newer Excel formats.
     *
     * @return True if encryption is successful, otherwise false.
     * @throws Exception If an error occurs during encryption.
     */
    boolean doOutputStreamEncrypt07() throws Exception {
        if (!isEncrypted(ExcelTypeEnum.XLSX)) {
            return false;
        }
        if (writeWorkbookHolder.getFile() != null) {
            return false;
        }
        File tempXlsx = FileUtils.createTmpFile(UUID.randomUUID() + ".xlsx");
        try {
            try (FileOutputStream tempFileOutputStream = new FileOutputStream(tempXlsx)) {
                try {
                    writeWorkbookHolder.getWorkbook().write(tempFileOutputStream);
                } finally {
                    writeWorkbookHolder.getWorkbook().close();
                }
            }
            try (POIFSFileSystem fileSystem = openFileSystemAndEncrypt(tempXlsx)) {
                fileSystem.writeFilesystem(writeWorkbookHolder.getOutputStream());
            }
        } finally {
            if (tempXlsx.exists() && !tempXlsx.delete()) {
                throw new ExcelGenerateException("Can not delete temp File!");
            }
        }
        return true;
    }

    /**
     * To encrypt
     */
    void doFileEncrypt07() throws Exception {
        // Check if the password is empty or the file type is not xlsx, if so, return directly
        if (!isEncrypted(ExcelTypeEnum.XLSX)) {
            return;
        }
        // Check if the file is null, if so, return directly
        if (writeWorkbookHolder.getFile() == null) {
            return;
        }
        try {
            // Use try-with-resources to automatically close resources, encrypt and write the file
            try (POIFSFileSystem fileSystem = openFileSystemAndEncrypt(writeWorkbookHolder.getFile());
                    FileOutputStream fileOutputStream = new FileOutputStream(writeWorkbookHolder.getFile())) {
                fileSystem.writeFilesystem(fileOutputStream);
            }
        } catch (Throwable t) {
            // The workbook was written to the file before encryption, so the file still holds the unprotected
            // workbook and must not be left behind.
            File file = writeWorkbookHolder.getFile();
            if (file.exists() && !file.delete()) {
                throw new ExcelGenerateException("Can not delete unencrypted file: " + file.getAbsolutePath(), t);
            }
            throw t;
        }
    }

    /**
     * Opens a file system and encrypts the given file.
     *
     * This method creates a new POIFSFileSystem instance, sets up an Encryptor with a standard encryption mode,
     * and confirms the password for encryption. It then opens the provided file in read-write mode, saves its content
     * into an encrypted output stream, and finally returns the encrypted file system.
     *
     * @param file The file to be encrypted.
     * @return An encrypted POIFSFileSystem object.
     * @throws Exception If any error occurs during the encryption process or file handling.
     */
    private POIFSFileSystem openFileSystemAndEncrypt(File file) throws Exception {
        POIFSFileSystem fileSystem = new POIFSFileSystem();
        Encryptor encryptor = new EncryptionInfo(EncryptionMode.standard).getEncryptor();
        encryptor.confirmPassword(writeWorkbookHolder.getPassword());
        try (OPCPackage opcPackage = OPCPackage.open(file, PackageAccess.READ_WRITE);
                OutputStream outputStream = encryptor.getDataStream(fileSystem)) {
            opcPackage.save(outputStream);
        }
        return fileSystem;
    }
}
