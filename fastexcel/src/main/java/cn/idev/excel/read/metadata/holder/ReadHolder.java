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

package cn.idev.excel.read.metadata.holder;

import cn.idev.excel.metadata.ConfigurationHolder;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.read.metadata.property.ExcelReadHeadProperty;
import java.util.List;

/**
 * Get the corresponding Holder
 *
 *
 **/
public interface ReadHolder extends ConfigurationHolder {

    /**
     * What handler does the currently operated cell need to execute
     *
     * @return Current {@link ReadListener}
     */
    List<ReadListener<?>> readListenerList();

    /**
     * What {@link ExcelReadHeadProperty} does the currently operated cell need to execute
     *
     * @return Current {@link ExcelReadHeadProperty}
     */
    ExcelReadHeadProperty excelReadHeadProperty();
}
