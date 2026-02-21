---
id: 'num-rows'
title: 'Rows'
---

<!--
- Licensed to the Apache Software Foundation (ASF) under one or more
- contributor license agreements.  See the NOTICE file distributed with
- this work for additional information regarding copyright ownership.
- The ASF licenses this file to You under the Apache License, Version 2.0
- (the "License"); you may not use this file except in compliance with
- the License.  You may obtain a copy of the License at
-
-   http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-->

# Reading Rows

This chapter introduces how to read a specified number of rows.

## Overview

During data analysis and processing, quickly viewing the first few rows of a spreadsheet file can help us better
understand the data structure and content.
By default, Fesod reads all data from the entire spreadsheet file.
However, by setting the `numRows` parameter, you can limit the number of rows to read. 0 means no limit on the number of
rows, i.e., read all rows. The row count includes header rows.

## All Sheets

### Code Example

```java

@Test
public void allSheetRead() {
    // Read the first 100 rows
    FesodSheet.read(fileName, DemoData.class, new PageReadListener<DemoData>(dataList -> {
        for (DemoData demoData : dataList) {
            log.info("Read one record: {}", JSON.toJSONString(demoData));
        }
    })).numRows(100).sheet().doRead();
}
```

---

## Single Sheet

### Code Example

```java

@Test
public void singleSheetRead() {
    try (ExcelReader excelReader = FesodSheet.read(fileName, DemoData.class, new DemoDataListener()).build()) {
        ReadSheet readSheet = FesodSheet.readSheet(0).build();
        readSheet.setNumRows(100); // Read the first 100 rows
        excelReader.read(readSheet);
    }
}
```
