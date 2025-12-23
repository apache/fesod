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

package org.apache.fesod.sheet.read.metadata.holder.xlsx;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.sheet.read.metadata.ReadSheet;
import org.apache.fesod.sheet.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.sheet.read.metadata.holder.ReadWorkbookHolder;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class XlsxReadSheetHolder extends ReadSheetHolder {
    /**
     * Record the label of the current operation to prevent NPE.
     */
    private Deque<String> tagDeque;
    /**
     * Current Column
     */
    private Integer columnIndex;
    /**
     * Data for current label.
     */
    private StringBuilder tempData;
    /**
     * Formula for current label.
     */
    private StringBuilder tempFormula;
    /**
     * Formula type for current label.
     */
    private String tempFormulaType;
    /**
     * Formula shared index for current label.
     */
    private Integer tempFormulaSharedIndex;
    /**
     * Map to store master shared formulas by their shared index.
     */
    private Map<Integer, SharedFormulaInfo> sharedFormulaMap;
    /**
     * excel Relationship
     */
    private PackageRelationshipCollection packageRelationshipCollection;

    public XlsxReadSheetHolder(ReadSheet readSheet, ReadWorkbookHolder readWorkbookHolder) {
        super(readSheet, readWorkbookHolder);
        this.tagDeque = new LinkedList<String>();
        this.sharedFormulaMap = new HashMap<>();
        packageRelationshipCollection = ((XlsxReadWorkbookHolder) readWorkbookHolder)
                .getPackageRelationshipCollectionMap()
                .get(readSheet.getSheetNo());
    }

    /**
     * Information about a shared formula master cell.
     */
    @Getter
    @Setter
    public static class SharedFormulaInfo {
        /**
         * The master formula text.
         */
        private String formulaText;
        /**
         * Row index of the master formula cell.
         */
        private int firstRow;
        /**
         * Column index of the master formula cell.
         */
        private int firstCol;

        public SharedFormulaInfo(String formulaText, int firstRow, int firstCol) {
            this.formulaText = formulaText;
            this.firstRow = firstRow;
            this.firstCol = firstCol;
        }
    }
}
