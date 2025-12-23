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

package org.apache.fesod.sheet.formula;

import java.io.File;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.util.TestFileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test for shared formula reading
 */
public class FormulaDataTest {

    private static File file07;

    @BeforeAll
    public static void init() {
        file07 = TestFileUtil.readFile("formula" + File.separator + "shared_formula.xlsx");
    }

    @Test
    public void t01ReadSharedFormula07() throws Exception {
        FesodSheet.read(file07, FormulaData.class, new FormulaDataListener())
                .sheet()
                .doRead();
    }
}
