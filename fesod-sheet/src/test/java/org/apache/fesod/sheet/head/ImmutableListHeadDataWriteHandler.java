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

package org.apache.fesod.sheet.head;

import java.util.ArrayList;
import java.util.List;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.junit.jupiter.api.Assertions;

public class ImmutableListHeadDataWriteHandler implements SheetWriteHandler {

    @Override
    public void afterSheetDispose(SheetWriteHandlerContext context) {
        List<List<String>> head = context.getWriteContext().writeSheetHolder().getHead();

        Assertions.assertInstanceOf(ArrayList.class, head);
        for (List<String> item : head) {
            Assertions.assertInstanceOf(ArrayList.class, item);
        }
    }
}
