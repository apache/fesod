---
id: 'head'
title: 'Head'
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

# Headers

This chapter introduces how to write header data in spreadsheet.

## Complex Header Writing

### Overview

Supports setting multi-level headers by specifying main titles and subtitles through the `@ExcelProperty` annotation.

### POJO Class

```java

@Getter
@Setter
@EqualsAndHashCode
public class ComplexHeadData {
    @ExcelProperty({"Main Title", "String Title"})
    private String string;
    @ExcelProperty({"Main Title", "Date Title"})
    private Date date;
    @ExcelProperty({"Main Title", "Number Title"})
    private Double doubleData;
}
```

### Code Example

```java

@Test
public void complexHeadWrite() {
    String fileName = "complexHeadWrite" + System.currentTimeMillis() + ".xlsx";
    FesodSheet.write(fileName, ComplexHeadData.class)
            .sheet()
            .doWrite(data());
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="3">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">3</td><td>String0</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String1</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String2</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">6</td><td>String3</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">7</td><td>String4</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">8</td><td>String5</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">9</td><td>String6</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">10</td><td>String7</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">11</td><td>String8</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">12</td><td>String9</td><td class="xl-num">2024-12-03 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>

---

## Dynamic Header Writing

### Overview

Generate dynamic headers in real-time, suitable for scenarios where header content changes dynamically.

### Code Example

```java

@Test
public void dynamicHeadWrite() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
            Collections.singletonList("Dynamic String Title"),
            Collections.singletonList("Dynamic Number Title"),
            Collections.singletonList("Dynamic Date Title"));

    FesodSheet.write(fileName)
            .head(head)
            .sheet()
            .doWrite(data());
}
```

### Result

<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Dynamic String Title</td><td class="xl-head">Dynamic Number Title</td><td class="xl-head">Dynamic Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">5</td><td>String3</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">6</td><td>String4</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">7</td><td>String5</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">8</td><td>String6</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">9</td><td>String7</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">10</td><td>String8</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">0.56</td><td class="xl-num">2024-12-03 20:50:23</td></tr>
</tbody>
</table>

---

## Header Merge Strategy

### Overview

By default, Fesod automatically merges header cells with the same name. However, you can control the merge behavior using the `headerMergeStrategy` parameter.

### Merge Strategies

- **NONE**: No automatic merging is performed.
- **HORIZONTAL_ONLY**: Only merges cells horizontally (same row).
- **VERTICAL_ONLY**: Only merges cells vertically (same column).
- **FULL_RECTANGLE**: Only merges complete rectangular regions where all cells have the same name.
- **AUTO**: Automatic merging (default).

### Code Example

```java
@Test
public void dynamicHeadWriteWithStrategy() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
        Collections.singletonList("Dynamic String Title"),
        Collections.singletonList("Dynamic Number Title"),
        Collections.singletonList("Dynamic Date Title"));

    FesodSheet.write(fileName)
        .head(head)
        .headerMergeStrategy(HeaderMergeStrategy.FULL_RECTANGLE)
        .sheet()
        .doWrite(data());
}
```

### Common Use Cases

**Disable merging**: Use `NONE` to completely disable automatic merging:

```java
FesodSheet.write(fileName)
    .head(head)
    .headerMergeStrategy(HeaderMergeStrategy.NONE)
    .sheet()
    .doWrite(data());
```

**Note**: The old `automaticMergeHead` parameter is still supported for backward compatibility. When `headerMergeStrategy` is not set, the behavior is determined by `automaticMergeHead`.
