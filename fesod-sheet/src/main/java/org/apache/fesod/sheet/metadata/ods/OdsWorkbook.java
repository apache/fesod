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

package org.apache.fesod.sheet.metadata.ods;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.exception.ExcelGenerateException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.CellReferenceType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;

/**
 * ODS workbook implementation for writing ODS files.
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@Slf4j
public class OdsWorkbook implements Workbook {

    /**
     * ODF Spreadsheet Document
     */
    private OdfSpreadsheetDocument odfDocument;

    /**
     * true if date uses 1904 windowing, or false if using 1900 date windowing.
     * <p>
     * default is false
     */
    private Boolean use1904windowing;

    /**
     * locale
     */
    private Locale locale;

    /**
     * Whether to use scientific Format.
     * <p>
     * default is false
     */
    private Boolean useScientificFormat;

    /**
     * data format
     */
    private OdsDataFormat odsDataFormat;

    /**
     * sheets
     */
    private List<OdsSheet> odsSheetList;

    /**
     * cell styles
     */
    private List<OdsCellStyle> odsCellStyleList;

    public OdsWorkbook(Locale locale, Boolean use1904windowing, Boolean useScientificFormat) {
        this.locale = locale;
        this.use1904windowing = use1904windowing;
        this.useScientificFormat = useScientificFormat;
        this.odsSheetList = new ArrayList<>();
        this.odsCellStyleList = new ArrayList<>();

        // ODS format uses ISO 8601 date standard and does not support Excel's 1904 date windowing
        if (Boolean.TRUE.equals(use1904windowing)) {
            log.warn(
                    "ODS format does not support 1904 date windowing. The 'use1904windowing' parameter will be ignored. "
                            + "ODS uses the standard 1900 date system (ISO 8601).");
        }

        try {
            this.odfDocument = OdfSpreadsheetDocument.newSpreadsheetDocument();
            // Remove the default sheet that OdfToolkit creates
            if (odfDocument.getTableList().size() > 0) {
                odfDocument.getTableList().get(0).remove();
            }
        } catch (Exception e) {
            throw new ExcelGenerateException("Failed to create ODS document", e);
        }
    }

    @Override
    public int getActiveSheetIndex() {
        return 0;
    }

    @Override
    public void setActiveSheet(int sheetIndex) {}

    @Override
    public int getFirstVisibleTab() {
        return 0;
    }

    @Override
    public void setFirstVisibleTab(int sheetIndex) {}

    @Override
    public void setSheetOrder(String sheetname, int pos) {}

    @Override
    public void setSelectedTab(int index) {}

    @Override
    public void setSheetName(int sheet, String name) {}

    @Override
    public String getSheetName(int sheet) {
        if (sheet < 0 || sheet >= odsSheetList.size()) {
            return null;
        }
        return odsSheetList.get(sheet).getSheetName();
    }

    @Override
    public int getSheetIndex(String name) {
        for (int i = 0; i < odsSheetList.size(); i++) {
            if (name.equals(odsSheetList.get(i).getSheetName())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSheetIndex(Sheet sheet) {
        return odsSheetList.indexOf(sheet);
    }

    @Override
    public Sheet createSheet() {
        return createSheet("Sheet" + (odsSheetList.size() + 1));
    }

    @Override
    public Sheet createSheet(String sheetname) {
        OdsSheet odsSheet = new OdsSheet(this, sheetname);
        odsSheetList.add(odsSheet);
        return odsSheet;
    }

    @Override
    public Sheet cloneSheet(int sheetNum) {
        return null;
    }

    @Override
    public Iterator<Sheet> sheetIterator() {
        return (Iterator<Sheet>) (Iterator<? extends Sheet>) odsSheetList.iterator();
    }

    @Override
    public int getNumberOfSheets() {
        return odsSheetList.size();
    }

    @Override
    public Sheet getSheetAt(int index) {
        if (index < 0 || index >= odsSheetList.size()) {
            return null;
        }
        return odsSheetList.get(index);
    }

    @Override
    public Sheet getSheet(String name) {
        int index = getSheetIndex(name);
        if (index >= 0) {
            return odsSheetList.get(index);
        }
        return null;
    }

    @Override
    public void removeSheetAt(int index) {
        if (index >= 0 && index < odsSheetList.size()) {
            odsSheetList.remove(index);
        }
    }

    @Override
    public Font createFont() {
        return null;
    }

    @Override
    public Font findFont(
            boolean bold,
            short color,
            short fontHeight,
            String name,
            boolean italic,
            boolean strikeout,
            short typeOffset,
            byte underline) {
        return null;
    }

    @Override
    public int getNumberOfFonts() {
        return 0;
    }

    @Override
    public int getNumberOfFontsAsInt() {
        return 0;
    }

    @Override
    public Font getFontAt(int idx) {
        return null;
    }

    @Override
    public CellStyle createCellStyle() {
        OdsCellStyle odsCellStyle = new OdsCellStyle((short) odsCellStyleList.size());
        odsCellStyleList.add(odsCellStyle);
        return odsCellStyle;
    }

    @Override
    public int getNumCellStyles() {
        return odsCellStyleList.size();
    }

    @Override
    public CellStyle getCellStyleAt(int idx) {
        if (idx < 0 || idx >= odsCellStyleList.size()) {
            return null;
        }
        return odsCellStyleList.get(idx);
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        try {
            // Flush all sheets data to the ODF document
            for (OdsSheet sheet : odsSheetList) {
                sheet.flushToDocument();
            }
            odfDocument.save(stream);
        } catch (Exception e) {
            throw new IOException("Failed to write ODS document", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (odfDocument != null) {
            odfDocument.close();
        }
    }

    @Override
    public int getNumberOfNames() {
        return 0;
    }

    @Override
    public Name getName(String name) {
        return null;
    }

    @Override
    public List<? extends Name> getNames(String name) {
        return null;
    }

    @Override
    public List<? extends Name> getAllNames() {
        return null;
    }

    @Override
    public Name createName() {
        return null;
    }

    @Override
    public void removeName(Name name) {}

    @Override
    public int linkExternalWorkbook(String name, Workbook workbook) {
        return 0;
    }

    @Override
    public void setPrintArea(int sheetIndex, String reference) {}

    @Override
    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {}

    @Override
    public String getPrintArea(int sheetIndex) {
        return null;
    }

    @Override
    public void removePrintArea(int sheetIndex) {}

    @Override
    public MissingCellPolicy getMissingCellPolicy() {
        return null;
    }

    @Override
    public void setMissingCellPolicy(MissingCellPolicy missingCellPolicy) {}

    @Override
    public DataFormat createDataFormat() {
        if (odsDataFormat != null) {
            return odsDataFormat;
        }
        odsDataFormat = new OdsDataFormat(locale);
        return odsDataFormat;
    }

    @Override
    public int addPicture(byte[] pictureData, int format) {
        return 0;
    }

    @Override
    public List<? extends PictureData> getAllPictures() {
        return null;
    }

    @Override
    public CreationHelper getCreationHelper() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean hiddenFlag) {}

    @Override
    public boolean isSheetHidden(int sheetIx) {
        return false;
    }

    @Override
    public boolean isSheetVeryHidden(int sheetIx) {
        return false;
    }

    @Override
    public void setSheetHidden(int sheetIx, boolean hidden) {}

    @Override
    public SheetVisibility getSheetVisibility(int sheetIx) {
        return null;
    }

    @Override
    public void setSheetVisibility(int sheetIx, SheetVisibility visibility) {}

    @Override
    public void addToolPack(UDFFinder toolpack) {}

    @Override
    public void setForceFormulaRecalculation(boolean value) {}

    @Override
    public boolean getForceFormulaRecalculation() {
        return false;
    }

    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return null;
    }

    @Override
    public int addOlePackage(byte[] oleData, String label, String fileName, String command) {
        return 0;
    }

    @Override
    public EvaluationWorkbook createEvaluationWorkbook() {
        return null;
    }

    @Override
    public CellReferenceType getCellReferenceType() {
        return null;
    }

    @Override
    public void setCellReferenceType(CellReferenceType cellReferenceType) {}

    @Override
    public Iterator<Sheet> iterator() {
        return sheetIterator();
    }
}
