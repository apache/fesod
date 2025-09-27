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

package org.apache.fesod.excel.context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.enums.WriteTypeEnum;
import org.apache.fesod.excel.exception.ExcelGenerateException;
import org.apache.fesod.excel.metadata.CellRange;
import org.apache.fesod.excel.metadata.Head;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.support.ExcelTypeEnum;
import org.apache.fesod.excel.util.ClassUtils;
import org.apache.fesod.excel.util.DateUtils;
import org.apache.fesod.excel.util.FileUtils;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.util.NumberDataFormatterUtils;
import org.apache.fesod.excel.util.StringUtils;
import org.apache.fesod.excel.util.WorkBookUtil;
import org.apache.fesod.excel.util.WriteHandlerUtils;
import org.apache.fesod.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.fesod.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.fesod.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.excel.write.handler.context.WorkbookWriteHandlerContext;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.apache.fesod.excel.write.metadata.WriteWorkbook;
import org.apache.fesod.excel.write.metadata.holder.WriteHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteTableHolder;
import org.apache.fesod.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.fesod.excel.write.property.ExcelWriteHeadProperty;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Implementation of the WriteContext interface, which serves as the main anchorage point for writing Excel files.
 * This class manages the context for writing workbooks, sheets, and tables, including initialization, handling,
 * and finishing the write process.
 *
 */
@Slf4j
public class WriteContextImpl implements WriteContext {

    private static final String NO_SHEETS = "no sheets";

    /**
     * The Workbook currently being written.
     */
    private WriteWorkbookHolder writeWorkbookHolder;

    /**
     * The current sheet holder being managed.
     */
    private WriteSheetHolder writeSheetHolder;

    /**
     * The table currently being written.
     */
    private WriteTableHolder writeTableHolder;

    /**
     * Configuration of the currently operated cell.
     */
    private WriteHolder currentWriteHolder;

    /**
     * Prevents multiple shutdowns of the context.
     */
    private boolean finished = false;

    /**
     * Constructor to initialize the WriteContextImpl with a WriteWorkbook.
     *
     * @param writeWorkbook The workbook configuration for writing.
     */
    public WriteContextImpl(WriteWorkbook writeWorkbook) {
        if (writeWorkbook == null) {
            throw new IllegalArgumentException("Workbook argument cannot be null");
        }
        if (log.isDebugEnabled()) {
            log.debug("Begin to Initialization 'WriteContextImpl'");
        }
        initCurrentWorkbookHolder(writeWorkbook);

        WorkbookWriteHandlerContext workbookWriteHandlerContext =
                WriteHandlerUtils.createWorkbookWriteHandlerContext(this);
        WriteHandlerUtils.beforeWorkbookCreate(workbookWriteHandlerContext);
        try {
            WorkBookUtil.createWorkBook(writeWorkbookHolder);
        } catch (Exception e) {
            throw new ExcelGenerateException("Create workbook failure", e);
        }
        WriteHandlerUtils.afterWorkbookCreate(workbookWriteHandlerContext);
        if (log.isDebugEnabled()) {
            log.debug("Initialization 'WriteContextImpl' complete");
        }
    }

    /**
     * Initializes the current workbook holder with the provided WriteWorkbook.
     *
     * @param writeWorkbook The workbook configuration for writing.
     */
    private void initCurrentWorkbookHolder(WriteWorkbook writeWorkbook) {
        writeWorkbookHolder = new WriteWorkbookHolder(writeWorkbook);
        currentWriteHolder = writeWorkbookHolder;
        if (log.isDebugEnabled()) {
            log.debug("CurrentConfiguration is writeWorkbookHolder");
        }
    }

    /**
     * Sets the current sheet for writing.
     *
     * @param writeSheet The sheet configuration for writing.
     * @param writeType  The type of write operation.
     */
    @Override
    public void currentSheet(WriteSheet writeSheet, WriteTypeEnum writeType) {
        if (writeSheet == null) {
            throw new IllegalArgumentException("Sheet argument cannot be null");
        }
        if (selectSheetFromCache(writeSheet)) {
            return;
        }

        initCurrentSheetHolder(writeSheet);

        // Supplementary execution for workbook handlers.
        WorkbookWriteHandlerContext workbookWriteHandlerContext =
                WriteHandlerUtils.createWorkbookWriteHandlerContext(this);
        WriteHandlerUtils.beforeWorkbookCreate(workbookWriteHandlerContext, true);
        WriteHandlerUtils.afterWorkbookCreate(workbookWriteHandlerContext, true);

        // Initialize the current sheet.
        initSheet(writeType);
    }

