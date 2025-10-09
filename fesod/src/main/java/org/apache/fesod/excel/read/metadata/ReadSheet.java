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

package org.apache.fesod.excel.read.metadata;

import lombok.EqualsAndHashCode;

/**
 * Read sheet
 *
 */
@EqualsAndHashCode
public class ReadSheet extends ReadBasicParameter {
    /**
     * Starting from 0
     */
    private Integer sheetNo;
    /**
     * sheet name
     */
    private String sheetName;
    /**
     * sheet hidden state
     */
    private boolean sheetHidden;
    /**
     * sheet very hidden state
     */
    private boolean sheetVeryHidden;
    /**
     * The number of rows to read, the default is all, start with 0.
     */
    public Integer numRows;

    public ReadSheet() {}

    public ReadSheet(Integer sheetNo) {
        this.sheetNo = sheetNo;
    }

    public ReadSheet(Integer sheetNo, String sheetName) {
        this.sheetNo = sheetNo;
        this.sheetName = sheetName;
    }

    public ReadSheet(Integer sheetNo, String sheetName, Integer numRows) {
        this.sheetNo = sheetNo;
        this.sheetName = sheetName;
        this.numRows = numRows;
    }

    public Integer getSheetNo() {
        return sheetNo;
    }

    public void setSheetNo(Integer sheetNo) {
        this.sheetNo = sheetNo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getNumRows() {
        return numRows;
    }

    public void setNumRows(Integer numRows) {
        this.numRows = numRows;
    }

    public boolean isHidden() {
        return sheetHidden;
    }

    public void setHidden(boolean sheetHidden) {
        this.sheetHidden = sheetHidden;
    }

    public boolean isVeryHidden() {
        return sheetVeryHidden;
    }

    public void setVeryHidden(boolean sheetVeryHidden) {
        this.sheetVeryHidden = sheetVeryHidden;
    }

    public void copyBasicParameter(ReadSheet other) {
        if (other == null) {
            return;
        }
        this.setHeadRowNumber(other.getHeadRowNumber());
        this.setCustomReadListenerList(other.getCustomReadListenerList());
        this.setHead(other.getHead());
        this.setClazz(other.getClazz());
        this.setCustomConverterList(other.getCustomConverterList());
        this.setAutoTrim(other.getAutoTrim());
        this.setAutoStrip(other.getAutoStrip());
        this.setUse1904windowing(other.getUse1904windowing());
        this.setNumRows(other.getNumRows());
        this.setHidden(other.isHidden());
        this.setVeryHidden(other.isVeryHidden());
    }

    @Override
    public String toString() {
        return "ReadSheet{" + "sheetNo=" + sheetNo + ", sheetName='" + sheetName + ", sheetHidden='" + sheetHidden
                + ", sheetVeryHidden='" + sheetVeryHidden + '\'' + "} " + super.toString();
    }
}
