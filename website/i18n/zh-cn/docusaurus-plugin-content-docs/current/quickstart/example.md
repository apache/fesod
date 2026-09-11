---
id: 'simple-example'
title: '简单示例'
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

## Fesod Sheet 示例

### 读取

下面是读取电子表格文件的例子：

```java
// 实现 ReadListener 接口，设置读取数据的操作
class DemoDataListener implements ReadListener<DemoData> {
    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("解析到一条数据" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据解析完成！");
    }
}

void main() {
    String pathname = "path/to/demo.xlsx";

    // 读取文件
    FesodSheet.read(pathname, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### 写入

下面是一个创建电子表格文档的简单例子：

```java
// 示例数据类
class DemoData {
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
List<DemoData> data() {
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

void main() {
    String pathname = "path/to/demo.xlsx";

    // 创建一个名为"模板"的 sheet 页，并写入数据
    FesodSheet.write(pathname, DemoData.class).sheet("模板").doWrite(data());
}
```
