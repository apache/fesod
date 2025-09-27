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

package org.apache.fesod.excel.temp.bug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.write.metadata.WriteSheet;

/**
 */
public class ExcelCreat {

    public static void main(String[] args) throws FileNotFoundException {
        List<DataType> data = getData();
        ExcelWriter excelWriter = null;
        excelWriter = FastExcel.write(new FileOutputStream("all.xlsx")).build();
        WriteSheet writeSheet =
                FastExcel.writerSheet(1, "test").head(HeadType.class).build();
        excelWriter.write(data, writeSheet);
        excelWriter.finish();
    }

    private static List<DataType> getData() {
        DataType vo = new DataType();
        vo.setId(738);
        vo.setFirstRemark("1222");
        vo.setSecRemark("22222");
        return Collections.singletonList(vo);
    }
}