    /**
     * Checks if the sheet exists in the cache and sets it as the current sheet if found.
     *
     * @param writeSheet The sheet configuration for writing.
     * @return True if the sheet is found in the cache, otherwise false.
     */
    private boolean selectSheetFromCache(WriteSheet writeSheet) {
        writeSheetHolder = null;
        Integer sheetNo = writeSheet.getSheetNo();
        if (sheetNo == null && StringUtils.isEmpty(writeSheet.getSheetName())) {
            sheetNo = 0;
        }
        if (sheetNo != null) {
            writeSheetHolder =
                    writeWorkbookHolder.getHasBeenInitializedSheetIndexMap().get(sheetNo);
        }
        if (writeSheetHolder == null && !StringUtils.isEmpty(writeSheet.getSheetName())) {
            writeSheetHolder =
                    writeWorkbookHolder.getHasBeenInitializedSheetNameMap().get(writeSheet.getSheetName());
        }
        if (writeSheetHolder == null) {
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Sheet:{},{} is already existed", writeSheet.getSheetNo(), writeSheet.getSheetName());
        }
        writeSheetHolder.setNewInitialization(Boolean.FALSE);
        writeTableHolder = null;
        currentWriteHolder = writeSheetHolder;
        if (log.isDebugEnabled()) {
            log.debug("CurrentConfiguration is writeSheetHolder");
        }
        return true;
    }

    /**
     * Initializes the current sheet holder with the provided WriteSheet.
     *
     * @param writeSheet The sheet configuration for writing.
     */
    private void initCurrentSheetHolder(WriteSheet writeSheet) {
        writeSheetHolder = new WriteSheetHolder(writeSheet, writeWorkbookHolder);
        writeTableHolder = null;
        currentWriteHolder = writeSheetHolder;
        if (log.isDebugEnabled()) {
            log.debug("CurrentConfiguration is writeSheetHolder");
        }
    }

    /**
     * Initializes the current sheet based on the write type.
     *
     * @param writeType The type of write operation.
     */
    private void initSheet(WriteTypeEnum writeType) {
        SheetWriteHandlerContext sheetWriteHandlerContext = WriteHandlerUtils.createSheetWriteHandlerContext(this);
        WriteHandlerUtils.beforeSheetCreate(sheetWriteHandlerContext);
        Sheet currentSheet;
        try {
            if (writeSheetHolder.getSheetNo() != null) {
                if (WriteTypeEnum.ADD.equals(writeType) && writeWorkbookHolder.getTempTemplateInputStream() == null) {
                    currentSheet = createSheet();
                } else {
                    currentSheet = writeWorkbookHolder.getWorkbook().getSheetAt(writeSheetHolder.getSheetNo());
                    writeSheetHolder.setCachedSheet(
                            writeWorkbookHolder.getCachedWorkbook().getSheetAt(writeSheetHolder.getSheetNo()));
                }
            } else {
                currentSheet = writeWorkbookHolder.getWorkbook().getSheet(writeSheetHolder.getSheetName());
                writeSheetHolder.setCachedSheet(
                        writeWorkbookHolder.getCachedWorkbook().getSheet(writeSheetHolder.getSheetName()));
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains(NO_SHEETS)) {
                currentSheet = createSheet();
            } else {
                throw e;
            }
        }
        if (currentSheet == null) {
            currentSheet = createSheet();
        }

        if (currentSheet != null && writeSheetHolder.getSheetNo() == null) {
            writeSheetHolder.setSheetNo(writeWorkbookHolder.getWorkbook().getSheetIndex(currentSheet));
        }

        writeSheetHolder.setSheet(currentSheet);
        WriteHandlerUtils.afterSheetCreate(sheetWriteHandlerContext);
        if (WriteTypeEnum.ADD.equals(writeType)) {
            initHead(writeSheetHolder.excelWriteHeadProperty());
        }
        writeWorkbookHolder.getHasBeenInitializedSheetIndexMap().put(writeSheetHolder.getSheetNo(), writeSheetHolder);
        writeWorkbookHolder.getHasBeenInitializedSheetNameMap().put(writeSheetHolder.getSheetName(), writeSheetHolder);
    }

    /**
     * Creates a new sheet if it does not exist.
     *
     * @return The newly created sheet.
     */
    private Sheet createSheet() {
        if (log.isDebugEnabled()) {
            log.debug("Can not find sheet:{} ,now create it", writeSheetHolder.getSheetNo());
        }
        if (StringUtils.isEmpty(writeSheetHolder.getSheetName())) {
            writeSheetHolder.setSheetName(writeSheetHolder.getSheetNo().toString());
        }
        Sheet currentSheet =
                WorkBookUtil.createSheet(writeWorkbookHolder.getWorkbook(), writeSheetHolder.getSheetName());
        writeSheetHolder.setCachedSheet(currentSheet);
        return currentSheet;
    }

