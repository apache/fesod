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

package cn.idev.excel.write.metadata.holder;

import cn.idev.excel.enums.HolderEnum;
import cn.idev.excel.write.metadata.WriteTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * sheet holder
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class WriteTableHolder extends AbstractWriteHolder {
    /***
     * poi sheet
     */
    private WriteSheetHolder parentWriteSheetHolder;
    /***
     * tableNo
     */
    private Integer tableNo;
    /**
     * current table param
     */
    private WriteTable writeTable;

    public WriteTableHolder(WriteTable writeTable, WriteSheetHolder writeSheetHolder) {
        super(writeTable, writeSheetHolder);
        this.parentWriteSheetHolder = writeSheetHolder;
        this.tableNo = writeTable.getTableNo();
        this.writeTable = writeTable;

        // init handler
        initHandler(writeTable, writeSheetHolder);
    }

    @Override
    public HolderEnum holderType() {
        return HolderEnum.TABLE;
    }
}
