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

package org.apache.fesod.excel;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.fesod.excel.read.builder.ExcelReaderBuilder;
import org.apache.fesod.excel.read.builder.ExcelReaderSheetBuilder;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.write.builder.ExcelWriterBuilder;
import org.apache.fesod.excel.write.builder.ExcelWriterSheetBuilder;
import org.apache.fesod.excel.write.builder.ExcelWriterTableBuilder;

/**
 * Reader and writer factory class
 */
public class FastExcelFactory {

    /**
     * Build excel the write
     *
     * @return
     */
    public static ExcelWriterBuilder write() {
        return new ExcelWriterBuilder();
    }

    /**
     * Build excel the write
     *
     * @param file File to write
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(File file) {
        return write(file, null);
    }

    /**
     * Build excel the write
     *
     * @param file File to write
     * @param head Annotate the class for configuration information
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(File file, Class head) {
        return new ExcelWriterBuilder().file(file).headIfNotNull(head);
    }

    /**
     * Build excel the write
     *
     * @param pathName File path to write
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(String pathName) {
        return write(pathName, null);
    }

    /**
     * Build excel the write
     *
     * @param pathName File path to write
     * @param head     Annotate the class for configuration information
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(String pathName, Class head) {
        return new ExcelWriterBuilder().file(pathName).headIfNotNull(head);
    }

    /**
     * Build excel the write
     *
     * @param outputStream Output stream to write
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(OutputStream outputStream) {
        return write(outputStream, null);
    }

    /**
     * Build excel the write
     *
     * @param outputStream Output stream to write
     * @param head         Annotate the class for configuration information.
     * @return Excel writer builder
     */
    public static ExcelWriterBuilder write(OutputStream outputStream, Class head) {
        return new ExcelWriterBuilder().file(outputStream).headIfNotNull(head);
    }

    /**
     * Build excel the <code>writerSheet</code>
     *
     * @return Excel sheet writer builder
     */
    public static ExcelWriterSheetBuilder writerSheet() {
        return writerSheet(null, null);
    }

    /**
     * Build excel the <code>writerSheet</code>
     *
     * @param sheetNo Index of sheet,0 base.
     * @return Excel sheet writer builder.
     */
    public static ExcelWriterSheetBuilder writerSheet(Integer sheetNo) {
        return writerSheet(sheetNo, null);
    }

    /**
     * Build excel the 'writerSheet'
     *
     * @param sheetName The name of sheet.
     * @return Excel sheet writer builder.
     */
    public static ExcelWriterSheetBuilder writerSheet(String sheetName) {
        return writerSheet(null, sheetName);
    }

    /**
     * Build excel the 'writerSheet'
     *
     * @param sheetNo   Index of sheet,0 base.
     * @param sheetName The name of sheet.
     * @return Excel sheet writer builder.
     */
    public static ExcelWriterSheetBuilder writerSheet(Integer sheetNo, String sheetName) {
        return new ExcelWriterSheetBuilder().sheetNoIfNotNull(sheetNo).sheetNameIfNotNull(sheetName);
    }

    /**
     * Build excel the <code>writerTable</code>
     *
     * @return Excel table writer builder.
     */
    public static ExcelWriterTableBuilder writerTable() {
        return writerTable(null);
    }

    /**
     * Build excel the 'writerTable'
     *
     * @param tableNo Index of table,0 base.
     * @return Excel table writer builder.
     */
    public static ExcelWriterTableBuilder writerTable(Integer tableNo) {
        return new ExcelWriterTableBuilder().tableNoIfNotNull(tableNo);
    }

    /**
     * Build excel the read
     *
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read() {
        return new ExcelReaderBuilder();
    }

    /**
     * Build excel the read
     *
     * @param file File to read.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(File file) {
        return read(file, null, null);
    }

    /**
     * Build excel the read
     *
     * @param file         File to read.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(File file, ReadListener readListener) {
        return read(file, null, readListener);
    }

    /**
     * Build excel the read
     *
     * @param file         File to read.
     * @param head         Annotate the class for configuration information.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(File file, Class head, ReadListener readListener) {
        return new ExcelReaderBuilder().file(file).headIfNotNull(head).registerReadListenerIfNotNull(readListener);
    }

    /**
     * Build excel the read
     *
     * @param pathName File path to read.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(String pathName) {
        return read(pathName, null, null);
    }

    /**
     * Build excel the read
     *
     * @param pathName     File path to read.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(String pathName, ReadListener readListener) {
        return read(pathName, null, readListener);
    }

    /**
     * Build excel the read
     *
     * @param pathName     File path to read.
     * @param head         Annotate the class for configuration information.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(String pathName, Class head, ReadListener readListener) {
        return new ExcelReaderBuilder().file(pathName).headIfNotNull(head).registerReadListenerIfNotNull(readListener);
    }

    /**
     * Build excel the read
     *
     * @param inputStream Input stream to read.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(InputStream inputStream) {
        return read(inputStream, null, null);
    }

    /**
     * Build excel the read
     *
     * @param inputStream  Input stream to read.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(InputStream inputStream, ReadListener readListener) {
        return read(inputStream, null, readListener);
    }

    /**
     * Build excel the read
     *
     * @param inputStream  Input stream to read.
     * @param head         Annotate the class for configuration information.
     * @param readListener Read listener.
     * @return Excel reader builder.
     */
    public static ExcelReaderBuilder read(InputStream inputStream, Class head, ReadListener readListener) {
        return new ExcelReaderBuilder()
                .file(inputStream)
                .headIfNotNull(head)
                .registerReadListenerIfNotNull(readListener);
    }

    /**
     * Build excel the 'readSheet'
     *
     * @return Excel sheet reader builder.
     */
    public static ExcelReaderSheetBuilder readSheet() {
        return readSheet(null, null);
    }

    /**
     * Build excel the 'readSheet'
     *
     * @param sheetNo Index of sheet,0 base.
     * @return Excel sheet reader builder.
     */
    public static ExcelReaderSheetBuilder readSheet(Integer sheetNo) {
        return readSheet(sheetNo, null);
    }

    /**
     * Build excel the 'readSheet'
     *
     * @param sheetName The name of sheet.
     * @return Excel sheet reader builder.
     */
    public static ExcelReaderSheetBuilder readSheet(String sheetName) {
        return readSheet(null, sheetName);
    }

    /**
     * Build excel the 'readSheet'
     *
     * @param sheetNo   Index of sheet,0 base.
     * @param sheetName The name of sheet.
     * @return Excel sheet reader builder.
     */
    public static ExcelReaderSheetBuilder readSheet(Integer sheetNo, String sheetName) {
        return readSheet(sheetNo, sheetName, null);
    }

    /**
     * Build excel the 'readSheet'
     *
     * @param sheetNo   Index of sheet,0 base.
     * @param sheetName The name of sheet.
     * @param numRows   The number of rows to read, the default is all, start with 0.
     * @return
     */
    public static ExcelReaderSheetBuilder readSheet(Integer sheetNo, String sheetName, Integer numRows) {
        return new ExcelReaderSheetBuilder()
                .sheetNoIfNotNull(sheetNo)
                .sheetNameIfNotNull(sheetName)
                .numRowsIfNotNull(numRows);
    }
}
