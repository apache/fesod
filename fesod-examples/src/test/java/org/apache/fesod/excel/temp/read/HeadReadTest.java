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

package org.apache.fesod.excel.temp.read;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.cache.Ehcache;
import org.junit.jupiter.api.Test;

/**
 * 临时测试
 *
 *
 **/
@Slf4j
public class HeadReadTest {

    @Test
    public void test() throws Exception {
        File file = new File("src/test/resources/cache/t2.xlsx");
        FastExcel.read(file, HeadReadData.class, new HeadListener())
                .ignoreEmptyRow(false)
                .sheet(0)
                .doRead();
    }

    @Test
    public void testCache() throws Exception {
        File file = new File("src/test/resources/cache/headt1.xls");
        FastExcel.read(file, HeadReadData.class, new HDListener())
                .readCache(new Ehcache(20))
                .sheet(0)
                .doRead();

        log.info("------------------");
        FastExcel.read(file, HeadReadData.class, new HDListener())
                .readCache(new Ehcache(20))
                .sheet(0)
                .doRead();
        log.info("------------------");
        FastExcel.read(file, HeadReadData.class, new HDListener())
                .readCache(new Ehcache(20))
                .sheet(0)
                .doRead();
    }
}
