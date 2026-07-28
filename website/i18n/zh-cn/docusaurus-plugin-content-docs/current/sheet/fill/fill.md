---
id: 'fill'
title: '填充'
---

# 填充

本章节介绍如何使用 Fesod 来填充数据到文件中。

## 简单填充

### 概述

基于模板文件，通过对象或 Map 填充数据到电子表格中。

### POJO 类

```java

@Getter
@Setter
@EqualsAndHashCode
public class FillData {
    private String name;
    private double number;
    private Date date;
}
```

### 代码示例

```java

@Test
public void simpleFill() {
    String templateFileName = "path/to/simple.xlsx";

    // 方案1：基于对象填充
    FillData fillData = new FillData();
    fillData.setName("张三");
    fillData.setNumber(5.2);
    FesodSheet.write("simpleFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(fillData);

    // 方案2：基于 Map 填充
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三");
    map.put("number", 5.2);
    FesodSheet.write("simpleFillMap.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(map);
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>复杂</td><td>忽略</td><td>空</td></tr>
<tr><td class="xl-chrome">2</td><td>{name}</td><td>{number}</td><td>{name}今年{number}岁了</td><td>\{name\}忽略，{name}</td><td>空{.empty}</td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>复杂</td><td>忽略</td></tr>
<tr><td class="xl-chrome">2</td><td>张三</td><td class="xl-num">5.2</td><td>张三今年5.2岁了</td><td>{name}忽略，张三</td></tr>
</tbody>
</table>

---

## 填充列表

### 概述

填充多个数据项到模板列表中，支持内存批量操作和文件缓存分批填充。

### 代码示例

```java

@Test
public void listFill() {
    String templateFileName = "path/to/list.xlsx";

    // 方案1：一次性填充所有数据
    FesodSheet.write("listFill.xlsx")
            .withTemplate(templateFileName)
            .sheet()
            .doFill(data());

    // 方案2：分批填充
    try (ExcelWriter writer = FesodSheet.write("listFillBatch.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();
        writer.fill(data(), writeSheet);
        writer.fill(data(), writeSheet);
    }
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>日期</td></tr>
<tr><td class="xl-chrome">2</td><td>{.name}</td><td>{.number}</td><td>{.date}</td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td>姓名</td><td>数字</td><td>日期</td></tr>
<tr><td class="xl-chrome">2</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">3</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">4</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">5</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">6</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">7</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">8</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">9</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">10</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">11</td><td>张三</td><td class="xl-num">5.2</td><td class="xl-num">2024-12-04 19:55:44</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
</tbody>
</table>

---

## 复杂填充

### 概述

在模板中填充多种数据类型，包括列表和普通变量。

### 代码示例

```java

@Test
public void complexFill() {
    String templateFileName = "path/to/complex.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 填充列表数据，开启 forceNewRow
        FillConfig config = FillConfig.builder().forceNewRow(true).build();
        writer.fill(data(), config, writeSheet);

        // 填充普通变量
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        map.put("total", 1000);
        writer.fill(map, writeSheet);
    }
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：{date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">{.name}</td><td class="xl-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td></td><td></td><td>统计:{total}</td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：2024年11月20日</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">15</td><td></td><td></td><td></td><td>统计:1000</td></tr>
</tbody>
</table>

---

## 大数据量填充

### 概述

优化大数据量填充性能，确保模板列表在最后一行，后续数据通过 `WriteTable` 填充。

### 代码示例

```java

@Test
public void complexFillWithTable() {
    String templateFileName = "path/to/complexFillWithTable.xlsx";

    try (ExcelWriter writer = FesodSheet.write("complexFillWithTable.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 填充列表数据
        writer.fill(data(), writeSheet);

        // 填充其他变量
        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);

        // 填充统计信息
        List<List<String>> totalList = new ArrayList<>();
        totalList.add(Arrays.asList(null, null, null, "统计: 1000"));
        writer.write(totalList, writeSheet);
    }
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：{date}</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">{.name}</td><td class="xl-green xl-num">{.number}</td><td>{.name}</td><td>{.number}</td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td></td><td></td><td>统计</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td></td><td></td><td>时间：2024年11月20日</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>数字</td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">4</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">6</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">7</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">8</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td>张三</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">14</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">15</td><td></td><td></td><td></td><td>统计: 1000</td></tr>
</tbody>
</table>

---

## 横向填充

### 概述

将列表数据横向填充，适用于动态列数场景。

### 代码示例

```java

@Test
public void horizontalFill() {
    String templateFileName = "path/to/horizontal.xlsx";

    try (ExcelWriter writer = FesodSheet.write("horizontalFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        FillConfig config = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
        writer.fill(data(), config, writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", "2024年11月20日");
        writer.fill(map, writeSheet);
    }
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-red">{.name}</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-green xl-num">{.number}</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>{.name}</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td>{.number}</td></tr>
<tr><td class="xl-chrome">5</td><td>时间：{date}</td><td></td><td></td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td><td class="xl-chrome">I</td><td class="xl-chrome">J</td><td class="xl-chrome">K</td><td class="xl-chrome">L</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td></tr>
<tr><td class="xl-chrome">5</td><td>时间：2024-12-04 20:03:48</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>

---

## 多列表组合填充

### 概述

支持多个列表同时填充，列表之间通过前缀区分。

### 代码示例

```java

@Test
public void compositeFill() {
    String templateFileName = "path/to/composite.xlsx";

    try (ExcelWriter writer = FesodSheet.write("compositeFill.xlsx").withTemplate(templateFileName).build()) {
        WriteSheet writeSheet = FesodSheet.writerSheet().build();

        // 使用 FillWrapper 进行多列表填充
        writer.fill(new FillWrapper("data1", data()), writeSheet);
        writer.fill(new FillWrapper("data2", data()), writeSheet);
        writer.fill(new FillWrapper("data3", data()), writeSheet);

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        writer.fill(map, writeSheet);
    }
}
```

### 模板

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-red">{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-green">{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>{data1.name}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td>{data1.number}</td><td></td><td></td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>时间：{date}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>姓名</td><td>数字</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">{data2.name}</td><td class="xl-green">{data2.number}</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td></td><td></td><td></td><td>姓名</td><td>数字</td></tr>
<tr><td class="xl-chrome">11</td><td></td><td></td><td></td><td class="xl-red">{data3.name}</td><td class="xl-green">{data3.number}</td></tr>
</tbody>
</table>

### 结果

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td><td class="xl-chrome">F</td><td class="xl-chrome">G</td><td class="xl-chrome">H</td></tr>
<tr><td class="xl-chrome">1</td><td rowspan="4">统计</td><td>姓名</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-red">张三</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">2</td><td>数字</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-green xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">3</td><td>姓名</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td>张三</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">4</td><td>数字</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-num">5.2</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">5</td><td></td><td>时间：2024-12-04 20:04:59</td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">6</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">7</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>姓名</td><td>数字</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">9</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">10</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td>姓名</td><td>数字</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">11</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">12</td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td class="xl-red">张三</td><td class="xl-green xl-num">5.2</td><td></td><td></td><td></td></tr>
<tr><td class="xl-chrome">13</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td class="xl-muted">…</td><td class="xl-muted">…</td><td></td><td></td><td></td></tr>
</tbody>
</table>
