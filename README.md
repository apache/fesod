<p align="center">
    <img src="logo.svg"/>
</p>

<p align="center">
    <a href="README.md">English</a> | <a href="README_ZH.md">中文</a> | <a href="README_JP.md">日本語</a>
</p>

<p align="center">
    <a href="https://github.com/fast-excel/fastexcel/actions/workflows/ci.yml"><img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/fast-excel/fastexcel/ci.yml?style=flat-square&logo=github"></a>
    <a href="https://github.com/fast-excel/fastexcel/actions/workflows/nightly.yml"><img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/fast-excel/fastexcel/nightly.yml?style=flat-square&logo=github&label=nightly"></a>
    <a href="https://github.com/fast-excel/fastexcel/blob/main/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/fast-excel/fastexcel?logo=apache&style=flat-square"></a>
    <a href="https://mvnrepository.com/artifact/cn.idev.excel/fastexcel"><img alt="Maven Central Version" src="https://img.shields.io/maven-central/v/cn.idev.excel/fastexcel?logo=apachemaven&style=flat-square"></a>
</p>

<p align="center">
    <a href="https://fast-excel.github.io/fastexcel/"><img alt="Document" src="https://img.shields.io/github/actions/workflow/status/fast-excel/fastexcel/ci.yml?style=flat-square&logo=read-the-docs&label=Document"></a>
    <a href="https://deepwiki.com/fast-excel/fastexcel"><img src="https://img.shields.io/badge/DeepWiki-fast--excel%2Ffastexcel-blue.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACwAAAAyCAYAAAAnWDnqAAAAAXNSR0IArs4c6QAAA05JREFUaEPtmUtyEzEQhtWTQyQLHNak2AB7ZnyXZMEjXMGeK/AIi+QuHrMnbChYY7MIh8g01fJoopFb0uhhEqqcbWTp06/uv1saEDv4O3n3dV60RfP947Mm9/SQc0ICFQgzfc4CYZoTPAswgSJCCUJUnAAoRHOAUOcATwbmVLWdGoH//PB8mnKqScAhsD0kYP3j/Yt5LPQe2KvcXmGvRHcDnpxfL2zOYJ1mFwrryWTz0advv1Ut4CJgf5uhDuDj5eUcAUoahrdY/56ebRWeraTjMt/00Sh3UDtjgHtQNHwcRGOC98BJEAEymycmYcWwOprTgcB6VZ5JK5TAJ+fXGLBm3FDAmn6oPPjR4rKCAoJCal2eAiQp2x0vxTPB3ALO2CRkwmDy5WohzBDwSEFKRwPbknEggCPB/imwrycgxX2NzoMCHhPkDwqYMr9tRcP5qNrMZHkVnOjRMWwLCcr8ohBVb1OMjxLwGCvjTikrsBOiA6fNyCrm8V1rP93iVPpwaE+gO0SsWmPiXB+jikdf6SizrT5qKasx5j8ABbHpFTx+vFXp9EnYQmLx02h1QTTrl6eDqxLnGjporxl3NL3agEvXdT0WmEost648sQOYAeJS9Q7bfUVoMGnjo4AZdUMQku50McDcMWcBPvr0SzbTAFDfvJqwLzgxwATnCgnp4wDl6Aa+Ax283gghmj+vj7feE2KBBRMW3FzOpLOADl0Isb5587h/U4gGvkt5v60Z1VLG8BhYjbzRwyQZemwAd6cCR5/XFWLYZRIMpX39AR0tjaGGiGzLVyhse5C9RKC6ai42ppWPKiBagOvaYk8lO7DajerabOZP46Lby5wKjw1HCRx7p9sVMOWGzb/vA1hwiWc6jm3MvQDTogQkiqIhJV0nBQBTU+3okKCFDy9WwferkHjtxib7t3xIUQtHxnIwtx4mpg26/HfwVNVDb4oI9RHmx5WGelRVlrtiw43zboCLaxv46AZeB3IlTkwouebTr1y2NjSpHz68WNFjHvupy3q8TFn3Hos2IAk4Ju5dCo8B3wP7VPr/FGaKiG+T+v+TQqIrOqMTL1VdWV1DdmcbO8KXBz6esmYWYKPwDL5b5FA1a0hwapHiom0r/cKaoqr+27/XcrS5UwSMbQAAAABJRU5ErkJggg==" alt="DeepWiki"></a>
    <a href="https://readmex.com/fast-excel/fastexcel"><img src="https://raw.githubusercontent.com/CodePhiliaX/resource-trusteeship/main/readmex.svg" alt="ReadmeX"></a>
