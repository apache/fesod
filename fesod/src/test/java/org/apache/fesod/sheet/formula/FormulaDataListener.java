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

import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.event.AnalysisEventListener;
import org.junit.jupiter.api.Assertions;

/**
 * Listener for formula data testing
 */
@Slf4j
public class FormulaDataListener extends AnalysisEventListener<FormulaData> {

    @Override
    public void invoke(FormulaData data, AnalysisContext context) {
        // Verify that shared formulas are read correctly
        if (data.getVolatileFormula() != null && data.getVolatileFormula().getFormulaData() != null) {
            String formula = data.getVolatileFormula().getFormulaData().getFormulaValue();
            Assertions.assertNotNull(formula);
            log.info("volatileFormula: {}", formula);
        }

        if (data.getRelativeFormula() != null && data.getRelativeFormula().getFormulaData() != null) {
            String formula = data.getRelativeFormula().getFormulaData().getFormulaValue();
            Assertions.assertNotNull(formula);
            log.info("relativeFormula: {}", formula);
        }

        if (data.getColumnAbsoluteFormula() != null
                && data.getColumnAbsoluteFormula().getFormulaData() != null) {
            String formula = data.getColumnAbsoluteFormula().getFormulaData().getFormulaValue();
            Assertions.assertNotNull(formula);
            log.info("columnAbsoluteFormula: {}", formula);
        }

        if (data.getMixedAbsoluteFormula() != null
                && data.getMixedAbsoluteFormula().getFormulaData() != null) {
            String formula = data.getMixedAbsoluteFormula().getFormulaData().getFormulaValue();
            Assertions.assertNotNull(formula);
            log.info("mixedAbsoluteFormula: {}", formula);
        }

        if (data.getAdditionFormula() != null && data.getAdditionFormula().getFormulaData() != null) {
            String formula = data.getAdditionFormula().getFormulaData().getFormulaValue();
            Assertions.assertNotNull(formula);
            log.info("additionFormula: {}", formula);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("All formula data analysed");
    }
}
