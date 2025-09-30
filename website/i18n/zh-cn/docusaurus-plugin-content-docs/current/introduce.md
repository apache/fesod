---
id: 'introduce'
title: '介绍'
slug: /
---

# Apache Fesod (Incubating)

## 介绍

**Apache Fesod (Incubating)** 是一款高性能且内存高效的Java库，用于读写Excel文件，旨在简化开发并确保可靠性。

Apache Fesod (Incubating)）能为开发者和企业提供极大的自由度与灵活性。我们计划在未来引入更多新功能，持续提升用户体验与工具实用性。Apache Fesod (Incubating) 致力于成为您处理Excel文件的最佳选择。

项目名称fesod（发音`/ˈfɛsɒd/`）是“fast easy spreadsheet and other documents”的缩写，体现了项目的起源、背景与愿景。

## 特性

- **高性能读写**：专注于性能优化，能够高效处理大规模Excel数据。相较于某些传统Excel处理库，它能显著降低内存消耗。
- **简单易用**：提供简单直观的API，无论进行基础Excel操作还是复杂数据处理，开发者均可轻松集成至项目中。
- **流式操作**：支持流式读取，有效规避一次性加载海量数据的瓶颈。此设计在处理数十万乃至数百万行数据时尤为关键。

## 示例

### 读取

下面是读取 Excel 文档的例子：

```java
// 实现 ReadListener 接口，设置读取数据的操作
public class DemoDataListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("解析到一条数据" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 读取 Excel 文件
    FastExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### 写入

下面是一个创建 Excel 文档的简单例子：

```java
// 示例数据类
public class DemoData {
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    @ExcelIgnore
    private String ignore;
}

// 填充要写入的数据
private static List<DemoData> data() {
    List<DemoData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("字符串" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // 创建一个名为“模板”的 sheet 页，并写入数据
    FastExcel.write(fileName, DemoData.class).sheet("模板").doWrite(data());
}
```
