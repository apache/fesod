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

open module org.apache.fesod {
    requires java.base;
    requires java.logging;
    requires java.xml;
    requires java.sql;

    requires org.apache.commons.collections4;
    requires org.apache.commons.compress;
    requires org.apache.commons.csv;
    requires org.apache.commons.io;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.poi.poi;
    requires org.apache.xmlbeans;
    requires fastexcel.support;
    requires ehcache;
    requires org.slf4j;

    requires static lombok;

    exports org.apache.fesod.excel;
    exports org.apache.fesod.excel.analysis;
    exports org.apache.fesod.excel.analysis.csv;
    exports org.apache.fesod.excel.analysis.v03;
    exports org.apache.fesod.excel.analysis.v03.handlers;
    exports org.apache.fesod.excel.analysis.v07;
    exports org.apache.fesod.excel.analysis.v07.handlers;
    exports org.apache.fesod.excel.analysis.v07.handlers.sax;
    exports org.apache.fesod.excel.annotation;
    exports org.apache.fesod.excel.annotation.format;
    exports org.apache.fesod.excel.annotation.write.style;
    exports org.apache.fesod.excel.cache;
    exports org.apache.fesod.excel.cache.selector;
    exports org.apache.fesod.excel.constant;
    exports org.apache.fesod.excel.context;
    exports org.apache.fesod.excel.context.csv;
    exports org.apache.fesod.excel.context.xls;
    exports org.apache.fesod.excel.context.xlsx;
    exports org.apache.fesod.excel.converters;
    exports org.apache.fesod.excel.converters.bigdecimal;
    exports org.apache.fesod.excel.converters.biginteger;
    exports org.apache.fesod.excel.converters.booleanconverter;
    exports org.apache.fesod.excel.converters.bytearray;
    exports org.apache.fesod.excel.converters.byteconverter;
    exports org.apache.fesod.excel.converters.date;
    exports org.apache.fesod.excel.converters.doubleconverter;
    exports org.apache.fesod.excel.converters.file;
    exports org.apache.fesod.excel.converters.floatconverter;
    exports org.apache.fesod.excel.converters.inputstream;
    exports org.apache.fesod.excel.converters.integer;
    exports org.apache.fesod.excel.converters.localdate;
    exports org.apache.fesod.excel.converters.localdatetime;
    exports org.apache.fesod.excel.converters.longconverter;
    exports org.apache.fesod.excel.converters.shortconverter;
    exports org.apache.fesod.excel.converters.string;
    exports org.apache.fesod.excel.converters.url;
    exports org.apache.fesod.excel.enums;
    exports org.apache.fesod.excel.enums.poi;
    exports org.apache.fesod.excel.event;
    exports org.apache.fesod.excel.exception;
    exports org.apache.fesod.excel.metadata;
    exports org.apache.fesod.excel.metadata.csv;
    exports org.apache.fesod.excel.metadata.data;
    exports org.apache.fesod.excel.metadata.format;
    exports org.apache.fesod.excel.metadata.property;
    exports org.apache.fesod.excel.read.builder;
    exports org.apache.fesod.excel.read.listener;
    exports org.apache.fesod.excel.read.metadata;
    exports org.apache.fesod.excel.read.metadata.holder;
    exports org.apache.fesod.excel.read.metadata.holder.csv;
    exports org.apache.fesod.excel.read.metadata.holder.xls;
    exports org.apache.fesod.excel.read.metadata.holder.xlsx;
    exports org.apache.fesod.excel.read.metadata.property;
    exports org.apache.fesod.excel.read.processor;
    exports org.apache.fesod.excel.support;
    exports org.apache.fesod.excel.util;
    exports org.apache.fesod.excel.write;
    exports org.apache.fesod.excel.write.builder;
    exports org.apache.fesod.excel.write.executor;
    exports org.apache.fesod.excel.write.handler;
    exports org.apache.fesod.excel.write.handler.chain;
    exports org.apache.fesod.excel.write.handler.context;
    exports org.apache.fesod.excel.write.handler.impl;
    exports org.apache.fesod.excel.write.merge;
    exports org.apache.fesod.excel.write.metadata;
    exports org.apache.fesod.excel.write.metadata.fill;
    exports org.apache.fesod.excel.write.metadata.holder;
    exports org.apache.fesod.excel.write.metadata.style;
    exports org.apache.fesod.excel.write.property;
    exports org.apache.fesod.excel.write.style;
    exports org.apache.fesod.excel.write.style.column;
    exports org.apache.fesod.excel.write.style.row;
}