    /**
     * Initializes the header for the current sheet or table.
     *
     * @param excelWriteHeadProperty The header property for writing.
     */
    public void initHead(ExcelWriteHeadProperty excelWriteHeadProperty) {
        if (!currentWriteHolder.needHead()
                || !currentWriteHolder.excelWriteHeadProperty().hasHead()) {
            return;
        }
        int newRowIndex = writeSheetHolder.getNewRowIndexAndStartDoWrite();
        newRowIndex += currentWriteHolder.relativeHeadRowIndex();
        if (currentWriteHolder.automaticMergeHead()) {
            addMergedRegionToCurrentSheet(excelWriteHeadProperty, newRowIndex);
        }
        for (int relativeRowIndex = 0, i = newRowIndex;
                i < excelWriteHeadProperty.getHeadRowNumber() + newRowIndex;
                i++, relativeRowIndex++) {

            RowWriteHandlerContext rowWriteHandlerContext =
                    WriteHandlerUtils.createRowWriteHandlerContext(this, newRowIndex, relativeRowIndex, Boolean.TRUE);
            WriteHandlerUtils.beforeRowCreate(rowWriteHandlerContext);

            Row row = WorkBookUtil.createRow(writeSheetHolder.getSheet(), i);
            rowWriteHandlerContext.setRow(row);

            WriteHandlerUtils.afterRowCreate(rowWriteHandlerContext);
            addOneRowOfHeadDataToExcel(row, i, excelWriteHeadProperty.getHeadMap(), relativeRowIndex);
            WriteHandlerUtils.afterRowDispose(rowWriteHandlerContext);
        }
    }

    /**
     * Adds merged regions to the current sheet based on the header property.
     *
     * @param excelWriteHeadProperty The header property for writing.
     * @param rowIndex               The starting row index for merging.
     */
    private void addMergedRegionToCurrentSheet(ExcelWriteHeadProperty excelWriteHeadProperty, int rowIndex) {
        for (CellRange cellRangeModel : excelWriteHeadProperty.headCellRangeList()) {
            writeSheetHolder
                    .getSheet()
                    .addMergedRegionUnsafe(new CellRangeAddress(
                            cellRangeModel.getFirstRow() + rowIndex,
                            cellRangeModel.getLastRow() + rowIndex,
                            cellRangeModel.getFirstCol(),
                            cellRangeModel.getLastCol()));
        }
    }

    /**
     * Adds one row of header data to the Excel sheet.
     *
     * @param row          The row to add header data to.
     * @param rowIndex     The row index.
     * @param headMap      The map of header data.
     * @param relativeRowIndex The relative row index for header data.
     */
    private void addOneRowOfHeadDataToExcel(
            Row row, Integer rowIndex, Map<Integer, Head> headMap, int relativeRowIndex) {
        for (Map.Entry<Integer, Head> entry : headMap.entrySet()) {
            Head head = entry.getValue();
            int columnIndex = entry.getKey();
            ExcelContentProperty excelContentProperty = ClassUtils.declaredExcelContentProperty(
                    null,
                    currentWriteHolder.excelWriteHeadProperty().getHeadClazz(),
                    head.getFieldName(),
                    currentWriteHolder);

            CellWriteHandlerContext cellWriteHandlerContext = WriteHandlerUtils.createCellWriteHandlerContext(
                    this, row, rowIndex, head, columnIndex, relativeRowIndex, Boolean.TRUE, excelContentProperty);
            WriteHandlerUtils.beforeCellCreate(cellWriteHandlerContext);

            Cell cell = row.createCell(columnIndex);
            cellWriteHandlerContext.setCell(cell);

            WriteHandlerUtils.afterCellCreate(cellWriteHandlerContext);

            WriteCellData<String> writeCellData =
                    new WriteCellData<>(head.getHeadNameList().get(relativeRowIndex));
            cell.setCellValue(writeCellData.getStringValue());
            cellWriteHandlerContext.setCellDataList(ListUtils.newArrayList(writeCellData));
            cellWriteHandlerContext.setFirstCellData(writeCellData);

            WriteHandlerUtils.afterCellDispose(cellWriteHandlerContext);
        }
    }

