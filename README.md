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

The plugin provides the following goals to import or export content from a LDAP directory server:

  * [load](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/load-mojo.html) is used to
    load content from a file into a LDAP directory server.

  * [dump](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/dump-mojo.html) is used to
    dump content to a file from a LDAP directory sever.

The following file formats are supported

  * [LDIF](http://en.wikipedia.org/wiki/LDIF) - LDAP Directory Interchange Format

  * [DSML](http://en.wikipedia.org/wiki/Directory_Service_Markup_Language) - Directory Services
    Markup Language

### LDAP Data Interchange Format (LDIF)

#### Import

The following **POM** fragment demonstrates how to load content into a LDAP directory server from a
[LDIF](http://en.wikipedia.org/wiki/LDIF) formatted file using the
[load](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/load-mojo.html)
goal of the **LDAP Maven Plugin**.

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.1</version>
    <executions>
        <execution>
            <id>load</id>
            <goals>
                <goal>load</goal>
            </goals>
            <configuration>
                <host>localhost</host>
                <port>10389</port>
                <authDn>uid=admin,ou=system</authDn>
                <passwd>secret</passwd>
                <sources>
                    <ldif>load.ldif</ldif>
                </sources>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.btmatthews.maven.plugins.ldap</groupId>
            <artifactId>server-unboundid</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
</plugin>
```

#### Export

The following **POM** fragment demonstrates how to export content from a LDAP directory server to a
[LDIF](http://en.wikipedia.org/wiki/LDIF) formatted file using the
[dump](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/dump-mojo.html)
goal of the **LDAP Maven Plugin**.

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.1</version>
    <executions>
        <execution>
            <id>dump</id>
            <goals>
                <goal>dump</goal>
            </goals>
            <configuration>
                <host>localhost</host>
                <port>10389</port>
                <authDn>uid=admin,ou=system</authDn>
                <passwd>secret</passwd>
                <searchBase>dc=btmatthews,dc=com</searchBase>
                <filename>dump.ldif</filename>
                <format>ldif</format>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.btmatthews.maven.plugins.ldap</groupId>
            <artifactId>server-unboundid</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
</plugin>
```

### Directory Service Markup Language (DSML)

#### Import

The following **POM** fragment demonstrates how to load content into a LDAP directory server from a
[DSML](http://en.wikipedia.org/wiki/Directory_Service_Markup_Language) formatted file using the
[load](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/load-mojo.html)
goal of the **LDAP Maven Plugin**.

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.1</version>
    <executions>
        <execution>
            <id>load</id>
            <goals>
                <goal>load</goal>
            </goals>
            <configuration>
                <host>localhost</host>
                <port>10389</port>
                <authDn>uid=admin,ou=system</authDn>
                <passwd>secret</passwd>
                <sources>
                    <dsml>${basedir}/load.dsml</dsml>
                </sources>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.btmatthews.maven.plugins.ldap</groupId>
            <artifactId>server-unboundid</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
</plugin>
```

#### Export

The following **POM** fragment demonstrates how to export content from a LDAP directory server to a
[DSML](http://en.wikipedia.org/wiki/Directory_Service_Markup_Language) formatted file using the
[dump](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/dump-mojo.html)
goal of the **LDAP Maven Plugin**.

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.1</version>
    <executions>
        <execution>
            <id>dump</id>
            <goals>
                <goal>dump</goal>
            </goals>
            <configuration>
                <host>localhost</host>
                <port>10389</port>
                <authDn>uid=admin,ou=system</authDn>
                <passwd>secret</passwd>
                <searchBase>dc=btmatthews,dc=com</searchBase>
                <filename>dump.dsml</filename>
                <format>dsml</format>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.btmatthews.maven.plugins.ldap</groupId>
            <artifactId>server-unboundid</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
</plugin>
```

Running an LDAP Server in the build life-cycle
----------------------------------------------

### pom.xml

The following **POM** fragment uses the
[run](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/run-mojo.html) goal to launch an
embedded LDAP directory server prior to the execution of the integration tests and then uses the
[stop](http://ldap-maven-plugin.btmatthews.com//ldap-maven-plugin/stop-mojo.html) goal to shutdown
the embedded LDAP directory server upon completion of the integration tests.

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
          <version>1.2.1</version>
        </plugin>
      </plugins>
    </pluginManagement>
    <plugins>
      <plugin>
        <groupId>com.btmatthews.maven.plugins</groupId>
        <artifactId>ldap-maven-plugin</artifactId>
        <version>1.2.1</version>
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
        <dependencies>
            <dependency>
                <groupId>com.btmatthews.maven.plugins.ldap</groupId>
                <artifactId>server-unboundid</artifactId>
                <version>1.2.1</version>
            </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>
</project>
```

### Connecting in Java

The following LDAP client libraries can be used to connect to the embedded LDAP server:

  * [UnboundID LDAP SDK for Java](http://www.unboundid.com/products/ldapsdk/)

  * [ApacheDS Directory LDAP API](http://directory.apache.org/api/)

  * [Spring LDAP](http://projects.spring.io/spring-ldap/)

The [LDAPUnit](https://github.com/bmatthews68/ldapunit) library provides an assortment of assertion and
verification methods for use in unit and integration test cases.

Maven Central Coordinates
-------------------------
The **LDAP Maven Plugin** has been published in [Maven Central](http://search.maven.org) at the following
coordinates:

```xml
<plugin>
    <groupId>com.btmatthews.maven.plugins</groupId>
    <artifactId>ldap-maven-plugin</artifactId>
    <version>1.2.1</version>
</plugin>
```

License & Source Code
---------------------
The **LDAP Maven Plugin** is made available under the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0.html) and the source code is hosted on
[GitHub](http://github.com) at https://github.com/bmatthews68/ldap-maven-plugin.