LDAP Maven Plugin
=================

The [LDAP Maven Plugin](http://ldap-maven-plugin.btmatthews.com/) is a [Maven](http://maven.apache.org) plugin that
can:

* Import/export data to/from an [LDAP](http://en.wikipedia.org/wiki/LDAP) directory server. Both the:
  [LDIF](http://en.wikipedia.org/wiki/LDIF) and [DSML](http://en.wikipedia.org/wiki/Directory_Service_Markup_Language)
  formats are supported.

* Launch and shutdown a LDAP directory server in the Maven build life-cycle. The plugin supports the following LDAP
  directory servers:

  * [ApacheDS](http://directory.apache.org/apacheds/)

  * [OpenDJ](http://opendj.forgerock.org/)

  * [UnboundID](https://www.unboundid.com/products/ldap-sdk/)

Importing or exporting data
---------------------------

### LDAP Data Interchange Format (LDIF)

#### Import

#### Export

### Directory Service Markup Language (DSML)

#### Import

#### Export

Running an LDAP Server in the build life-cycle
----------------------------------------------

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project
  xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="
    http://maven.apache.org/POM/4.0.0
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>com.btmatthews.maven.plugins</groupId>
          <artifactId>ldap-maven-plugin</artifactId>
          <version>1.2.0</version>
        </plugin>
      </plugins>
    </pluginManagement>
    <plugins>
      <plugin>
        <groupId>com.btmatthews.maven.plugins</groupId>
        <artifactId>ldap-maven-plugin</artifactId>
        <version>1.2.0</version>
        <configuration>
          <monitorPort>11389</monitorPort>
          <monitorKey>ldap</monitorKey>
          <daemon>true</daemon>
        </configuration>
        <executions>
          <execution>
            <id>start-ldap</id>
            <goals>
              <goal>run</goal>
            </goals>
            <phase>pre-integration-test</phase>
          </execution>
          <execution>
            <id>stop-ldap</id>
            <goals>
              <goal>stop</goal>
            </goals>
            <phase>post-integration-test</phase>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
```

### Connecting in Java

Maven Central Coordinates
-------------------------
The **LDAP Maven Plugin** has been published in [Maven Central](http://search.maven.org) at the following
coordinates:

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.0</version>
</plugin>
```

Credits
-------
The technique for embedding **ApacheDS** was determined by examining the implementation of the embedded LDAP server in
the [Spring Security](http://www.springsource.org/spring-security) project.

License & Source Code
---------------------
The **LDAP Maven Plugin** is made available under the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0.html) and the source code is hosted on
[GitHub](http://github.com) at https://github.com/bmatthews68/ldap-maven-plugin.