# Hello, Java 22

Hi, Spring fans! [Java 22](https://) is out today! Did you get the bits? Java 22 is a good improvement that i think is a worthy upgrade for everybody. i love java 22, and of course i love graalvm, which is a distributio of OpoenJDK didstributed by oracle and released on the same day. it includes all the niceties of the new java 22 release, with some extra utilities to support, among other things, compiling code into operating system and architecture specific binaries. these binaries start all but instantly and take a considerably smaller amount of ram compared to their jRE cousins. Graalvm isn't new, but it's just remeber that spring boot has a great engine to support turning your spring boot applications into gralvm native images. you can install graalvm for java 22 today, as well. 

Here's what I did.

## installation 

I'm using the fantastic SDKMAN package manager for Java. I'm also runnong on an Apple Silicon chip running macOS. this, and the fact that I like and encourage the use of graalvm) will be somewhat important later. I installed a pre-release download of graalvm community edition, which i downloaded [from here](https://github.com/graalvm/oracle-graalvm-ea-builds/releases/tag/jdk-22.0.0-ea.07). i then  unzipped it, and  installed it manually using the SDKMAN command line utility, like this: `sdk install java 22.07-graalce $HOME/bin/graalvm-jdk-22+36.1/Contents/Home`. By the time 

then i did: `sdk default java 22.07-graalce` and opened up a new shell. verify that everything is working by typing `javac --version` and `java --version` and `native-image --version`. 

## you gotta start somewhere..

At this point, i wanted to start building! so i went to my second favoeite place on the internet, [start.spring.io](https://start.spring.io) and generated a new project, using the following specifications:

- i selected the `3.3.0-snapshot` version of Spring Boot. 3.3 is not yet GA, but it should be in a few short months. In the meantime, onward and upward! This gives us better support for Java 22.
- i selected `Maven` as the build tool. 
- i added `graalvm native image` support

I opened the project in my iDE, like this: `idea pom.xml`. Now I needed to configure a few of the Maven plugins to support both Java 22 and some of the preview features we're going to look at in this article. here's my fully configured `pom.xml`.

```pom
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0-SNAPSHOT</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>22</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.graalvm.sdk</groupId>
            <artifactId>graal-sdk</artifactId>
            <version>23.1.2</version>
        </dependency>
        <dependency>
            <groupId>org.graalvm.nativeimage</groupId>
            <artifactId>svm</artifactId>
            <version>23.1.2</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>0.10.1</version>
                <configuration>
                    <buildArgs>
                        <buildArg> --features=com.example.demo.DemoFeature</buildArg>
                        <buildArg> --enable-native-access=ALL-UNNAMED </buildArg>
                        <buildArg> -H:+ForeignAPISupport</buildArg>
                        <buildArg> -H:+UnlockExperimentalVMOptions</buildArg>
                        <buildArg> --enable-preview</buildArg>
                    </buildArgs>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <argLine>--enable-preview</argLine>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <enablePreview>true</enablePreview>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <compilerArguments> --enable-preview </compilerArguments>
                    <jvmArguments> --enable-preview</jvmArguments>
                </configuration>
            </plugin>
            <plugin>
			<groupId>io.spring.javaformat</groupId>
			<artifactId>spring-javaformat-maven-plugin</artifactId>
			<version>0.0.41</version>
                <executions>
                    <execution>
                        <phase>validate</phase>
                        <inherited>true</inherited>
                        <goals>
                            <goal>validate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <repositories>
    <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
        <repository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <releases>
                <enabled>false</enabled>
            </releases>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </pluginRepository>
        <pluginRepository>
            <id>spring-snapshots</id>
            <name>Spring Snapshots</name>
            <url>https://repo.spring.io/snapshot</url>
            <releases>
                <enabled>false</enabled>
            </releases>
        </pluginRepository>
    </pluginRepositories>
</project>
```

I know, I know! There's a lot! But, not really. This is mostly identical to what I got from the spring Initializr. the main changes are:

- i redefined the `maven-surefire-plugin` and `maven-compiler-plugin` to support preview features
- i added the `spring-javaformat-maven-plugin` to support formatting my source code.
- i added two new dependencies: `org.graalvm.sdk`:`graal-sdk`:`23.1.2` and `org.graalvm.nativeimage`:`svm`:`23.1.2`, both of which are exclusively for the creation of the GraalVM `Feature` implementationt hat we will need later.
- i added configuration stanzas to the `<configuration>` sections of the `native-maven-plugin`, and the `spring-boot-maven-plugin`. 

In no time at all, Spring Boot 3.3 will be GA and support Java 22, and so maybe half of this build will disappear. talk about "Spring" cleaning... 

## A Brave New World

Java 22 is an amazing new release. It brings with it a bevy of huge features and quality of life improvements. Remember, it can't always be this good! Nobody can introduce paradfigm changing new features cnosistently every six momths. It's just not possible. So,let's be thankful and emjoy it while we can, shall we? :) Java 21 is, in my estimation, maybe the single biggest release i've seen since perhaps Java 5, mayve even earlier. it might be the biggest ever! 

there are a ton of features there that are well worth your attention, including _data oriented programming_ and _virtual threads_. 

I covered this, and a lot more, in a blog I did to support the release six months ago, [_Hello, Java 21_](https://spring.io/blog/2023/09/20/hello-java-21). 




## unnamed variables and patterns


## Gatherers 
https://www.morling.dev/blog/zipping-gatherer/ 

## Runners Up 

* Scoped values

## Project Panama: 
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/ForeignInterface.md
https://www.baeldung.com/java-project-panama