</p>

## What is FastExcel

Fast, Concise, Java tool for processing Excel files that solves memory overflow issues with large files.

FastExcel will always be free and open source, use the business-friendly Apache license, making it suitable for any
commercial scenarios. This provides developers and enterprises with great freedom and flexibility.
We plan to introduce more new features in the future to continually enhance user experience and tool usability. Stay
tuned to "Programmer Xiao Lan's" public account for updates on the development of FastExcel. FastExcel is committed to
being your best choice for handling Excel files.

## Features

- **High-performance Reading and Writing**: FastExcel focuses on performance optimization, capable of efficiently
  handling large-scale Excel data. Compared to some traditional Excel processing libraries, it can significantly reduce
  memory consumption.
- **Simplicity and Ease of Use**: The library offers a simple and intuitive API, allowing developers to easily integrate
  it into projects, whether for simple Excel operations or complex data processing.
- **Stream Operations**: FastExcel supports stream reading, minimizing the problem of loading large amounts of data at
  once. This design is especially important when dealing with hundreds of thousands or even millions of rows of data.

## Installation

The following table lists the minimum Java language version requirements for each version of the FastExcel library:

| Version  | JDK Version Support Range | Notes |
|----------|:-------------------------:|-------|
| 1.3.x    |       JDK8 - JDK25        |       |
| 1.2.x    |       JDK8 - JDK21        |       |
| 1.1.x    |       JDK8 - JDK21        |       |
| 1.0.x    |       JDK8 - JDK21        |       |

We strongly recommend using the latest version of FastExcel, as performance optimizations, bug fixes, and new features
in the latest version will enhance your experience.

> Currently, FastExcel uses POI as its underlying package. If your project already includes POI-related components, you
> will need to manually exclude POI-related jar files.

### Version Update

For detailed update logs, refer to [Details of version updates](./CHANGELOG.md). You can also find all available
versions in the [Maven Central Repository](https://mvnrepository.com/artifact/cn.idev.excel/fastexcel).

### Maven

If you are using Maven for project building, add the following configuration in the `pom.xml` file:

```xml

<dependency>
    <groupId>cn.idev.excel</groupId>
    <artifactId>fastexcel</artifactId>
    <version>version</version>
</dependency>
```

### Gradle

If you are using Gradle for project building, add the following configuration in the build.gradle file:

```gradle
dependencies {
    implementation 'cn.idev.excel:fastexcel:version'
}
```

## Example

### Reading Excel Files

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
    FastExcel.read(fileName, DemoData.class, new DemoDataListener()).sheet().doRead();
}
```

### Creating Excel Files

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
    FastExcel.write(fileName, DemoData.class).sheet("Template").doWrite(data());
}
```

## Contributing

Contributors are welcomed to join the FastExcel project. Please check [Contributing Guide](./CONTRIBUTING.md) about how
to contribute to this project.

Thank you to all the people who already contributed to FastExcel!

<a href="https://github.com/fast-excel/fastexcel/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=fast-excel/fastexcel"/>
</a>

> Note: Showing the first 100 contributors only due to GitHub image size limitations

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=fast-excel/fastexcel&type=Date)](https://www.star-history.com/#fast-excel/fastexcel&Date)

## License

The project is licensed under the [Apache License 2.0](LICENSE).

