/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.idev.excel.test.temp.issue1663;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.enums.WriteDirectionEnum;
import cn.idev.excel.test.demo.fill.FillData;
import cn.idev.excel.test.util.TestFileUtil;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import cn.idev.excel.write.metadata.fill.FillWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class FillTest {

    @Test
    public void TestFillNullPoint() {
        String templateFileName = TestFileUtil.getPath() + "temp/issue1663" + File.separator + "template.xlsx";

        String fileName = TestFileUtil.getPath() + "temp/issue1663" + File.separator + "issue1663.xlsx";
        ExcelWriter excelWriter =
                EasyExcel.write(fileName).withTemplate(templateFileName).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig =
                FillConfig.builder().direction(WriteDirectionEnum.VERTICAL).build();
        excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);

        Map<String, Object> map = new HashMap<String, Object>();
        // Variable {date} does not exist in the template.xlsx, which should be ignored instead of reporting an error.
        map.put("date", "2019年10月9日13:28:28");
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
    }

    private List<cn.idev.excel.test.demo.fill.FillData> data() {
        List<cn.idev.excel.test.demo.fill.FillData> list = new ArrayList<cn.idev.excel.test.demo.fill.FillData>();
        for (int i = 0; i < 10; i++) {
            cn.idev.excel.test.demo.fill.FillData fillData = new FillData();
            list.add(fillData);
            fillData.setName("张三");
            fillData.setNumber(5.2);
        }
        return list;
    }
}
