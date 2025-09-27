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

package org.apache.fesod.excel.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    void stripTest() {
        Assertions.assertNull(StringUtils.strip(null));
        Assertions.assertEquals("", StringUtils.strip(""));
        Assertions.assertEquals("", StringUtils.strip("   "));
        Assertions.assertEquals("abc", StringUtils.strip("abc"));
        Assertions.assertEquals("abc", StringUtils.strip("  abc"));
        Assertions.assertEquals("abc", StringUtils.strip("abc  "));
        Assertions.assertEquals("abc", StringUtils.strip(" abc "));
        Assertions.assertEquals("abc", StringUtils.strip("　abc　"));
        Assertions.assertEquals("abc", StringUtils.strip(" abc　"));
        Assertions.assertEquals("ab　c", StringUtils.strip(" ab　c　"));
        Assertions.assertEquals("ab c", StringUtils.strip(" ab c "));
    }

    @Test
    void isBlankCharTest() {
        Assertions.assertTrue(StringUtils.isBlankChar(' '));
        Assertions.assertTrue(StringUtils.isBlankChar('　'));
        Assertions.assertTrue(StringUtils.isBlankChar('\ufeff'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u202a'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u3164'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u2800'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u200c'));
        Assertions.assertTrue(StringUtils.isBlankChar('\u180e'));
    }
}
