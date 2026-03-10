---
id: 'large-file'
title: '大文件写入'
---

# 大文件写入

本章节介绍如何通过内存优化写入超大 Excel 文件（10 万行以上）。

## 概述

导出大型数据集（如数据库转储、日志分析）时，一次性加载所有行会耗尽内存。Fesod 内部使用 Apache POI 的流式 API（SXSSF），但临时 XML 文件可能会占用大量磁盘空间。启用压缩可以减少磁盘占用，但会略微增加 CPU 消耗。

## 批量写入与压缩临时文件

### 代码示例

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
        WriteSheet writeSheet = FesodSheet.writerSheet("模板").build();
        // 分批写入数据 — 每次 data() 调用返回一个批次
        for (int i = 0; i < 1000; i++) {
            excelWriter.write(data(), writeSheet);
        }
    }
}
```

---

## 架构

```text
数据（内存中，分批处理）        Fesod          POI/SXSSF
    │                              │                 │
    ├─ 100 行批次 ───────────────▶ write() ──────▶ 临时 XML（压缩）
    ├─ 100 行批次 ───────────────▶ write() ──────▶ 临时 XML（追加）
    │  ...（1000 批次）              │                 │
    └─ close() ──────────────────▶ 完成 ─────────▶ 最终 .xlsx
```

## 性能建议

- 使用 `ExcelWriter`（try-with-resources）进行批量写入，而非通过 `doWrite()` 一次性加载全部数据。
- 在磁盘受限的环境中启用临时文件压缩。
- 根据行宽和可用内存调整批次大小（此处为 100 行）。
- 监控临时目录大小：`FileUtils.getPoiFilesPath()`。

:::tip
关于大文件**读取**优化，请参阅帮助指南中的[大数据量](/docs/sheet/help/large-data)章节。
:::
