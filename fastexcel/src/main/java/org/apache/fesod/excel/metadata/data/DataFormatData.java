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
import org.apache.fesod.excel.util.StringUtils;

/**
 * data format
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class DataFormatData {
    /**
     * index
     */
    private Short index;

    /**
     * format
     */
    private String format;

    /**
     * The source is not empty merge the data to the target.
     *
     * @param source source
     * @param target target
     */
    public static void merge(DataFormatData source, DataFormatData target) {
        if (source == null || target == null) {
            return;
        }
        if (source.getIndex() != null) {
            target.setIndex(source.getIndex());
        }
        if (StringUtils.isNotBlank(source.getFormat())) {
            target.setFormat(source.getFormat());
        }
    }

    @Override
    public DataFormatData clone() {
        DataFormatData dataFormatData = new DataFormatData();
        dataFormatData.setIndex(getIndex());
        dataFormatData.setFormat(getFormat());
        return dataFormatData;
    }
}
