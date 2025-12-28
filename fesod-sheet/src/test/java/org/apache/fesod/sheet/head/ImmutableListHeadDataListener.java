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

package org.apache.fesod.sheet.head;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.junit.jupiter.api.Assertions;

public class ImmutableListHeadDataListener implements ReadListener<Map<Integer, String>> {

    private List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Assertions.assertNotNull(context.readRowHolder().getRowIndex());
        headMap.forEach((key, value) -> {
            Assertions.assertEquals(value.getRowIndex(), context.readRowHolder().getRowIndex());
            Assertions.assertEquals(value.getColumnIndex(), key);
        });
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        List<List<String>> head = context.readSheetHolder().getHead();

        Assertions.assertInstanceOf(ArrayList.class, head);
        for (List<String> item : head) {
            Assertions.assertInstanceOf(ArrayList.class, item);
        }

        Assertions.assertEquals(1, list.size());
        Map<Integer, String> data = list.get(0);
        Assertions.assertEquals("stringData", data.get(0));
        Assertions.assertEquals("1", data.get(1));
        Assertions.assertEquals("2025-10-31 01:01:01", data.get(2));
        Assertions.assertEquals("extraData", data.get(3));
    }
}
