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

package org.apache.fesod.excel.metadata.csv;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

/**
 * rich text string
 *
 *
 */
public class CsvRichTextString implements RichTextString {
    /**
     * string
     */
    private final String string;

    public CsvRichTextString(String string) {
        this.string = string;
    }

    @Override
    public void applyFont(int startIndex, int endIndex, short fontIndex) {}

    @Override
    public void applyFont(int startIndex, int endIndex, Font font) {}

    @Override
    public void applyFont(Font font) {}

    @Override
    public void clearFormatting() {}

    @Override
    public String getString() {
        return string;
    }

    @Override
    public int length() {
        if (string == null) {
            return 0;
        }
        return string.length();
    }

    @Override
    public int numFormattingRuns() {
        return 0;
    }

    @Override
    public int getIndexOfFormattingRun(int index) {
        return 0;
    }

    @Override
    public void applyFont(short fontIndex) {}
}
