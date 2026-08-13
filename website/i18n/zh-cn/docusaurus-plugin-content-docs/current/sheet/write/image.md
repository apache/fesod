---
id: 'image'
title: '图片'
---

# 图片

本章节介绍如何导出包含图片的文件。

## 图片的写出方式

图片以浮动图形的形式写出，锚定在单元格上，单元格本身的值仍然为空。图片会被拉伸到单元格大小，不保持原始比例，因此需要用 `@ContentRowHeight` 和 `@ColumnWidth` 把行高列宽调整到合适尺寸。

## 图片来源

字段声明的类型决定使用哪个转换器，因此大部分来源无需额外配置：

| 字段类型 | 转换器 | 说明 |
| --- | --- | --- |
| `File` | `FileImageConverter` | 磁盘上的文件。 |
| `InputStream` | `InputStreamImageConverter` | 会被读取到末尾，关闭流仍由调用方负责。 |
| `byte[]`、`Byte[]` | `ByteArrayImageConverter`、`BoxingByteArrayImageConverter` | 图片的原始字节。 |
| `URL` | `UrlImageConverter` | 写文件时通过网络下载，见 [URL 来源](#url-来源)。 |
| `String` | 默认没有 | 必须显式指定，见下文。 |

`String` 是唯一必须显式声明转换器的来源，否则该字段会被当作文本写出。根据取值选择对应的转换器：

- `StringImageConverter` 或 `StringPathnameImageConverter` - 文件路径（两者行为一致）。
- `StringBase64ImageConverter` - base64 数据，可以带 `data:image/png;base64,` 前缀，也可以不带。

## 图片导出

### POJO 类

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

### 代码示例

```java
void main() {
    String pathname = "path/to/demo.xlsx";
    String imagePath = "path/to/image.jpg";

    ImageDemoData data = new ImageDemoData();
    data.setImage(new File(imagePath));

    FesodSheet.write(pathname, ImageDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

### 结果

没有用 `@ExcelProperty` 指定标题时，列名就是字段名。

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">image</td></tr>
<tr class="xl-rh-100"><td class="xl-chrome">2</td><td class="xl-pic xl-rh-100"><img src="/img/docs/write/sample-image.svg" alt="图片"/></td></tr>
</tbody>
</table>
</div>

指定不同的来源方式：

```java
private InputStream image;   // 也可以是 byte[]、Byte[]、URL

@ExcelProperty(converter = StringImageConverter.class)
private String image;        // String 需要显式指定转换器
```

## 单个单元格内的多张图片与文字

`WriteCellData<Void>` 字段可以携带一个 `ImageData` 列表，从而在一个单元格内同时放置文字和多张图片。每张图片通过 `top`/`right`/`bottom`/`left` 边距（单位为磅）定位，`relativeLastColumnIndex` 可以让图片延伸到右侧的列。

### POJO 类

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

### 代码示例

```java
void main() throws Exception {
    String pathname = "path/to/demo.xlsx";

    byte[] imageBytes = Files.readAllBytes(Paths.get("path/to/image.jpg"));

    WriteCellData<Void> writeCellData = new WriteCellData<>();
    // 如果单元格不需要文字，可以设置为 CellDataTypeEnum.EMPTY
    writeCellData.setType(CellDataTypeEnum.STRING);
    writeCellData.setStringValue("额外的放一些文字");

    List<ImageData> imageDataList = new ArrayList<>();
    writeCellData.setImageDataList(imageDataList);

    // 第一张图片：位于单元格内部，并与右边缘保持距离
    ImageData imageData = new ImageData();
    imageDataList.add(imageData);
    imageData.setImage(imageBytes);
    imageData.setTop(5);
    imageData.setRight(95);
    imageData.setBottom(5);
    imageData.setLeft(5);

    // 第二张图片：起点更靠右，并延伸到下一列
    imageData = new ImageData();
    imageDataList.add(imageData);
    imageData.setImage(imageBytes);
    imageData.setTop(5);
    imageData.setRight(5);
    imageData.setBottom(5);
    imageData.setLeft(50);
    // 结束位置相对当前单元格向右移动一列，图片会同时覆盖这两个单元格
    imageData.setRelativeLastColumnIndex(1);

    ImageCellDemoData data = new ImageCellDemoData();
    data.setImage(writeCellData);

    FesodSheet.write(pathname, ImageCellDemoData.class)
        .sheet()
        .doWrite(Collections.singletonList(data));
}
```

图片格式支持自动识别，因此不需要设置 `ImageData.imageType`。
边距设置如果超过单元格大小时，打开文件可能会出现修复提示。

### 结果

`A` 列同时包含文字和两张图片，其中第二张图片覆盖到了 `B` 列。

<div class="xl-sheet-container">
<table class="xl-sheet">
<tbody>
<tr><td class="xl-chrome"></td><td class="xl-chrome xl-cw-25">A</td><td class="xl-chrome">B</td></tr>
<tr><td class="xl-chrome">1</td><td class="xl-head">image</td><td></td></tr>
<tr class="xl-rh-100"><td class="xl-chrome">2</td><td class="xl-pic-multi xl-rh-100"><img class="xl-pic-abs xl-pic-abs-left" src="/img/docs/write/sample-image.svg" alt="图片"/><img class="xl-pic-abs xl-pic-abs-right" src="/img/docs/write/sample-image.svg" alt="图片"/><b>额外的放一些文字</b></td><td></td></tr>
</tbody>
</table>
</div>

## URL 来源

写入文件时将通过 `URL` 下载，但内置了如下的一些安全策略：

- 默认只允许 `http` 和 `https`;
- 拒绝解析到回环、链路本地、站点本地等私有地址的主机;
- 最多跟随 3 次重定向，最多读取 10 MB;
- 连接超时为 1 秒，读取超时为 5 秒。

不满足上述约束时间会下载失败，且抛出自定义明细错误信息的 `IOException` 异常：

```shell
URL image protocol is not allowed

URL image host resolves to a restricted address

URL image request exceeded redirect limit

URL image data exceeds maximum size
```

支持自定义设置安全策略：

```java
void main() {
    UrlImageConverter.setFetchPolicy(UrlImageFetchPolicy.builder()
        .maxImageBytes(2 * 1024 * 1024)
        .maxRedirects(1)
        // 只设置 allowPrivateNetwork 不会放行任何地址，
        // 还需要把主机或其网段列出来
        .allowPrivateNetwork(true)
        .allowedPrivateHosts(Collections.singletonList("images.internal"))
        .allowedPrivateCidrs(Collections.singletonList(CidrBlock.parse("10.0.0.0/8")))
        .build());

    UrlImageConverter.urlConnectTimeout = 2000;
    UrlImageConverter.urlReadTimeout = 10000;
}
```

调用 `UrlImageConverter.resetFetchPolicy()` 可以恢复默认值。
