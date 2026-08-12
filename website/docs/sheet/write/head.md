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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="3">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">String Title</td><td class="xl-head">Date Title</td><td class="xl-head">Number Title</td></tr>
<tr><td class="xl-chrome">3</td><td>String0</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">4</td><td>String1</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">5</td><td>String2</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">12</td><td>String9</td><td class="xl-num">2026-07-31 20:50:23</td><td class="xl-num">0.56</td></tr>
</tbody>
</table>
</div>

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

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Dynamic String Title</td><td class="xl-head">Dynamic Number Title</td><td class="xl-head">Dynamic Date Title</td></tr>
<tr><td class="xl-chrome">2</td><td>String0</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">3</td><td>String1</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">4</td><td>String2</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
<tr><td class="xl-chrome">⋮</td><td class="xl-muted">…</td><td class="xl-muted">…</td><td class="xl-muted">…</td></tr>
<tr><td class="xl-chrome">11</td><td>String9</td><td class="xl-num">0.56</td><td class="xl-num">2026-07-31 20:50:23</td></tr>
</tbody>
</table>
</div>

---

## Header Merge Strategy

### Overview

By default, Fesod automatically merges header cells with the same name. However, you can control the merge behavior using the `headerMergeStrategy` parameter.

### Merge Strategies

- **NONE**: No automatic merging is performed.
- **HORIZONTAL_ONLY**: Only merges cells horizontally (adjacent columns in the same header row).
- **VERTICAL_ONLY**: Only merges cells vertically (adjacent rows in the same column).
- **FULL_RECTANGLE**: Merges both directions, but only where the repeated names form a complete rectangle.
- **AUTO**: Merges in both directions (default), except that a vertical merge starting below the top header
  row also requires the cells directly above the two rows to match - see the note under the example.

Strategies only matter for a **multi-level** header, since merging is driven by the same name repeating in adjacent
cells. The example below uses a three-level header where names repeat in both directions:

### Code Example

```java
@Test
public void dynamicHeadWriteWithStrategy() {
    String fileName = "dynamicHeadWrite" + System.currentTimeMillis() + ".xlsx";

    List<List<String>> head = Arrays.asList(
        Arrays.asList("Main Title", "ID", "ID"),
        Arrays.asList("Main Title", "Group A", "Name"),
        Arrays.asList("Main Title", "Group A", "Age"),
        Arrays.asList("Main Title", "Group B", "Name"),
        Arrays.asList("Main Title", "Group B", "Age"));

    FesodSheet.write(fileName)
        .head(head)
        .headerMergeStrategy(HeaderMergeStrategy.FULL_RECTANGLE)
        .sheet()
        .doWrite(data());
}
```

Each inner list is one **column**, listing its title from the top level down. So `"Main Title"` spans all five columns in row 1,\
`"Group A"` covers columns B–C in row 2, and `"ID"` repeats down rows 2–3 of column A.

#### NONE

Every header cell stands alone:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">ID</td><td class="xl-head">Group A</td><td class="xl-head">Group A</td><td class="xl-head">Group B</td><td class="xl-head">Group B</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">ID</td><td class="xl-head">Name</td><td class="xl-head">Age</td><td class="xl-head">Name</td><td class="xl-head">Age</td></tr>
</tbody>
</table>
</div>

#### HORIZONTAL_ONLY

Merges `A1:E1`, `B2:C2`, `D2:E2`. The repeated `ID` in column A stays split:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">ID</td><td class="xl-head" colspan="2">Group A</td><td class="xl-head" colspan="2">Group B</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">ID</td><td class="xl-head">Name</td><td class="xl-head">Age</td><td class="xl-head">Name</td><td class="xl-head">Age</td></tr>
</tbody>
</table>
</div>

#### VERTICAL_ONLY

Merges only `A2:A3`; the row-spanning titles stay split:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td><td class="xl-head">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head" rowspan="2">ID</td><td class="xl-head">Group A</td><td class="xl-head">Group A</td><td class="xl-head">Group B</td><td class="xl-head">Group B</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">Name</td><td class="xl-head">Age</td><td class="xl-head">Name</td><td class="xl-head">Age</td></tr>
</tbody>
</table>
</div>

#### FULL_RECTANGLE

Merges `A1:E1`, `A2:A3`, `B2:C2`, `D2:E2`:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head" rowspan="2">ID</td><td class="xl-head" colspan="2">Group A</td><td class="xl-head" colspan="2">Group B</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">Name</td><td class="xl-head">Age</td><td class="xl-head">Name</td><td class="xl-head">Age</td></tr>
</tbody>
</table>
</div>

#### AUTO

Merges `A1:E1`, `B2:C2`, `D2:E2`, but **not** `A2:A3`:

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome">A</td><td class="xl-chrome">B</td><td class="xl-chrome">C</td><td class="xl-chrome">D</td><td class="xl-chrome">E</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head" colspan="5">Main Title</td></tr>
<tr><td class="xl-chrome">2</td><td class="xl-head">ID</td><td class="xl-head" colspan="2">Group A</td><td class="xl-head" colspan="2">Group B</td></tr>
<tr><td class="xl-chrome">3</td><td class="xl-head">ID</td><td class="xl-head">Name</td><td class="xl-head">Age</td><td class="xl-head">Name</td><td class="xl-head">Age</td></tr>
</tbody>
</table>
</div>

:::tip
`AUTO` merges horizontally exactly like `FULL_RECTANGLE`. The two differ only in **vertical** merging, where
`AUTO` looks at *where the repeated name starts*:

- **It starts in header row 1.** The cells merge, with no extra condition, because there is no row above row 1 to
  compare against. A column headed `ID` / `ID` / `ID` becomes one tall cell under `AUTO`, just as under
  `FULL_RECTANGLE`.
- **It starts further down.** Two rows merge only if the cells **directly above them** hold the same name.

The example above is the second case. `ID` repeats in rows 2 and 3, so `AUTO` compares what sits above each of them:
above row 2 is `Main Title`, above row 3 is `ID` itself. They differ, so the two cells are left separate.
`FULL_RECTANGLE` makes no such comparison and merges `A2:A3`.

The condition is applied to each pair of rows in turn, so `AUTO` can merge just *part* of a repeated run. For a
column headed `Main Title` / `B` / `B` / `B`, `AUTO` merges rows 3–4 only (both sit under a `B`) and leaves row 2
on its own, where `FULL_RECTANGLE` merges rows 2–4 into one cell.

So if a column title that repeats down the levels should become one tall cell, choose `FULL_RECTANGLE` or
`VERTICAL_ONLY` explicitly.
:::

### Common Use Cases

**Disable merging**: Use `NONE` to completely disable automatic merging:

```java
FesodSheet.write(fileName)
    .head(head)
    .headerMergeStrategy(HeaderMergeStrategy.NONE)
    .sheet()
    .doWrite(data());
```

**Note**: The old `automaticMergeHead` parameter is still supported for backward compatibility. When `headerMergeStrategy` is not set, the behavior is determined by `automaticMergeHead` (`true` → `AUTO`, `false` → `NONE`).
