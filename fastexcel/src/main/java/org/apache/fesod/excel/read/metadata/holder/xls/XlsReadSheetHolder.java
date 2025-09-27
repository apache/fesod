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

package org.apache.fesod.excel.read.metadata.holder.xls;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.fesod.excel.enums.RowTypeEnum;
import org.apache.fesod.excel.read.metadata.ReadSheet;
import org.apache.fesod.excel.read.metadata.holder.ReadSheetHolder;
import org.apache.fesod.excel.read.metadata.holder.ReadWorkbookHolder;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class XlsReadSheetHolder extends ReadSheetHolder {
    /**
     * Row type.Temporary storage, last set in <code>ReadRowHolder</code>.
     */
    private RowTypeEnum tempRowType;
    /**
     * Temp object index.
     */
    private Integer tempObjectIndex;
    /**
     * Temp object index.
     */
    private Map<Integer, String> objectCacheMap;

    public XlsReadSheetHolder(ReadSheet readSheet, ReadWorkbookHolder readWorkbookHolder) {
        super(readSheet, readWorkbookHolder);
        tempRowType = RowTypeEnum.EMPTY;
        objectCacheMap = new HashMap<Integer, String>(16);
    }
}
