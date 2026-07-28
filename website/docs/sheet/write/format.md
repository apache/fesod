---
id: 'format'
title: 'Format'
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

# Formatting

This chapter introduces data formatting when writing data.

## Custom Format Writing

### Overview

Supports date, number, or other custom formats through annotations.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
public class ConverterData {
    @ExcelProperty(value = "String Title", converter = CustomStringStringConverter.class)
    private String string;

    @DateTimeFormat("yyyy/MM/dd HH:mm:ss")
    @ExcelProperty("Date Title")
    private Date date;

    @NumberFormat("#.##%")
    @ExcelProperty("Number Title")
    private Double doubleData;
}
```

### Code Example

```java
@Test
public void converterWrite() {
    String fileName = "converterWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ConverterData.class)
        .sheet()
        .doWrite(data());
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td><td></td></tr>
<tr><td class="xl-chrome">2</td><td>Custom: String0</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">3</td><td>Custom: String1</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">4</td><td>Custom: String2</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">5</td><td>Custom: String3</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">6</td><td>Custom: String4</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">7</td><td>Custom: String5</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">8</td><td>Custom: String6</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">9</td><td>Custom: String7</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">10</td><td>Custom: String8</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">11</td><td>Custom: String9</td><td>2024/12/03 20:50:23</td><td class="xl-num">56.%</td><td></td></tr>
<tr><td class="xl-chrome">12</td><td></td><td></td><td></td><td></td></tr>
</tbody>
</table>
