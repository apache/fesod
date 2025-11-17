---
id: 'image'
title: 'Image'
---

# Images

This chapter introduces how to export files containing images.

## Image Export

### Overview

Supports exporting images through various methods including files, streams, byte arrays, URLs, etc.

#### POJO Class

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

#### Code Example

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

### Result

![img](/img/docs/write/imgWrite.png)
