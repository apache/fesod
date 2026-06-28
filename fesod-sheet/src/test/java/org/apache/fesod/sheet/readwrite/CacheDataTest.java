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

/*
 * This file is part of the Apache Fesod (Incubating) project, which was derived from Alibaba EasyExcel.
 *
 * Copyright (C) 2018-2024 Alibaba Group Holding Ltd.
 */

package org.apache.fesod.sheet.readwrite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;
import lombok.Getter;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.enums.CacheLocationEnum;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.apache.fesod.sheet.metadata.FieldCache;
import org.apache.fesod.sheet.read.listener.PageReadListener;
import org.apache.fesod.sheet.testkit.Tags;
import org.apache.fesod.sheet.testkit.base.AbstractExcelTest;
import org.apache.fesod.sheet.testkit.builders.TestDataBuilder;
import org.apache.fesod.sheet.testkit.enums.ExcelFormat;
import org.apache.fesod.sheet.util.ClassUtils;
import org.apache.fesod.sheet.util.FieldUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 *
 */
@Tag(Tags.ROUND_TRIP)
public class CacheDataTest extends AbstractExcelTest {

    @Test
    void clearsThreadLocalFieldCacheAfterRead() throws Exception {
        File file07 = createTempFile("cache", ExcelFormat.XLSX);
        Field field = FieldUtils.getField(ClassUtils.class, "FIELD_THREAD_LOCAL", true);
        ThreadLocal<Map<Class<?>, FieldCache>> fieldThreadLocal =
                (ThreadLocal<Map<Class<?>, FieldCache>>) field.get(ClassUtils.class.newInstance());
        assertNull(fieldThreadLocal.get());
        FesodSheet.write(file07, CacheData.class).sheet().doWrite(TestDataBuilder.cacheData(10));
        FesodSheet.read(file07, CacheData.class, new PageReadListener<CacheData>(dataList -> {
                    assertNotNull(fieldThreadLocal.get());
                }))
                .sheet()
                .doRead();
        assertNull(fieldThreadLocal.get());
    }

    @Test
    void usesUpdatedHeaderWithInvocationCache() throws Exception {
        setNameHeader("Name");
        File fileCacheInvoke = createTempFile("fileCacheInvoke", ExcelFormat.XLSX);
        File fileCacheInvoke2 = createTempFile("fileCacheInvoke2", ExcelFormat.XLSX);
        FesodSheet.write(fileCacheInvoke, CacheData.class).sheet().doWrite(TestDataBuilder.cacheData(10));
        assertHeadMap(fileCacheInvoke, "Name", null);

        setNameHeader("Name2");

        FesodSheet.write(fileCacheInvoke2, CacheData.class).sheet().doWrite(TestDataBuilder.cacheData(10));
        assertHeadMap(fileCacheInvoke2, "Name2", null);
    }

    @Test
    void reusesOriginalHeaderWithMemoryCache() throws Exception {
        setNameHeader("Name");

        File fileCacheInvokeMemory = createTempFile("fileCacheInvokeMemory", ExcelFormat.XLSX);
        File fileCacheInvokeMemory2 = createTempFile("fileCacheInvokeMemory2", ExcelFormat.XLSX);
        FesodSheet.write(fileCacheInvokeMemory, CacheData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(TestDataBuilder.cacheData(10));
        assertHeadMap(fileCacheInvokeMemory, "Name", CacheLocationEnum.MEMORY);

        setNameHeader("Name2");

        FesodSheet.write(fileCacheInvokeMemory2, CacheData.class)
                .filedCacheLocation(CacheLocationEnum.MEMORY)
                .sheet()
                .doWrite(TestDataBuilder.cacheData(10));
        assertHeadMap(fileCacheInvokeMemory2, "Name", CacheLocationEnum.MEMORY);
    }

    private void assertHeadMap(File file, String expectedFirstHead, CacheLocationEnum cacheLocation) {
        HeadCapturingListener listener = new HeadCapturingListener();

        if (cacheLocation == null) {
            FesodSheet.read(file, CacheData.class, listener).sheet().doRead();
        } else {
            FesodSheet.read(file, CacheData.class, listener)
                    .filedCacheLocation(cacheLocation)
                    .sheet()
                    .doRead();
        }

        Map<Integer, String> headMap = listener.getHeadMap();
        assertNotNull(headMap);
        assertEquals(2, headMap.size());
        assertEquals(expectedFirstHead, headMap.get(0));
        assertEquals("Age", headMap.get(1));
    }

    private void setNameHeader(String nameHeader) throws Exception {
        Field name = FieldUtils.getField(CacheData.class, "name", true);
        ExcelProperty annotation = name.getAnnotation(ExcelProperty.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Field memberValues = invocationHandler.getClass().getDeclaredField("memberValues");
        memberValues.setAccessible(true);
        Map map = (Map) memberValues.get(invocationHandler);
        map.put("value", new String[] {nameHeader});
    }

    @Getter
    private static class HeadCapturingListener extends AnalysisEventListener<CacheData> {
        private Map<Integer, String> headMap;

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            this.headMap = headMap;
        }

        @Override
        public void invoke(CacheData data, AnalysisContext context) {}

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}
    }
}
