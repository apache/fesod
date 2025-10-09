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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * hyperlink
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class HyperlinkData extends CoordinateData {
    /**
     * Depending on the hyperlink type it can be URL, e-mail, path to a file, etc
     */
    private String address;
    /**
     * hyperlink type
     */
    private HyperlinkType hyperlinkType;

    @Getter
    public enum HyperlinkType {
        /**
         * Not a hyperlink
         */
        NONE(org.apache.poi.common.usermodel.HyperlinkType.NONE),

        /**
         * Link to an existing file or web page
         */
        URL(org.apache.poi.common.usermodel.HyperlinkType.URL),

        /**
         * Link to a place in this document
         */
        DOCUMENT(org.apache.poi.common.usermodel.HyperlinkType.DOCUMENT),

        /**
         * Link to an E-mail address
         */
        EMAIL(org.apache.poi.common.usermodel.HyperlinkType.EMAIL),

        /**
         * Link to a file
         */
        FILE(org.apache.poi.common.usermodel.HyperlinkType.FILE);

        org.apache.poi.common.usermodel.HyperlinkType value;

        HyperlinkType(org.apache.poi.common.usermodel.HyperlinkType value) {
            this.value = value;
        }
    }
}
