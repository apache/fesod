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

package org.apache.fesod.excel.temp;

import cn.idev.excel.support.cglib.beans.BeanMap;
import cn.idev.excel.support.cglib.core.DebuggingClassWriter;
import com.alibaba.fastjson2.JSON;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.util.BeanMapUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * 临时测试
 *
 *
 **/
@Slf4j
public class Xls03Test {

    @TempDir
    Path tempDir;

    @Test
    public void test() {
        List<Object> list = FastExcel.read("src/test/resources/compatibility/t07.xlsx")
                .sheet()
                .doReadSync();
        for (Object data : list) {
            log.info("返回数据：{}", JSON.toJSONString(data));
        }
    }

    @Test
    public void test2() {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, tempDir.toString());

        CamlData camlData = new CamlData();
        // camlData.setTest("test2");
        // camlData.setAEst("test3");
        // camlData.setTEST("test4");

        BeanMap beanMap = BeanMapUtils.create(camlData);

        log.info("test:{}", beanMap.get("test"));
        log.info("test:{}", beanMap.get("Test"));
        log.info("test:{}", beanMap.get("TEst"));
        log.info("test:{}", beanMap.get("TEST"));
    }
}
