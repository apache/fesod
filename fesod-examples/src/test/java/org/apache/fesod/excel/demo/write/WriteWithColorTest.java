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

package org.apache.fesod.excel.demo.write;

import java.util.LinkedList;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Test;

/**
 * Class for testing colors
 *
 */
public class WriteWithColorTest {

    @Test
    public void write() {
        String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        FastExcel.write(fileName, ColorDemoData.class).sheet("模板").doWrite(this::data);
        System.out.println(fileName);
    }

    private List<ColorDemoData> data() {
        List<ColorDemoData> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new ColorDemoData("name" + i, i, "男"));
        }
        return list;
    }
}
