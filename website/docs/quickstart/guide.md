---
id: 'guide'
title: 'Guide'
---

# Installation

## Compatibility Information

The following table lists the minimum Java language version requirements for each version of the Apache Fesod(
Incubating) library:

| Version          | JDK Version Support Range | Notes              |
|------------------|---------------------------|--------------------|
| 2.0.0-incubating | JDK8 - JDK25              | NA(not available)  |
| 1.3.x            | JDK8 - JDK25              | Non-Apache release |
| 1.2.x            | JDK8 - JDK21              | Non-Apache release |
| 1.1.x            | JDK8 - JDK21              | Non-Apache release |
| 1.0.x            | JDK8 - JDK21              | Non-Apache release |

We strongly recommend using the latest version of Apache Fesod(Incubating), as performance optimizations, bug fixes, and
new features
in the latest version will enhance your experience.

### Dependencies

Apache Fesod(Incubating) uses the following key dependencies:

- **Apache POI 5.5.1** - For Excel file processing
- **Apache Commons CSV 1.14.1** - For CSV file support
- **Ehcache 3.9.11** - For caching functionality

> If your project already includes POI-related components, you may need to manually exclude POI-related jar files to
> avoid version conflicts.

## Version Update

For detailed update logs, refer
to [Details of version updates](https://github.com/apache/fesod/releases). You can also find all
available versions in the [Maven Central Repository](https://mvnrepository.com/artifact/org.apache.fesod/fesod).

## Maven

If you are using Maven for project building, add the following configuration in the `pom.xml` file:

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod-sheet</artifactId>
    <version>version</version>
</dependency>
```

### Alternative: Using BOM for Version Management

For better dependency management, you can use the Fesod BOM:

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.apache.fesod</groupId>
            <artifactId>fesod-bom</artifactId>
            <version>version</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>org.apache.fesod</groupId>
        <artifactId>fesod-sheet</artifactId>
        <!-- Version managed by BOM -->
    </dependency>
</dependencies>
```

## Gradle

If you are using Gradle for project building, add the following configuration in the `build.gradle` file:

```gradle
dependencies {
    implementation 'org.apache.fesod:fesod-sheet:version'
}
```
