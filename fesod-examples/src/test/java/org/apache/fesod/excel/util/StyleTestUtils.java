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

package org.apache.fesod.excel.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class StyleTestUtils {

    public static byte[] getFillForegroundColor(Cell cell) {
        if (cell instanceof XSSFCell) {
            return ((XSSFCell) cell)
                    .getCellStyle()
                    .getFillForegroundColorColor()
                    .getRGB();
        } else {
            return short2byte(((HSSFCell) cell)
                    .getCellStyle()
                    .getFillForegroundColorColor()
                    .getTriplet());
        }
    }

    public static byte[] getFontColor(Cell cell, Workbook workbook) {
        if (cell instanceof XSSFCell) {
            return ((XSSFCell) cell).getCellStyle().getFont().getXSSFColor().getRGB();
        } else {
            return short2byte(((HSSFCell) cell)
                    .getCellStyle()
                    .getFont(workbook)
                    .getHSSFColor((HSSFWorkbook) workbook)
                    .getTriplet());
        }
    }

    public static short getFontHeightInPoints(Cell cell, Workbook workbook) {
        if (cell instanceof XSSFCell) {
            return ((XSSFCell) cell).getCellStyle().getFont().getFontHeightInPoints();
        } else {
            return ((HSSFCell) cell).getCellStyle().getFont(workbook).getFontHeightInPoints();
        }
    }

    private static byte[] short2byte(short[] shorts) {
        byte[] bytes = new byte[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            bytes[i] = (byte) shorts[i];
        }
        return bytes;
    }
}
