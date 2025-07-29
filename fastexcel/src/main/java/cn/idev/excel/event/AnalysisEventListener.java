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

package cn.idev.excel.event;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.util.ConverterUtils;
import java.util.Map;

/**
 * Receives the return of each piece of data parsed
 *
 */
public abstract class AnalysisEventListener<T> implements ReadListener<T> {

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        invokeHeadMap(ConverterUtils.convertToStringMap(headMap, context), context);
    }

    /**
     * Returns the header as a map.Override the current method to receive header data.
     *
     * @param headMap
     * @param context
     */
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {}
}
