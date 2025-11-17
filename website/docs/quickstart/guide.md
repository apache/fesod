---
id: 'guide'
title: 'Guide'
---

# Installation

## Compatibility Information

The following table lists the minimum Java language version requirements for each version of the Fesod library:

| Version | JDK Version Support Range | Notes |
|---------|---------------------------|-------|
| 1.3.x   | JDK8 - JDK25              |       |
| 1.2.x   | JDK8 - JDK21              |       |
| 1.1.x   | JDK8 - JDK21              |       |
| 1.0.x   | JDK8 - JDK21              |       |

We strongly recommend using the latest version of Fesod, as performance optimizations, bug fixes, and new features
in the latest version will enhance your experience.

> Currently, Fesod uses POI as its underlying package. If your project already includes POI-related components, you
> will need to manually exclude POI-related jar files.

## Version Update

For detailed update logs, refer
to [Details of version updates](https://github.com/apache/fesod/releases). You can also find all
available versions in the [Maven Central Repository](https://mvnrepository.com/artifact/org.apache.fesod/fesod).

## Maven

If you are using Maven for project building, add the following configuration in the `pom.xml` file:

```xml

<dependency>
    <groupId>org.apache.fesod</groupId>
    <artifactId>fesod</artifactId>
    <version>version</version>
</dependency>
```

## Gradle

If you are using Gradle for project building, add the following configuration in the `build.gradle` file:

```gradle
dependencies {
    implementation 'org.apache.fesod:fesod:version'
}
```
