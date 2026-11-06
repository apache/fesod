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

package org.apache.fesod.sheet.analysis.v07;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.MapUtils;
import org.apache.fesod.common.util.StringUtils;
import org.apache.fesod.sheet.analysis.ExcelReadExecutor;
import org.apache.fesod.sheet.analysis.v07.handlers.sax.SharedStringsTableHandler;
import org.apache.fesod.sheet.analysis.v07.handlers.sax.XlsxRowHandler;
import org.apache.fesod.sheet.cache.ReadCache;
import org.apache.fesod.sheet.context.xlsx.XlsxReadContext;
import org.apache.fesod.sheet.enums.CellExtraTypeEnum;
import org.apache.fesod.sheet.exception.ExcelAnalysisException;
import org.apache.fesod.sheet.exception.ExcelAnalysisStopSheetException;
import org.apache.fesod.sheet.exception.ExcelCommonException;
import org.apache.fesod.sheet.metadata.CellExtra;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.xlsx.XlsxReadWorkbookHolder;
import org.apache.fesod.sheet.util.FileUtils;
import org.apache.fesod.sheet.util.SheetUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.Comments;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbookPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Streaming SAX based analyser for the XLSX (OOXML) format.
 *
 * <p>The workbook is opened as an {@link OPCPackage} and parsed part by part: the shared
 * strings table, the styles table, the workbook properties and each worksheet are handled
 * with a SAX {@link ContentHandler}, so sheet data is never fully loaded into memory.</p>
 *
 * <p>Instances are created by {@code ExcelAnalyserImpl} once the file type has been detected
 * and implement {@link ExcelReadExecutor} to expose and read the sheets of the workbook.</p>
 */
@Slf4j
public class XlsxSaxAnalyser implements ExcelReadExecutor {

    /**
     * The OPC part name of the workbook's shared strings table, {@code /xl/sharedStrings.xml}.
     */
    public static final PackagePartName SHARED_STRINGS_PART_NAME;

    static {
        try {
            SHARED_STRINGS_PART_NAME = PackagingURIHelper.createPartName("/xl/sharedStrings.xml");
        } catch (InvalidFormatException e) {
            log.error("Initialize the XlsxSaxAnalyser failure", e);
            throw new ExcelAnalysisException("Initialize the XlsxSaxAnalyser failure", e);
        }
    }

    private final XlsxReadContext xlsxReadContext;
    private final List<ReadSheet> sheetList;
    private final Map<Integer, InputStream> sheetMap;
    private final Map<String, CTSheet> ctSheetMap;
    /**
     * excel comments key: sheetNo value: CommentsTable
     */
    private final Map<Integer, CommentsTable> commentsTableMap;

