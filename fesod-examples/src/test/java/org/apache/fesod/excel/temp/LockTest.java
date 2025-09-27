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

import com.alibaba.fastjson2.JSON;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.junit.jupiter.api.Test;

/**
 * 临时测试
 *
 *
 **/
@Slf4j
public class LockTest {

    @Test
    public void test() throws Exception {
        List<Object> list = FastExcel.read(new FileInputStream("src/test/resources/simple/simple07.xlsx"))
                .useDefaultListener(false)
                .doReadAllSync();
        for (Object data : list) {
            log.info("返回数据：{}", JSON.toJSONString(data));
        }
    }

    @Test
    public void test2() throws Exception {
        List<Object> list = FastExcel.read(new FileInputStream("src/test/resources/simple/simple07.xlsx"))
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        for (Object data : list) {
            log.info("返回数据：{}", ((Map) data).size());
            log.info("返回数据：{}", JSON.toJSONString(data));
        }
    }
}
