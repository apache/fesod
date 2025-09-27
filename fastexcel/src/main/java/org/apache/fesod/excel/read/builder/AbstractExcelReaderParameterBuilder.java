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

package org.apache.fesod.excel.read.builder;

import java.util.Objects;
import org.apache.fesod.excel.metadata.AbstractParameterBuilder;
import org.apache.fesod.excel.read.listener.ReadListener;
import org.apache.fesod.excel.read.metadata.ReadBasicParameter;
import org.apache.fesod.excel.util.ListUtils;

/**
 * Build ExcelBuilder
 *
 *
 */
public abstract class AbstractExcelReaderParameterBuilder<
                T extends AbstractExcelReaderParameterBuilder, C extends ReadBasicParameter>
        extends AbstractParameterBuilder<T, C> {
    /**
     * Count the number of added heads when read sheet.
     *
     * <p>
     * 0 - This Sheet has no head ,since the first row are the data
     * <p>
     * 1 - This Sheet has one row head , this is the default
     * <p>
     * 2 - This Sheet has two row head ,since the third row is the data
     *
     * @param headRowNumber
     * @return
     */
    public T headRowNumber(Integer headRowNumber) {
        parameter().setHeadRowNumber(headRowNumber);
        return self();
    }

    /**
     * Whether to use scientific Format.
     *
     * default is false
     *
     * @param useScientificFormat
     * @return
     */
    public T useScientificFormat(Boolean useScientificFormat) {
        parameter().setUseScientificFormat(useScientificFormat);
        return self();
    }

    /**
     * Custom type listener run after default
     *
     * @param readListener
     * @return
     */
    public T registerReadListener(ReadListener<?> readListener) {
        if (parameter().getCustomReadListenerList() == null) {
            parameter().setCustomReadListenerList(ListUtils.newArrayList());
        }
        parameter().getCustomReadListenerList().add(readListener);
        return self();
    }

    public T registerReadListenerIfNotNull(ReadListener<?> readListener) {
        if (Objects.nonNull(readListener)) {
            if (parameter().getCustomReadListenerList() == null) {
                parameter().setCustomReadListenerList(ListUtils.newArrayList());
            }
            parameter().getCustomReadListenerList().add(readListener);
        }
        return self();
    }
}
