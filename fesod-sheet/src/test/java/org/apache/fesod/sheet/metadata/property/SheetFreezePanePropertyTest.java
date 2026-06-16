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

package org.apache.fesod.sheet.metadata.property;

import java.util.HashMap;
import java.util.Map;
import org.apache.fesod.sheet.annotation.AnnotationAttributes;
import org.apache.fesod.sheet.annotation.write.style.FreezePane;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SheetFreezePanePropertyTest {

    @Test
    void build_returnsNull_whenAnnotationIsNull() {
        Assertions.assertNull(SheetFreezePaneProperty.build((AnnotationAttributes) null));
        Assertions.assertNull(SheetFreezePaneProperty.build((FreezePane) null));
    }

    @Test
    void build_mapsColSplitAndRowSplit() {
        FreezePane annotation = FreezePaneClass.class.getAnnotation(FreezePane.class);
        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(annotation);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(2, property.getColSplit());
        Assertions.assertEquals(3, property.getRowSplit());
    }

    @Test
    void build_mapsColSplitAndRowSplit_useAnnotationAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("colSplit", 2);
        map.put("rowSplit", 3);
        map.put("leftmostColumn", -1);
        map.put("topRow", -1);
        AnnotationAttributes attr = new AnnotationAttributes(FreezePane.class, map);

        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(attr);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(2, property.getColSplit());
        Assertions.assertEquals(3, property.getRowSplit());
    }

    @Test
    void build_usesDefaults_whenLeftmostColumnAndTopRowNotSet() {
        FreezePane annotation = DefaultsClass.class.getAnnotation(FreezePane.class);
        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(annotation);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(4, property.getLeftmostColumn());
        Assertions.assertEquals(5, property.getTopRow());
    }

    @Test
    void build_usesDefaults_whenLeftmostColumnAndTopRowNotSet_useAnnotationAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("colSplit", 4);
        map.put("rowSplit", 5);
        map.put("leftmostColumn", -1);
        map.put("topRow", -1);
        AnnotationAttributes attr = new AnnotationAttributes(FreezePane.class, map);

        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(attr);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(4, property.getLeftmostColumn());
        Assertions.assertEquals(5, property.getTopRow());
    }

    @Test
    void build_usesExplicitValues_whenLeftmostColumnAndTopRowSet() {
        FreezePane annotation = ExplicitValuesClass.class.getAnnotation(FreezePane.class);
        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(annotation);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(10, property.getLeftmostColumn());
        Assertions.assertEquals(20, property.getTopRow());
    }

    @Test
    void build_usesExplicitValues_whenLeftmostColumnAndTopRowSet_useAnnotationAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("colSplit", 1);
        map.put("rowSplit", 2);
        map.put("leftmostColumn", 10);
        map.put("topRow", 20);
        AnnotationAttributes attr = new AnnotationAttributes(FreezePane.class, map);

        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(attr);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(10, property.getLeftmostColumn());
        Assertions.assertEquals(20, property.getTopRow());
    }

    @Test
    void build_zeroColSplitAndRowSplit() {
        FreezePane annotation = ZeroSplitClass.class.getAnnotation(FreezePane.class);
        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(annotation);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(0, property.getColSplit());
        Assertions.assertEquals(0, property.getRowSplit());
        // When not specified, defaults should fall back to 0 (the split values)
        Assertions.assertEquals(0, property.getLeftmostColumn());
        Assertions.assertEquals(0, property.getTopRow());
    }

    @Test
    void build_zeroColSplitAndRowSplit_useAnnotationAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("colSplit", 0);
        map.put("rowSplit", 0);
        map.put("leftmostColumn", -1);
        map.put("topRow", -1);
        AnnotationAttributes attr = new AnnotationAttributes(FreezePane.class, map);

        SheetFreezePaneProperty property = SheetFreezePaneProperty.build(attr);

        Assertions.assertNotNull(property);
        Assertions.assertEquals(0, property.getColSplit());
        Assertions.assertEquals(0, property.getRowSplit());
        // When not specified, defaults should fall back to 0 (the split values)
        Assertions.assertEquals(0, property.getLeftmostColumn());
        Assertions.assertEquals(0, property.getTopRow());
    }

    @FreezePane(colSplit = 2, rowSplit = 3)
    static class FreezePaneClass {}

    @FreezePane(colSplit = 4, rowSplit = 5)
    static class DefaultsClass {}

    @FreezePane(colSplit = 1, rowSplit = 1, leftmostColumn = 10, topRow = 20)
    static class ExplicitValuesClass {}

    @FreezePane(colSplit = 0, rowSplit = 0)
    static class ZeroSplitClass {}
}
