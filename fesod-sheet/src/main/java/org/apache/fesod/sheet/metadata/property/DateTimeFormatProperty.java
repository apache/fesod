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

package org.apache.fesod.sheet.metadata.property;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.fesod.common.util.BooleanUtils;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.apache.fesod.sheet.enums.BooleanEnum;

/**
 * Configuration from annotations
 *
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public class DateTimeFormatProperty {
    private String format;
    private Boolean use1904windowing;

    public DateTimeFormatProperty(String format, Boolean use1904windowing) {
        this.format = format;
        this.use1904windowing = use1904windowing;
    }

    public static DateTimeFormatProperty build(AnnotationAttributes attributes) {
        if (attributes == null) {
            return null;
        }
        if (!attributes.isAnnotationTypeEqual(DateTimeFormat.class)) {
            throw new IllegalArgumentException(String.format(
                    "DateTimeFormatProperty only support DateTimeFormat annotation" + ", but currently provides '%s'",
                    attributes.getAnnotationType()));
        }
        return new DateTimeFormatProperty(
                attributes.getRequiredAttribute("value", String.class),
                BooleanUtils.isTrue(attributes
                        .getRequiredAttribute("use1904windowing", BooleanEnum.class)
                        .getBooleanValue()));
    }

    /**
     * @see DateTimeFormatProperty#build(AnnotationAttributes)
     */
    @Deprecated
    public static DateTimeFormatProperty build(DateTimeFormat dateTimeFormat) {
        if (dateTimeFormat == null) {
            return null;
        }
        return new DateTimeFormatProperty(
                dateTimeFormat.value(),
                BooleanUtils.isTrue(dateTimeFormat.use1904windowing().getBooleanValue()));
    }
}
