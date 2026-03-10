---
id: 'introduce'
title: '介绍'
slug: /
---

# Apache Fesod (Incubating)

## 介绍

**Apache Fesod (Incubating)** 是一款高性能且内存高效的 Java 库，用于读写电子表格文件，旨在简化开发并确保可靠性。

Apache Fesod (Incubating) 能为开发者和企业提供极大的自由度与灵活性。我们计划在未来引入更多新功能，持续提升用户体验与工具实用性。Apache
Fesod (Incubating) 致力于成为您处理电子表格文件的最佳选择。

项目名称fesod（发音`/ˈfɛsɒd/`）是“fast easy spreadsheet and other documents”的缩写，体现了项目的起源、背景与愿景。

## 特性

- **高性能读写**：专注于性能优化，能够高效处理大规模电子表格数据。相较于某些传统电子表格处理库，它能显著降低内存消耗。
- **简单易用**：提供简单直观的API，无论进行基础电子表格操作还是复杂数据处理，开发者均可轻松集成至项目中。
- **流式操作**：支持流式读取，有效规避一次性加载海量数据的瓶颈。此设计在处理数十万乃至数百万行数据时尤为关键。

## 快速示例

### 读取

```java
FesodSheet.read("demo.xlsx", DemoData.class, new DemoDataListener()).sheet().doRead();
```

### 写入

```java
FesodSheet.write("demo.xlsx", DemoData.class).sheet("模板").doWrite(data());
```

:::tip
完整示例（包含 POJO 定义、监听器和数据准备），请参阅[快速开始](/docs/quickstart/simple-example)。
:::
