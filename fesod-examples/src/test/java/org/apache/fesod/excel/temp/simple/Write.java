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

package org.apache.fesod.excel.temp.simple;

import com.alibaba.fastjson2.JSON;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.ExcelWriter;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.demo.write.DemoData;
import org.apache.fesod.excel.temp.large.LargeData;
import org.apache.fesod.excel.util.BeanMapUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.apache.fesod.excel.write.metadata.WriteSheet;
import org.apache.fesod.excel.write.metadata.WriteTable;
import org.junit.jupiter.api.Test;

/**
 * 测试poi
 *
 *
 **/
@Slf4j
public class Write {

    @Test
    public void simpleWrite1() {
        LargeData ss = new LargeData();
        ss.setStr23("ttt");
        Map map = BeanMapUtils.create(ss);
        System.out.println(map.containsKey("str23"));
        System.out.println(map.containsKey("str22"));
        System.out.println(map.get("str23"));
        System.out.println(map.get("str22"));
    }

    @Test
    public void simpleWrite() {
        log.info("t5");
        // 写法1
        String fileName = TestFileUtil.getPath() + "t22" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        FastExcel.write(fileName, DemoData.class)
                .relativeHeadRowIndex(10)
                .sheet("模板")
                .doWrite(data());
    }

    @Test
    public void simpleWrite2() {
        // 写法1
        String fileName = TestFileUtil.getPath() + "t22" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        FastExcel.write(fileName, WriteData.class)
                .sheet("模板")
                .registerWriteHandler(new WriteHandler())
                .doWrite(data1());
    }

    @Test
    public void simpleWrite3() {
        // 写法1
        String fileName = TestFileUtil.getPath() + "t33" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        FastExcel.write(fileName)
                .head(head())
                .inMemory(true)
                .sheet("模板")
                .registerWriteHandler(new WriteCellHandler())
                .doWrite(data1());
    }

    @Test
    public void json() {
        JsonData jsonData = new JsonData();
        jsonData.setSS1("11");
        jsonData.setSS2("22");
        jsonData.setSs3("33");
        System.out.println(JSON.toJSONString(jsonData));
    }

    @Test
    public void json3() {
        String json = "{\"SS1\":\"11\",\"sS2\":\"22\",\"ss3\":\"33\"}";

        JsonData jsonData = JSON.parseObject(json, JsonData.class);
        System.out.println(JSON.toJSONString(jsonData));
    }

    @Test
    public void tableWrite() {
        String fileName = TestFileUtil.getPath() + "tableWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里直接写多个table的案例了，如果只有一个 也可以直一行代码搞定，参照其他案例
        // 这里 需要指定写用哪个class去写
        ExcelWriter excelWriter = FastExcel.write(fileName).build();
        // 把sheet设置为不需要头 不然会输出sheet的头 这样看起来第一个table 就有2个头了
        WriteSheet writeSheet = FastExcel.writerSheet("模板").build();
        // 这里必须指定需要头，table 会继承sheet的配置，sheet配置了不需要，table 默认也是不需要
        WriteTable writeTable0 = FastExcel.writerTable(0).head(DemoData1.class).build();
        // 第一次写入会创建头
        excelWriter.write(data(), writeSheet, writeTable0);
        // 第二次写如也会创建头，然后在第一次的后面写入数据
        /// 千万别忘记close 会帮忙关闭流
        excelWriter.finish();
    }

    private List<List<String>> head() {
        List<List<String>> list = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("字符串" + System.currentTimeMillis());
        List<String> head1 = new ArrayList<String>();
        head1.add("数字" + System.currentTimeMillis());
        List<String> head2 = new ArrayList<String>();
        head2.add("日期" + System.currentTimeMillis());
        list.add(head0);
        list.add(head1);
        list.add(head2);
        return list;
    }

    private List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("640121807369666560" + i);
            data.setDate(new Date());
            data.setDoubleData(null);
            list.add(data);
        }
        return list;
    }

    private List<WriteData> data1() {
        List<WriteData> list = new ArrayList<WriteData>();
        for (int i = 0; i < 10; i++) {
            WriteData data = new WriteData();
            data.setDd(new Date());
            data.setF1(33f);
            list.add(data);
        }
        return list;
    }
}
