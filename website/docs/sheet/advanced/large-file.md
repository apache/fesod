---
id: 'large-file'
title: 'Large File Writing'
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

# Large File Writing

This chapter introduces how to write very large Excel files (100,000+ rows) with memory optimization.

## Overview

When exporting large datasets (e.g., database dumps, log analysis), loading all rows at once would exhaust memory. Fesod uses Apache POI's streaming API (SXSSF) internally, but temporary XML files can consume significant disk space. Enabling compression reduces disk usage at the cost of slightly more CPU.

## Batch Writing with Compressed Temp Files

### Code Example

```java
@Test
public void largeFileWrite() {
    String fileName = "largeFile" + System.currentTimeMillis() + ".xlsx";

    try (ExcelWriter excelWriter = FesodSheet.write(fileName, DemoData.class)
            .registerWriteHandler(new WorkbookWriteHandler() {
                @Override
                public void afterWorkbookCreate(WorkbookWriteHandlerContext context) {
                    Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
                    if (workbook instanceof SXSSFWorkbook) {
                        ((SXSSFWorkbook) workbook).setCompressTempFiles(true);
                    }
                }
            })
            .build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet("Template").build();
        // Write data in batches — each data() call returns one batch
        for (int i = 0; i < 1000; i++) {
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

---

## Architecture

```text
Data (in memory, batched)        Fesod          POI/SXSSF
    │                              │                 │
    ├─ 100 rows batch ───────────▶ write() ──────▶ temp XML (compressed)
    ├─ 100 rows batch ───────────▶ write() ──────▶ temp XML (append)
    │  ... (1000 batches)           │                 │
    └─ close() ──────────────────▶ finalize ─────▶ final .xlsx
```

## Performance Tips

- Use `ExcelWriter` (try-with-resources) for batch writing instead of loading all data with `doWrite()`.
- Enable temp file compression for disk-constrained environments.
- Tune batch size (100 rows here) based on your row width and available memory.
- Monitor temp directory size: `FileUtils.getPoiFilesPath()`.

:::tip
For large file **reading** optimization, see the [Large Data](/docs/sheet/help/large-data) section in the Help guide.
:::
