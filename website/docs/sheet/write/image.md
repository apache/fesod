---
id: 'image'
title: 'Image'
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

# Images

This chapter introduces how to export files containing images.

## How Images Are Written

An image is written as a floating picture anchored to its cell - the cell value itself stays empty.
The picture is stretched to the cell box, so its aspect ratio is not preserved: size the row and the
column to match with `@ContentRowHeight` and `@ColumnWidth`.

## Image Sources

The declared field type selects the converter, so most sources need no configuration:

| Field type | Converter | Notes |
| --- | --- | --- |
| `File` | `FileImageConverter` | A file on disk. |
| `InputStream` | `InputStreamImageConverter` | Read to the end; closing the stream stays your responsibility. |
| `byte[]`, `Byte[]` | `ByteArrayImageConverter`, `BoxingByteArrayImageConverter` | Raw image bytes. |
| `URL` | `UrlImageConverter` | Downloaded while the file is written, see [URL Sources](#url-sources). |
| `String` | none by default | Must be declared explicitly, see below. |

`String` is the only source you have to declare, because an undeclared `String` field is written as
text. Choose the converter that matches the value:

- `StringImageConverter` or `StringPathnameImageConverter` - a path to a file (the two behave identically).
- `StringBase64ImageConverter` - base64 data, with or without a `data:image/png;base64,` prefix.

## Image Export

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(100)
@ColumnWidth(25)
public class ImageDemoData {
    private File image;
}
```

### Code Example

```java
@Test
public void imageWrite() {
    String fileName = "imageWrite" + System.currentTimeMillis() + ".xlsx";
    String imagePath = "path/to/image.jpg";

    ImageDemoData data = new ImageDemoData();
    data.setImage(new File(imagePath));

    FesodSheet.write(fileName, ImageDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### Result

The column is named after the field, unless `@ExcelProperty` gives it a title.

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">image</td></tr>
<tr class="xl-rh-100"><td class="xl-chrome">2</td><td class="xl-pic xl-rh-100"><img src="/img/docs/write/sample-image.svg" alt="image"/></td></tr>
</tbody>
</table>
</div>

Switching to another source is only a change of field type - the written picture is the same:

```java
private InputStream image;   // or byte[], Byte[], URL

@ExcelProperty(converter = StringImageConverter.class)
private String image;        // String needs the converter declared
```

## Multiple Images and Text in One Cell

A `WriteCellData<Void>` field carries a list of `ImageData`, which lets one cell hold several images
alongside its text. Each image is placed with `top`/`right`/`bottom`/`left` margins in points, and
`relativeLastColumnIndex` lets an image extend into the columns to its right.

### POJO Class

```java
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(100)
@ColumnWidth(25)
public class ImageCellDemoData {
    private WriteCellData<Void> image;
}
```

### Code Example

```java
@Test
public void imageCellWrite() throws Exception {
    String fileName = "imageCellWrite" + System.currentTimeMillis() + ".xlsx";
    byte[] imageBytes = Files.readAllBytes(Paths.get("path/to/image.jpg"));

    WriteCellData<Void> writeCellData = new WriteCellData<>();
    // Use CellDataTypeEnum.EMPTY if the cell needs no text of its own
    writeCellData.setType(CellDataTypeEnum.STRING);
    writeCellData.setStringValue("Additional text content");

    List<ImageData> imageDataList = new ArrayList<>();
    writeCellData.setImageDataList(imageDataList);

    // First image: inset within the cell, kept clear of the right edge
    ImageData imageData = new ImageData();
    imageDataList.add(imageData);
    imageData.setImage(imageBytes);
    imageData.setTop(5);
    imageData.setRight(95);
    imageData.setBottom(5);
    imageData.setLeft(5);

    // Second image: starts further right and extends into the next column
    imageData = new ImageData();
    imageDataList.add(imageData);
    imageData.setImage(imageBytes);
    imageData.setTop(5);
    imageData.setRight(5);
    imageData.setBottom(5);
    imageData.setLeft(50);
    // End one column to the right of this cell, so the image covers both
    imageData.setRelativeLastColumnIndex(1);

    ImageCellDemoData data = new ImageCellDemoData();
    data.setImage(writeCellData);

    FesodSheet.write(fileName, ImageCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

The image format is detected from the data itself, so `ImageData.imageType` does not have to be set.
Margins larger than the cell can make Excel prompt to repair the file when it is opened.

### Result

Column `A` holds the text and both images; the second image overlaps column `B`.

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">image</td><td></td></tr>
<tr class="xl-rh-100"><td class="xl-chrome">2</td><td class="xl-pic-multi xl-rh-100"><img class="xl-pic-abs xl-pic-abs-left" src="/img/docs/write/sample-image.svg" alt="image"/><img class="xl-pic-abs xl-pic-abs-right" src="/img/docs/write/sample-image.svg" alt="image"/><b>Additional text content</b></td><td></td></tr>
</tbody>
</table>
</div>

## URL Sources

A `URL` field is fetched over the network while the file is written, under the following fetch policies:

- Remote fetching is disabled by default and requires an explicit exact-host allowlist;
- Configured policies support `http` and `https`;
- Host matching is case-insensitive and does not support wildcards or implicit subdomain matching;
- IPv6 literals are accepted with or without brackets; the allowlist matches hosts only and permits any URL port;
- Refuses hosts resolving to a loopback, link-local, site-local or otherwise private address;
- Revalidates the scheme and host after every redirect, follows at most 3 redirects, and reads at most 10 MB;
- The connect timeout is 1s and the read timeout 5s.

Only allowlist hosts whose DNS and HTTP services you trust. A shared or attacker-controlled host must not be
allowlisted because the host allowlist is the primary security boundary against DNS-rebinding attacks.

A refused fetch fails the write with an `IOException` naming the rule that stopped it:

```shell
URL image protocol is not allowed

Remote URL image fetching is disabled

URL image host is not allowlisted

URL image host resolves to a restricted address

URL image request exceeded redirect limit

URL image data exceeds maximum size
```

The policy is global and can be replaced:

```java
@Test
public void configureUrlImages() {
    UrlImageConverter.setFetchPolicy(UrlImageFetchPolicy.builder()
        .allowedHosts(Collections.singletonList("images.internal"))
        .allowedSchemes(SchemePolicy.HTTP_OR_HTTPS)
        .maxImageBytes(2 * 1024 * 1024)
        .maxRedirects(1)
        // allowPrivateNetwork on its own allows nothing - the host or its
        // range has to be listed as well
        .allowPrivateNetwork(true)
        .allowedPrivateHosts(Collections.singletonList("images.internal"))
        .allowedPrivateCidrs(Collections.singletonList(CidrBlock.parse("10.0.0.0/8")))
        .build());

    UrlImageConverter.urlConnectTimeout = 2000;
    UrlImageConverter.urlReadTimeout = 10000;
}
```

The policy is process-wide and should be configured during application startup. Call
`UrlImageConverter.resetFetchPolicy()` to restore the default deny policy.
