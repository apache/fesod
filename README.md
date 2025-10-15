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

# Apache Fesod (Incubating)

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/apache/fesod/ci.yml?style=flat-square&logo=github)](https://github.com/apache/fesod/actions/workflows/ci.yml)
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/apache/fesod/nightly.yml?style=flat-square&logo=github&label=nightly)](https://github.com/apache/fesod/actions/workflows/nightly.yml)
[![GitHub License](https://img.shields.io/github/license/apache/fesod?logo=apache&style=flat-square)](https://github.com/apache/fesod/blob/main/LICENSE)
![Maven Central Version](https://img.shields.io/maven-central/v/org.apache.fesod/fesod?logo=apachemaven&style=flat-square)
[![Document](https://img.shields.io/github/actions/workflow/status/apache/fesod/ci.yml?style=flat-square&logo=read-the-docs&label=Document)](https://fesod.apache.org/)
[![DeepWiki](https://img.shields.io/badge/DeepWiki-apache%2Ffesod-blue.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACwAAAAyCAYAAAAnWDnqAAAAAXNSR0IArs4c6QAAA05JREFUaEPtmUtyEzEQhtWTQyQLHNak2AB7ZnyXZMEjXMGeK/AIi+QuHrMnbChYY7MIh8g01fJoopFb0uhhEqqcbWTp06/uv1saEDv4O3n3dV60RfP947Mm9/SQc0ICFQgzfc4CYZoTPAswgSJCCUJUnAAoRHOAUOcATwbmVLWdGoH//PB8mnKqScAhsD0kYP3j/Yt5LPQe2KvcXmGvRHcDnpxfL2zOYJ1mFwrryWTz0advv1Ut4CJgf5uhDuDj5eUcAUoahrdY/56ebRWeraTjMt/00Sh3UDtjgHtQNHwcRGOC98BJEAEymycmYcWwOprTgcB6VZ5JK5TAJ+fXGLBm3FDAmn6oPPjR4rKCAoJCal2eAiQp2x0vxTPB3ALO2CRkwmDy5WohzBDwSEFKRwPbknEggCPB/imwrycgxX2NzoMCHhPkDwqYMr9tRcP5qNrMZHkVnOjRMWwLCcr8ohBVb1OMjxLwGCvjTikrsBOiA6fNyCrm8V1rP93iVPpwaE+gO0SsWmPiXB+jikdf6SizrT5qKasx5j8ABbHpFTx+vFXp9EnYQmLx02h1QTTrl6eDqxLnGjporxl3NL3agEvXdT0WmEost648sQOYAeJS9Q7bfUVoMGnjo4AZdUMQku50McDcMWcBPvr0SzbTAFDfvJqwLzgxwATnCgnp4wDl6Aa+Ax283gghmj+vj7feE2KBBRMW3FzOpLOADl0Isb5587h/U4gGvkt5v60Z1VLG8BhYjbzRwyQZemwAd6cCR5/XFWLYZRIMpX39AR0tjaGGiGzLVyhse5C9RKC6ai42ppWPKiBagOvaYk8lO7DajerabOZP46Lby5wKjw1HCRx7p9sVMOWGzb/vA1hwiWc6jm3MvQDTogQkiqIhJV0nBQBTU+3okKCFDy9WwferkHjtxib7t3xIUQtHxnIwtx4mpg26/HfwVNVDb4oI9RHmx5WGelRVlrtiw43zboCLaxv46AZeB3IlTkwouebTr1y2NjSpHz68WNFjHvupy3q8TFn3Hos2IAk4Ju5dCo8B3wP7VPr/FGaKiG+T+v+TQqIrOqMTL1VdWV1DdmcbO8KXBz6esmYWYKPwDL5b5FA1a0hwapHiom0r/cKaoqr+27/XcrS5UwSMbQAAAABJRU5ErkJggg==)](https://deepwiki.com/apache/fesod)

## Introduction

**Apache Fesod (Incubating)** is a high-performance and memory-efficient Java library for reading and writing Excel
files, designed to simplify development and ensure reliability.

Apache Fesod (Incubating) can provide developers and enterprises with great freedom and flexibility. We plan to
introduce more new features in the future to continually enhance user experience and tool usability. Apache Fesod (
Incubating) is committed to being your best choice for handling Excel files.

The name fesod(pronounced `/ˈfɛsɒd/`), an acronym for "fast easy spreadsheet and other documents" expresses the
project's origin, background and vision.

### Features

- **High-performance Reading and Writing**: Apache Fesod (Incubating) focuses on performance optimization, capable of
  efficiently handling large-scale Excel data. Compared to some traditional Excel processing libraries, it can
  significantly reduce memory consumption.
- **Simplicity and Ease of Use**: The library offers a simple and intuitive API, allowing developers to easily integrate
  it into projects, whether for simple Excel operations or complex data processing.
- **Stream Operations**: Apache Fesod (Incubating) supports stream reading, minimizing the problem of loading large
  amounts of data at once. This design is especially important when dealing with hundreds of thousands or even millions
  of rows of data.

## Installation

Apache Fesod (Incubating) requires **Java 1.8** or later. Using the latest LTS release of Java is encouraged. We
strongly recommend using the latest version of Apache Fesod (Incubating), as performance optimizations, bug fixes, and
new features in the latest version will enhance your experience.

> Currently, Apache Fesod (Incubating) uses POI as its underlying package. If your project already includes POI-related
> components, you
> will need to manually exclude POI-related jar files.


> We are currently preparing for the first release under the Apache Incubator. Current releases were non-Apache releases. You can see previous verison in this link: https://fesod.apache.org/docs/quickstart/guide/

### Maven

If you are using Maven for project building, add the following configuration in the `pom.xml` file:

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>version</version>
</dependency>
```

### Gradle

If you are using Gradle for project building, add the following configuration in the build.gradle file:

```gradle
dependencies {
    implementation 'org.apache.fesod:fesod:version'
}
```

## QuickStart

### Read

Below is an example of reading an Excel document:

```java
// Implement the ReadListener interface to set up operations for reading data
public class DemoDataListener implements ReadListener<DemoData> {

    @Override
    public void invoke(DemoData data, AnalysisContext context) {
        System.out.println("Parsed a data entry" + JSON.toJSONString(data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("All data parsed!");
    }
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // Read Excel file
    Fesod.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### Write

Below is a simple example of creating an Excel document:

```java
// Sample data class
public class DemoData {

    @ExcelProperty("String Title")
    private String string;

    @ExcelProperty("Date Title")
    private Date date;

    @ExcelProperty("Number Title")
    private Double doubleData;

    @ExcelIgnore
    private String ignore;
}

// Prepare data to write
private static List<DemoData> data() {
    List<DemoData> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        DemoData data = new DemoData();
        data.setString("String" + i);
        data.setDate(new Date());
        data.setDoubleData(0.56);
        list.add(data);
    }
    return list;
}

public static void main(String[] args) {
    String fileName = "demo.xlsx";
    // Create a "Template" sheet and write data
    Fesod.write(fileName, DemoData.class).sheet("Template").doWrite(data());
}
```

## Community

### Contributors

Contributors are welcomed to join the Apache Fesod (Incubating). Please
check [Contributing Guide](./CONTRIBUTING.md) about how to contribute to this project.

Thank you to all the people who already contributed to the Apache Fesod (Incubating) !

<a href="https://github.com/apache/fesod/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=apache/fesod"/>
</a>

> Note: Showing the first 100 contributors only due to GitHub image size limitations

### Subscribe Mailing Lists

Mail List is the most recognized form of communication in the Apache community. Contact us through the following mailing
list.

| Name                                                | Mailing list                                                                                                  |
|:----------------------------------------------------|:--------------------------------------------------------------------------------------------------------------|
| [dev@fesod.apache.org](mailto:dev@fesod.apache.org) | [Subscribe](mailto:dev-subscribe@fesod.apache.org)  ｜  [Unsubscribe](mailto:dev-unsubscribe@fesod.apache.org) |

### Star History

[![Star History Chart](https://api.star-history.com/svg?repos=apache/fesod&type=Date)](https://www.star-history.com/#apache/fesod&Date)

## License

Apache Fesod (Incubating) project is licensed under the [Apache License 2.0](LICENSE).
