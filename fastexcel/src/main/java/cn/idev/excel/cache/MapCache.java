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

package cn.idev.excel.cache;

import cn.idev.excel.context.AnalysisContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Putting temporary data directly into a map is a little more efficient but very memory intensive
 *
 *
 */
public class MapCache implements ReadCache {

    private final List<String> cache = new ArrayList<>();

    @Override
    public void init(AnalysisContext analysisContext) {}

    @Override
    public void put(String value) {
        cache.add(value);
    }

    @Override
    public String get(Integer key) {
        if (key == null || key < 0) {
            return null;
        }
        return cache.get(key);
    }

    @Override
    public void putFinished() {}

    @Override
    public void destroy() {}
}
