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

package org.apache.fesod.excel.temp.issue1662;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Test;

public class Issue1662Test {
    @Test
    public void test1662() {
        String fileName = TestFileUtil.getPath() + "Test1939" + ".xlsx";
        System.out.println(fileName);
        FastExcel.write(fileName).head(head()).sheet("模板").doWrite(dataList());
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        List<String> head1 = new ArrayList<String>();
        head0.add("xx");
        head0.add("日期");
        list.add(head0);
        head1.add("日期");
        list.add(head1);
        return list;
    }

    private List<List<Object>> dataList() {
        List<List<Object>> list = new ArrayList<List<Object>>();
        List<Object> data = new ArrayList<Object>();
        data.add("字符串");
        data.add(new Date());
        data.add(0.56);
        list.add(data);
        return list;
    }
}
