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
