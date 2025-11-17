---
id: 'image'
title: '图片'
---

# 图片

本章节介绍如何导出包含图片的文件。

## 图片导出

### 概述

支持通过文件、流、字节数组、URL 等多种方式导出图片。

#### POJO 类

```java
@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(100)
@ColumnWidth(25)
public class ImageDemoData {
    private File file;
    private InputStream inputStream;
    @ExcelProperty(converter = StringImageConverter.class)
    private String string;
    private byte[] byteArray;
    private URL url;
}
```

#### 代码示例

```java
@Test
public void imageWrite() throws Exception {
    String fileName = "imageWrite" + System.currentTimeMillis() + ".xlsx";
    String imagePath = "path/to/image.jpg";

    List<ImageDemoData> list = new ArrayList<>();
    ImageDemoData data = new ImageDemoData();
    data.setFile(new File(imagePath));
    data.setByteArray(Files.readAllBytes(Paths.get(imagePath)));
    data.setUrl(new URL("https://example.com/image.jpg"));
    list.add(data);

    FesodSheet.write(fileName, ImageDemoData.class)
        .sheet()
        .doWrite(list);
}
```

### 结果

![img](/img/docs/write/imgWrite.png)