    /**
     * Sets the current table for writing.
     *
     * @param writeTable The table configuration for writing.
     */
    @Override
    public void currentTable(WriteTable writeTable) {
        if (writeTable == null) {
            return;
        }
        if (writeTable.getTableNo() == null || writeTable.getTableNo() <= 0) {
            writeTable.setTableNo(0);
        }
        if (writeSheetHolder.getHasBeenInitializedTable().containsKey(writeTable.getTableNo())) {
            if (log.isDebugEnabled()) {
                log.debug("Table:{} is already existed", writeTable.getTableNo());
            }
            writeTableHolder = writeSheetHolder.getHasBeenInitializedTable().get(writeTable.getTableNo());
            writeTableHolder.setNewInitialization(Boolean.FALSE);
            currentWriteHolder = writeTableHolder;
            if (log.isDebugEnabled()) {
                log.debug("CurrentConfiguration is writeTableHolder");
            }
            return;
        }

        initCurrentTableHolder(writeTable);

        // Supplementary execution for workbook and sheet handlers.
        WorkbookWriteHandlerContext workbookWriteHandlerContext =
                WriteHandlerUtils.createWorkbookWriteHandlerContext(this);
        WriteHandlerUtils.beforeWorkbookCreate(workbookWriteHandlerContext, true);
        WriteHandlerUtils.afterWorkbookCreate(workbookWriteHandlerContext, true);

        SheetWriteHandlerContext sheetWriteHandlerContext = WriteHandlerUtils.createSheetWriteHandlerContext(this);
        WriteHandlerUtils.beforeSheetCreate(sheetWriteHandlerContext, true);
        WriteHandlerUtils.afterSheetCreate(sheetWriteHandlerContext, true);

        initHead(writeTableHolder.excelWriteHeadProperty());
    }

    /**
     * Initializes the current table holder with the provided WriteTable.
     *
     * @param writeTable The table configuration for writing.
     */
    private void initCurrentTableHolder(WriteTable writeTable) {
        writeTableHolder = new WriteTableHolder(writeTable, writeSheetHolder);
        writeSheetHolder.getHasBeenInitializedTable().put(writeTable.getTableNo(), writeTableHolder);
        currentWriteHolder = writeTableHolder;
        if (log.isDebugEnabled()) {
            log.debug("CurrentConfiguration is writeTableHolder");
        }
    }

    /**
     * Retrieves the current workbook holder.
     *
     * @return The current workbook holder.
     */
    @Override
    public WriteWorkbookHolder writeWorkbookHolder() {
        return writeWorkbookHolder;
    }

    /**
     * Retrieves the current sheet holder.
     *
     * @return The current sheet holder.
     */
    @Override
    public WriteSheetHolder writeSheetHolder() {
        return writeSheetHolder;
    }

    /**
     * Retrieves the current table holder.
     *
     * @return The current table holder.
     */
    @Override
    public WriteTableHolder writeTableHolder() {
        return writeTableHolder;
    }

    /**
     * Retrieves the current write holder.
     *
     * @return The current write holder.
     */
    @Override
    public WriteHolder currentWriteHolder() {
        return currentWriteHolder;
    }

    /**
     * Finishes the write process and releases resources.
     *
     * @param onException Indicates whether the finish is triggered by an exception.
     */
    @Override
    public void finish(boolean onException) {
        if (finished) {
            return;
        }
        finished = true;
        WriteHandlerUtils.afterWorkbookDispose(writeWorkbookHolder.getWorkbookWriteHandlerContext());
        if (writeWorkbookHolder == null) {
            return;
        }
        Throwable throwable = null;
        boolean isOutputStreamEncrypt = false;
        // Determine if you need to write excel
        boolean writeExcel = !onException;
        if (writeWorkbookHolder.getWriteExcelOnException()) {
            writeExcel = Boolean.TRUE;
        }
        // No data is written if an exception is thrown
        if (writeExcel) {
            try {
                isOutputStreamEncrypt = doOutputStreamEncrypt07();
            } catch (Throwable t) {
                throwable = t;
            }
        }
        if (!isOutputStreamEncrypt) {
            try {
                if (writeExcel) {
                    writeWorkbookHolder.getWorkbook().write(writeWorkbookHolder.getOutputStream());
                }
                writeWorkbookHolder.getWorkbook().close();
            } catch (Throwable t) {
                throwable = t;
            }
        }
        try {
            Workbook workbook = writeWorkbookHolder.getWorkbook();
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        } catch (Throwable t) {
            throwable = t;
        }
        try {
            if (writeWorkbookHolder.getAutoCloseStream() && writeWorkbookHolder.getOutputStream() != null) {
                writeWorkbookHolder.getOutputStream().close();
            }
        } catch (Throwable t) {
            throwable = t;
        }
        if (writeExcel && !isOutputStreamEncrypt) {
            try {
                doFileEncrypt07();
            } catch (Throwable t) {
                throwable = t;
            }
        }
        try {
            if (writeWorkbookHolder.getTempTemplateInputStream() != null) {
                writeWorkbookHolder.getTempTemplateInputStream().close();
            }
        } catch (Throwable t) {
            throwable = t;
        }
        clearEncrypt03();
        removeThreadLocalCache();
        if (throwable != null) {
            throw new ExcelGenerateException("Can not close IO.", throwable);
        }
        if (log.isDebugEnabled()) {
            log.debug("Finished write.");
        }
    }

