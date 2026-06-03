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

package org.apache.fesod.sheet.annotation.write;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.fesod.sheet.write.builder.AbstractExcelWriterParameterBuilder;

/**
 * Annotation used for indicating view(s) that the property
 * that is defined by field annotated is part of.
 * <p>
 * An example annotation would be:
 * <pre>
 *  &#064;ExcelView(asTypes = BasicView.class)
 *  // Or
 *  &#064;ExcelView(asNames = "BasicView")
 * </pre>
 * which would specify that field annotated would be included
 * when processing (writing) Sheet identified by <code>BasicView.class</code> (or its subclass) or
 * <code>"BasicView"</code>.
 * If multiple View class or string identifiers are included, the field will be part of all of them.
 * </p>
 *
 * @see AbstractExcelWriterParameterBuilder#groups(Class[])
 * @see AbstractExcelWriterParameterBuilder#groups(String[])
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelView {

    /**
     * View or views that annotated element is part of. Views are identified
     * by classes, and use expected class inheritance relationship: child
     * views contain all elements parent views have.
     */
    Class<?>[] asTypes() default {};

    /**
     * View or views that annotated element is part of. Views are identified
     * by strings.
     */
    String[] asNames() default {};
}