    /**
     * Opens the workbook described by {@code xlsxReadContext} and prepares it for reading.
     *
     * <p>The OPC package, the shared strings cache, the styles table, the {@code 1904} date windowing
     * flag and the sheet metadata (including optional comment and hyperlink extras) are all resolved
     * here, so that a later {@link #execute()} call only has to stream the matched sheets.</p>
     *
     * @param xlsxReadContext   the read context holding the workbook configuration and event processors
     * @param decryptedStream   a stream of the decrypted workbook content, or {@code null} when the
     *                          source stream is not encrypted
     * @throws Exception if the package cannot be opened, a required part cannot be parsed, or the
     *                   workbook contains no sheet
     */
    public XlsxSaxAnalyser(XlsxReadContext xlsxReadContext, InputStream decryptedStream) throws Exception {
        this.xlsxReadContext = xlsxReadContext;
        // Initialize cache
        XlsxReadWorkbookHolder xlsxReadWorkbookHolder = xlsxReadContext.xlsxReadWorkbookHolder();

        OPCPackage pkg = readOpcPackage(xlsxReadWorkbookHolder, decryptedStream);
        xlsxReadWorkbookHolder.setOpcPackage(pkg);

        // Read the Shared information Strings
        PackagePart sharedStringsTablePackagePart = pkg.getPart(SHARED_STRINGS_PART_NAME);
        if (sharedStringsTablePackagePart != null) {
            // Specify default cache
            defaultReadCache(xlsxReadWorkbookHolder, sharedStringsTablePackagePart);

            // Analysis sharedStringsTable.xml
            analysisSharedStringsTable(sharedStringsTablePackagePart.getInputStream(), xlsxReadWorkbookHolder);
        }

        XSSFReader xssfReader = new XSSFReader(pkg);
        analysisUse1904WindowDate(xssfReader, xlsxReadWorkbookHolder);
        // set style table
        setStylesTable(xlsxReadWorkbookHolder, xssfReader);

        sheetList = new ArrayList<>();
        sheetMap = new HashMap<>();
        commentsTableMap = new HashMap<>();
        ctSheetMap = new HashMap<>();
        Map<Integer, PackageRelationshipCollection> packageRelationshipCollectionMap = MapUtils.newHashMap();
        xlsxReadWorkbookHolder.setPackageRelationshipCollectionMap(packageRelationshipCollectionMap);
        // analysis CTSheet
        analysisCtSheetMap(xssfReader, xlsxReadWorkbookHolder);

        XSSFReader.SheetIterator ite = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        int index = 0;
        if (!ite.hasNext()) {
            throw new ExcelAnalysisException("Can not find any sheet!");
        }
        while (ite.hasNext()) {
            InputStream inputStream = ite.next();
            String sheetName = ite.getSheetName();
            CTSheet ctSheet = ctSheetMap.get(sheetName);
            if (ctSheet == null) {
                continue;
            }
            ReadSheet readSheet = new ReadSheet(index, sheetName);
            readSheet.setHidden(ctSheet.getState() == STSheetState.HIDDEN);
            readSheet.setVeryHidden(ctSheet.getState() == STSheetState.VERY_HIDDEN);
            sheetList.add(readSheet);
            sheetMap.put(index, inputStream);
            if (xlsxReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.COMMENT)) {
                Comments comments = ite.getSheetComments();
                if (comments instanceof CommentsTable) {
                    commentsTableMap.put(index, (CommentsTable) comments);
                }
            }
            if (xlsxReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.HYPERLINK)) {
                PackageRelationshipCollection packageRelationshipCollection = Optional.ofNullable(ite.getSheetPart())
                        .map(packagePart -> {
                            try {
                                return packagePart.getRelationships();
                            } catch (InvalidFormatException e) {
                                log.warn("Reading the Relationship failed", e);
                                return null;
                            }
                        })
                        .orElse(null);
                if (packageRelationshipCollection != null) {
                    packageRelationshipCollectionMap.put(index, packageRelationshipCollection);
                }
            }
            index++;
        }
    }

    /**
     * Loads the workbook's styles table into the read context.
     *
     * <p>Style information is optional: when it cannot be read the failure is logged as a warning
     * and cell value analysis continues unaffected.</p>
     *
     * @param xlsxReadWorkbookHolder the workbook holder to receive the styles table
     * @param xssfReader             the POI reader used to access the styles table
     */
    private void setStylesTable(XlsxReadWorkbookHolder xlsxReadWorkbookHolder, XSSFReader xssfReader) {
        try {
            xlsxReadWorkbookHolder.setStylesTable(xssfReader.getStylesTable());
        } catch (Exception e) {
            log.warn(
                    "Currently excel cannot get style information, but it doesn't affect the data analysis.You can try to"
                            + " save the file with office again or ignore the current error.",
                    e);
        }
    }

    /**
     * Selects and initialises the cache used to store the shared strings table.
     *
     * @param xlsxReadWorkbookHolder           the workbook holder to receive the cache
     * @param sharedStringsTablePackagePart    the shared strings part, used to size the cache
     */
    private void defaultReadCache(
            XlsxReadWorkbookHolder xlsxReadWorkbookHolder, PackagePart sharedStringsTablePackagePart) {
        ReadCache readCache = xlsxReadWorkbookHolder.getReadCacheSelector().readCache(sharedStringsTablePackagePart);
        xlsxReadWorkbookHolder.setReadCache(readCache);
        readCache.init(xlsxReadContext);
    }

    /**
     * Resolves the {@code date1904} windowing flag from the workbook part.
     *
     * <p>The value already present in the read configuration (for example set explicitly by the user)
     * takes precedence and is left untouched.</p>
     *
     * @param xssfReader             the POI reader used to access the workbook part
     * @param xlsxReadWorkbookHolder the workbook holder whose global configuration is updated
     * @throws Exception if the workbook part cannot be parsed
     */
    private void analysisUse1904WindowDate(XSSFReader xssfReader, XlsxReadWorkbookHolder xlsxReadWorkbookHolder)
            throws Exception {
        if (xlsxReadWorkbookHolder.getReadWorkbook().getUse1904windowing() != null) {
            return;
        }
        InputStream workbookXml = xssfReader.getWorkbookData();
        WorkbookDocument ctWorkbook = WorkbookDocument.Factory.parse(workbookXml);
        CTWorkbook wb = ctWorkbook.getWorkbook();
        CTWorkbookPr prefix = wb.getWorkbookPr();
        if (prefix != null && prefix.getDate1904()) {
            xlsxReadWorkbookHolder.getGlobalConfiguration().setUse1904windowing(Boolean.TRUE);
        } else {
            xlsxReadWorkbookHolder.getGlobalConfiguration().setUse1904windowing(Boolean.FALSE);
        }
    }

    /**
     * Streams {@code sharedStrings.xml} into the read cache and marks the cache as fully populated.
     *
     * @param sharedStringsTableInputStream the stream of the shared strings part; closed when parsing ends
     * @param xlsxReadWorkbookHolder        the workbook holder providing the target cache
     */
    private void analysisSharedStringsTable(
            InputStream sharedStringsTableInputStream, XlsxReadWorkbookHolder xlsxReadWorkbookHolder) {
        ContentHandler handler = new SharedStringsTableHandler(xlsxReadWorkbookHolder.getReadCache());
        parseXmlSource(sharedStringsTableInputStream, handler);
        xlsxReadWorkbookHolder.getReadCache().putFinished();
    }

    /**
     * Collects the declared sheets, keyed by sheet name, from the workbook part.
     *
     * <p>Hidden and very hidden sheets are skipped when {@code ignoreHiddenSheet} is enabled, so that
     * only the sheets allowed by the read configuration are later exposed to the callers.</p>
     *
     * @param xssfReader             the POI reader used to access the workbook part
     * @param xlsxReadWorkbookHolder the workbook holder providing the hidden-sheet configuration
     * @throws Exception if the workbook part cannot be parsed
     */
    private void analysisCtSheetMap(XSSFReader xssfReader, XlsxReadWorkbookHolder xlsxReadWorkbookHolder)
            throws Exception {
        CTWorkbook wb =
                WorkbookDocument.Factory.parse(xssfReader.getWorkbookData()).getWorkbook();
        for (CTSheet ctSheet : wb.getSheets().getSheetList()) {
            boolean isHidden =
                    (ctSheet.getState() == STSheetState.HIDDEN) || (ctSheet.getState() == STSheetState.VERY_HIDDEN);
            if (Boolean.FALSE.equals(xlsxReadWorkbookHolder.getIgnoreHiddenSheet()) || !isHidden) {
                ctSheetMap.put(ctSheet.getName(), ctSheet);
            }
        }
    }

    /**
     * Opens the workbook as an {@link OPCPackage}.
     *
     * <p>When the source is a {@link File} it is opened directly. Otherwise the content is copied to a
     * temporary file first, unless the configuration forces pure {@code InputStream} usage or the whole
     * workbook can be kept in memory. The temporary file is registered on the holder so it is cleaned up
     * when the read finishes.</p>
     *
     * @param xlsxReadWorkbookHolder the workbook holder providing the source and stream options
     * @param decryptedStream        a stream of the decrypted workbook content, or {@code null} when the
     *                               source stream is not encrypted
     * @return the opened package, never {@code null}
     * @throws ExcelCommonException if the content is not a valid OOXML/zip package
     * @throws Exception            if the package cannot be opened or the temporary file cannot be written
     */
    private OPCPackage readOpcPackage(XlsxReadWorkbookHolder xlsxReadWorkbookHolder, InputStream decryptedStream)
            throws Exception {
        try {
            if (decryptedStream == null && xlsxReadWorkbookHolder.getFile() != null) {
                return OPCPackage.open(xlsxReadWorkbookHolder.getFile());
            }
            if (xlsxReadWorkbookHolder.getMandatoryUseInputStream()) {
                if (decryptedStream != null) {
                    return OPCPackage.open(decryptedStream);
                } else {
                    return OPCPackage.open(xlsxReadWorkbookHolder.getInputStream());
                }
            }
            File readTempFile = FileUtils.createCacheTmpFile();
            xlsxReadWorkbookHolder.setTempFile(readTempFile);
            File tempFile = new File(readTempFile.getPath(), UUID.randomUUID() + ".xlsx");
            if (decryptedStream != null) {
                FileUtils.writeToFile(tempFile, decryptedStream, false);
            } else {
                FileUtils.writeToFile(
                        tempFile, xlsxReadWorkbookHolder.getInputStream(), xlsxReadWorkbookHolder.getAutoCloseStream());
            }
            return OPCPackage.open(tempFile, PackageAccess.READ);
        } catch (NotOfficeXmlFileException | InvalidFormatException e) {
            // Wrap as a common, expected format error for callers/tests to handle gracefully
            throw new ExcelCommonException("Invalid OOXML/zip format: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns the sheets discovered while opening the workbook, already filtered according to the
     * hidden-sheet configuration.</p>
     */
    @Override
    public List<ReadSheet> sheetList() {
        return sheetList;
    }

    /**
     * Parses an XML part of the workbook with a secured SAX parser.
     *
     * <p>Document Type Declarations and external entities are disabled to protect against XML external
     * entity (XXE) attacks. A custom {@code SAXParserFactory} can be supplied through the workbook
     * configuration; otherwise the platform default is used.</p>
     *
     * @param inputStream the XML part to parse; closed by this method once parsing ends
     * @param handler     the content handler receiving the SAX events
     * @throws ExcelAnalysisException if the XML cannot be parsed or the stream cannot be closed
     */
    private void parseXmlSource(InputStream inputStream, ContentHandler handler) {
        InputSource inputSource = new InputSource(inputStream);
        try {
            SAXParserFactory saxFactory;
            String xlsxSAXParserFactoryName =
                    xlsxReadContext.xlsxReadWorkbookHolder().getSaxParserFactoryName();
            if (StringUtils.isEmpty(xlsxSAXParserFactoryName)) {
                saxFactory = SAXParserFactory.newInstance();
            } else {
                saxFactory = SAXParserFactory.newInstance(xlsxSAXParserFactoryName, null);
            }
            try {
                saxFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            } catch (Throwable ignore) {
            }
            try {
                saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            } catch (Throwable ignore) {
            }
            try {
                saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            } catch (Throwable ignore) {
            }
            SAXParser saxParser = saxFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);
            inputStream.close();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new ExcelAnalysisException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new ExcelAnalysisException("Can not close 'inputStream'!");
                }
            }
        }
    }

    /**
     * Reads every sheet selected by the current read configuration.
     *
     * <p>Each matched sheet is streamed through {@link XlsxRowHandler}, its comments are emitted when
     * requested, and {@code endSheet} is signalled afterwards. A listener may stop the current sheet by
     * raising {@link ExcelAnalysisStopSheetException}; this is expected and is not reported as a failure.</p>
     */
    @Override
    public void execute() {
        for (ReadSheet readSheet : sheetList) {
            readSheet = SheetUtils.match(readSheet, xlsxReadContext);
            if (readSheet != null) {
                try {
                    xlsxReadContext.currentSheet(readSheet);
                    parseXmlSource(sheetMap.get(readSheet.getSheetNo()), new XlsxRowHandler(xlsxReadContext));
                    // Read comments
                    readComments(readSheet);
                } catch (ExcelAnalysisStopSheetException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Custom stop!", e);
                    }
                }
                // The last sheet is read
                xlsxReadContext.analysisEventProcessor().endSheet(xlsxReadContext);
            }
        }
    }

    /**
     * Emits the comments of a sheet as {@link CellExtra} events.
     *
     * <p>Does nothing when comment reading was not requested or when the sheet declares no comments.</p>
     *
     * @param readSheet the sheet whose comments are read
     */
    private void readComments(ReadSheet readSheet) {
        if (!xlsxReadContext.readWorkbookHolder().getExtraReadSet().contains(CellExtraTypeEnum.COMMENT)) {
            return;
        }
        CommentsTable commentsTable = commentsTableMap.get(readSheet.getSheetNo());
        if (commentsTable == null) {
            return;
        }
        Iterator<CellAddress> cellAddresses = commentsTable.getCellAddresses();
        while (cellAddresses.hasNext()) {
            CellAddress cellAddress = cellAddresses.next();
            XSSFComment cellComment = commentsTable.findCellComment(cellAddress);
            CellExtra cellExtra = new CellExtra(
                    CellExtraTypeEnum.COMMENT,
                    cellComment.getString().toString(),
                    cellAddress.getRow(),
                    cellAddress.getColumn());
            xlsxReadContext.readSheetHolder().setCellExtra(cellExtra);
            xlsxReadContext.analysisEventProcessor().extra(xlsxReadContext);
        }
    }
}
