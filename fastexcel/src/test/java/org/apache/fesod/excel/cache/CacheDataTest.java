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

package org.apache.fesod.excel.cache;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.FastExcel;
import org.apache.fesod.excel.annotation.ExcelProperty;
import org.apache.fesod.excel.context.AnalysisContext;
import org.apache.fesod.excel.data.DemoData;
import org.apache.fesod.excel.enums.CacheLocationEnum;
import org.apache.fesod.excel.event.AnalysisEventListener;
import org.apache.fesod.excel.metadata.FieldCache;
import org.apache.fesod.excel.read.listener.PageReadListener;
import org.apache.fesod.excel.util.ClassUtils;
import org.apache.fesod.excel.util.FieldUtils;
import org.apache.fesod.excel.util.TestFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 *
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class CacheDataTest {

    private static File file07;
    private static File fileCacheInvoke;
    private static File fileCacheInvoke2;
    private static File fileCacheInvokeMemory;
    private static File fileCacheInvokeMemory2;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.createNewFile("cache/cache.xlsx");
        fileCacheInvoke = TestFileUtil.createNewFile("cache/fileCacheInvoke.xlsx");
        fileCacheInvoke2 = TestFileUtil.createNewFile("cache/fileCacheInvoke2.xlsx");
        fileCacheInvokeMemory = TestFileUtil.createNewFile("cache/fileCacheInvokeMemory.xlsx");
        fileCacheInvokeMemory2 = TestFileUtil.createNewFile("cache/fileCacheInvokeMemory2.xlsx");
    }

    @Test
    public void t01ReadAndWrite() throws Exception {
        Field field = FieldUtils.getField(ClassUtils.class, "FIELD_THREAD_LOCAL", true);
        ThreadLocal<Map<Class<?>, FieldCache>> fieldThreadLocal =
                (ThreadLocal<Map<Class<?>, FieldCache>>) field.get(ClassUtils.class.newInstance());
        Assertions.assertNull(fieldThreadLocal.get());
        FastExcel.write(file07, CacheData.class).sheet().doWrite(data());
        FastExcel.read(file07, CacheData.class, new PageReadListener<DemoData>(dataList -> {
                    Assertions.assertNotNull(fieldThreadLocal.get());
                }))
                .sheet()
                .doRead();
        Assertions.assertNull(fieldThreadLocal.get());
    }

    @Test
    public void t02ReadAndWriteInvoke() throws Exception {
        FastExcel.write(fileCacheInvoke, CacheInvokeData.class).sheet().doWrite(dataInvoke());
        FastExcel.read(fileCacheInvoke, CacheInvokeData.class, new AnalysisEventListener<CacheInvokeData>() {

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        Assertions.assertEquals(2, headMap.size());
                        Assertions.assertEquals("姓名", headMap.get(0));
                        Assertions.assertEquals("年龄", headMap.get(1));
                    }

                    @Override
                    public void invoke(CacheInvokeData data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();

        Field name = FieldUtils.getField(CacheInvokeData.class, "name", true);
        ExcelProperty annotation = name.getAnnotation(ExcelProperty.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
        memberValues.setAccessible(true);
        Map map = (Map) memberValues.get(invocationHandler);
        map.put("value", new String[] {"姓名2"});

        FastExcel.write(fileCacheInvoke2, CacheInvokeData.class).sheet().doWrite(dataInvoke());
        FastExcel.read(fileCacheInvoke2, CacheInvokeData.class, new AnalysisEventListener<CacheInvokeData>() {

                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        Assertions.assertEquals(2, headMap.size());
                        Assertions.assertEquals("姓名2", headMap.get(0));
                        Assertions.assertEquals("年龄", headMap.get(1));
                    }

                    @Override
                    public void invoke(CacheInvokeData data, AnalysisContext context) {}

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {}
                })
                .sheet()
                .doRead();
    }

    @Test
    public void t03ReadAndWriteInvokeMemory() throws Exception {
        FastExcel.write(fileCacheInvokeMemory, CacheInvokeMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(dataInvokeMemory());
        FastExcel.read(
                        fileCacheInvokeMemory,
                        CacheInvokeMemoryData.class,
                        new AnalysisEventListener<CacheInvokeMemoryData>() {

                            @Override
                            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                                Assertions.assertEquals(2, headMap.size());
                                Assertions.assertEquals("姓名", headMap.get(0));
                                Assertions.assertEquals("年龄", headMap.get(1));
                            }

                            @Override
                            public void invoke(CacheInvokeMemoryData data, AnalysisContext context) {}

                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {}
                        })
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doRead();

        Field name = FieldUtils.getField(CacheInvokeMemoryData.class, "name", true);
        ExcelProperty annotation = name.getAnnotation(ExcelProperty.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
        memberValues.setAccessible(true);
        Map map = (Map) memberValues.get(invocationHandler);
        map.put("value", new String[] {"姓名2"});

        FastExcel.write(fileCacheInvokeMemory2, CacheInvokeMemoryData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(dataInvokeMemory());
        FastExcel.read(
                        fileCacheInvokeMemory2,
                        CacheInvokeMemoryData.class,
                        new AnalysisEventListener<CacheInvokeMemoryData>() {

                            @Override
                            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                                Assertions.assertEquals(2, headMap.size());
                                Assertions.assertEquals("姓名", headMap.get(0));
                                Assertions.assertEquals("年龄", headMap.get(1));
                            }

                            @Override
                            public void invoke(CacheInvokeMemoryData data, AnalysisContext context) {}

                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {}
                        })
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doRead();
    }

    private List<CacheData> data() {
        List<CacheData> list = new ArrayList<CacheData>();
        for (int i = 0; i < 10; i++) {
            CacheData simpleData = new CacheData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }

    private List<CacheInvokeData> dataInvoke() {
        List<CacheInvokeData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CacheInvokeData simpleData = new CacheInvokeData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }

    private List<CacheInvokeMemoryData> dataInvokeMemory() {
        List<CacheInvokeMemoryData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CacheInvokeMemoryData simpleData = new CacheInvokeMemoryData();
            simpleData.setName("姓名" + i);
            simpleData.setAge((long) i);
            list.add(simpleData);
        }
        return list;
    }
}
