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

package org.apache.fesod.excel.temp.cache;

import com.alibaba.fastjson2.JSON;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.excel.util.FileUtils;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.junit.jupiter.api.Test;

/**
 *
 **/
@Slf4j
public class CacheTest {

    @Test
    public void cache() throws Exception {

        File readTempFile = FileUtils.createCacheTmpFile();

        File cacheFile = new File(readTempFile.getPath(), UUID.randomUUID().toString());
        PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(cacheFile))
                .withCache(
                        "cache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Integer.class,
                                HashMap.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.GB)))
                .build(true);
        Cache<Integer, HashMap> cache = persistentCacheManager.getCache("cache", Integer.class, HashMap.class);

        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(1, "test");

        cache.put(1, map);
        log.info("dd1:{}", JSON.toJSONString(cache.get(1)));

        cache.clear();

        log.info("dd2:{}", JSON.toJSONString(cache.get(1)));
    }
}
