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

package org.apache.fesod.excel.metadata.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.util.ListUtils;
import org.apache.fesod.excel.write.metadata.style.WriteFont;

/**
 * rich text string
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RichTextStringData {
    private String textString;
    private WriteFont writeFont;
    private List<IntervalFont> intervalFontList;

    public RichTextStringData(String textString) {
        this.textString = textString;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class IntervalFont {
        private Integer startIndex;
        private Integer endIndex;
        private WriteFont writeFont;
    }

    /**
     * Applies a font to the specified characters of a string.
     *
     * @param startIndex The start index to apply the font to (inclusive)
     * @param endIndex   The end index to apply to font to (exclusive)
     * @param writeFont  The font to use.
     */
    public void applyFont(int startIndex, int endIndex, WriteFont writeFont) {
        if (intervalFontList == null) {
            intervalFontList = ListUtils.newArrayList();
        }
        intervalFontList.add(new IntervalFont(startIndex, endIndex, writeFont));
    }

    /**
     * Sets the font of the entire string.
     *
     * @param writeFont The font to use.
     */
    public void applyFont(WriteFont writeFont) {
        this.writeFont = writeFont;
    }
}
