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

package org.apache.fesod.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Basic binding test to verify {@link FesodProperties} is bound from environment properties.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = FesodPropertiesBindingTest.TestConfig.class,
        properties = {"fesod.global.auto-trim=true", "fesod.reader.ignore-empty-row=true", "fesod.writer.in-memory=true"
        })
class FesodPropertiesBindingTest {

    @Autowired
    private FesodProperties properties;

    @Test
    void propertiesAreBound() {
        assertNotNull(properties);
        assertNotNull(properties.getGlobal());
        assertTrue(properties.getGlobal().getAutoTrim(), "autoTrim should bind to true");
        assertNotNull(properties.getReader());
        assertTrue(properties.getReader().getIgnoreEmptyRow(), "ignoreEmptyRow should bind to true");
        assertNotNull(properties.getWriter());
        assertTrue(properties.getWriter().getInMemory(), "inMemory should bind to true");
    }

    @Configuration
    @EnableConfigurationProperties(FesodProperties.class)
    static class TestConfig {}
}
