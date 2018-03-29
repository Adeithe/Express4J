# Express4J [![](https://jitpack.io/v/Adeithe/Express4J.svg?style=flat-square)](https://jitpack.io/#Adeithe/Express4J)

Simplistic Web Framework written in Java 8

## Getting Started
All examples of how to get started with Express4J are available at [/src/test/java](https://github.com/Adeithe/Express4J/tree/master/src/test/java)

## Documentation
Documentation is not available at this time.

## Using Express4J in your project
`@VERSION@` = The release version of Express4J to use or `-SNAPSHOT` to use the dev version
##### With Maven
Add the following to your `pom.xml` (Without ellipses)
```xml
...
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
...
<dependencies>
    ...
    <dependency>
        <groupId>com.github.Adeithe</groupId>
        <artifactId>Express4J</artifactId>
        <version>@VERSION@</version>
    </dependency>
</dependencies>
...
```
##### With Gradle
Add the following to your `build.gradle` (Without ellipses)
```groovy
allprojects {
    ...
    repositories {
        ...
        maven { url  "https://jitpack.io" }
    }
}
...
dependencies {
  ...
  compile "com.github.Adeithe:Express4J:@VERSION@"
}
...
```