    /**
     * Removes thread-local caches used during the write process.
     */
    private void removeThreadLocalCache() {
        NumberDataFormatterUtils.removeThreadLocalCache();
        DateUtils.removeThreadLocalCache();
        ClassUtils.removeThreadLocalCache();
    }

    /**
     * Retrieves the current sheet being written.
     *
     * @return The current sheet.
     */
    @Override
    public Sheet getCurrentSheet() {
        return writeSheetHolder.getSheet();
    }

    /**
     * Checks if the current sheet requires a header.
     *
     * @return True if a header is required, otherwise false.
     */
    @Override
    public boolean needHead() {
        return writeSheetHolder.needHead();
    }

    /**
     * Retrieves the output stream for writing the Excel file.
     *
     * @return The output stream.
     */
    @Override
    public OutputStream getOutputStream() {
        return writeWorkbookHolder.getOutputStream();
    }

    /**
     * Retrieves the workbook being written.
     *
     * @return The workbook.
     */
    @Override
    public Workbook getWorkbook() {
        return writeWorkbookHolder.getWorkbook();
    }

    /**
     * Clears encryption settings for older Excel formats.
     */
    private void clearEncrypt03() {
        if (StringUtils.isEmpty(writeWorkbookHolder.getPassword())
                || !ExcelTypeEnum.XLS.equals(writeWorkbookHolder.getExcelType())) {
            return;
        }
        Biff8EncryptionKey.setCurrentUserPassword(null);
    }

    /**
     * Encrypts the output stream for newer Excel formats.
     *
     * @return True if encryption is successful, otherwise false.
     * @throws Exception If an error occurs during encryption.
     */
    private boolean doOutputStreamEncrypt07() throws Exception {
        if (StringUtils.isEmpty(writeWorkbookHolder.getPassword())
                || !ExcelTypeEnum.XLSX.equals(writeWorkbookHolder.getExcelType())) {
            return false;
        }
        if (writeWorkbookHolder.getFile() != null) {
            return false;
        }
        File tempXlsx = FileUtils.createTmpFile(UUID.randomUUID() + ".xlsx");
        FileOutputStream tempFileOutputStream = new FileOutputStream(tempXlsx);
        try {
            writeWorkbookHolder.getWorkbook().write(tempFileOutputStream);
        } finally {
            try {
                writeWorkbookHolder.getWorkbook().close();
                tempFileOutputStream.close();
            } catch (Exception e) {
                if (!tempXlsx.delete()) {
                    throw new ExcelGenerateException("Can not delete temp File!");
                }
                throw e;
            }
        }
        try (POIFSFileSystem fileSystem = openFileSystemAndEncrypt(tempXlsx)) {
            fileSystem.writeFilesystem(writeWorkbookHolder.getOutputStream());
        } finally {
            if (!tempXlsx.delete()) {
                throw new ExcelGenerateException("Can not delete temp File!");
            }
        }
        return true;
    }

    /**
     * To encrypt
     */
    private void doFileEncrypt07() throws Exception {
        // Check if the password is empty or the file type is not xlsx, if so, return directly
        if (StringUtils.isEmpty(writeWorkbookHolder.getPassword())
                || !ExcelTypeEnum.XLSX.equals(writeWorkbookHolder.getExcelType())) {
            return;
        }
        // Check if the file is null, if so, return directly
        if (writeWorkbookHolder.getFile() == null) {
            return;
        }
        // Use try-with-resources to automatically close resources, encrypt and write the file
        try (POIFSFileSystem fileSystem = openFileSystemAndEncrypt(writeWorkbookHolder.getFile());
                FileOutputStream fileOutputStream = new FileOutputStream(writeWorkbookHolder.getFile())) {
            fileSystem.writeFilesystem(fileOutputStream);
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
