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
- i added `graalvm native image` support, `H2`, and the `JDBC` support

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
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
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

## virtual threads (yes, I know this was released six momths ago!)

virtual threads are the really important bit, tho. read the blog i just linked you to, towards the bottom. (Don't be
like the Primeagen, who read the article but managed to sort of move on before even getting to the best part - the
virtual threads!)

virtual threads are a way to squeeze more out of your cloud infrastructure spend, your hardware, etc., if you're running
IO bound services. tey make it so that you can take existing code writtena gainst the blocking IO apis in `java.io`,
switch to virtual threads, and suddenly handle with narry a few lines of code changes scale to much higher levels. the
effect, usually, is that your system is no longer constantly waiting for threads to be available so the avergafe
response time goes down, and - even nicer - you will see the handle many more rwquests at the same time! I cant stress
this enough. vritual threads are _awesome_! and if u r using spring boot 3.2, you need only
specify `spring.threads.virtual.enabled=true` to benefit from them!

virtual threads are part of a slate of new features, which have been more than half a decade in coming, designed to make
Java the lean mean scale machnie we all knew it deserved be. and it's working! virtual threads was 1/3, and is the only
one that has been delivered in a GA form.

there are two other features: structured concurrenc and scoped values, both of which have yet to land. Structured
concurrency gives you a more elegant programin model for building concurrent code, and scoped values give you an
efficient and more versatile alternative to `ThreadLocal<T>`, particularly useful in the context of virtual threads,
where you can now realistically have _millions_ of threads. Imagine havign duplicated data for each of those! 

these features are in preview in java 22. i don't know that they're worth showing, just yet. virtual threads are the magic piece, in my mind, and they are so magic precisely because you dont rally need to know about them! jsut set that one property and you're off. 

virtual threads give you the amazing scale of something like   `async`/`await` in Python, Rust, C#, Typescript, JavaScript, or `suspend` in Kotlin, but without the inherit verbosity of code and busy work required to use those language features. it's one of the few times where, save for maybe Go's implementation, Java is jsut straight up better in the result. Gos implementation is ideal, but only because they had this baked in to the 1.0 version. Indeed, Javas implementation is more remarkable precisdely because it coexists with the older platform threads model. 

## unnamed variables and patterns

when you're creating threads, or working with Java 8 streams and gatherers, you're going to be creating lots of lambdas. Indeed, there are plenty of situations - like the `JdbcClient` and its `RowMapper` interface - in Spring  where you'l be working with lambdas

Fun fact: lambdas were first introduced in 2014's Java 8 release. (Yes, that was a _decade_ ago! People were doing the ice bucket challenges, the world was obsessed with selfie sticks, _Frozen_, and _Flappy Bird_.) 

Lambdas are amazing. They introduce a new unit of reuse in the java language. and the best part is that thy were designed in such a way as to sort of graft on to the existing rules of the runtime, including adapting so-called _functional interfaces_  or SAMs (single abstract method) interfaces automatically to lambdas. My only complaint iwth them is that it was annoying havint to make things final that were referenced from within the lambda that belong to a containing scope. that's since been fixed. and it is annoying having to spell out every parameter to a lmbda even if i have no intentino on using it, and now, with java 222, that took ahs been fixed! 

```java 
package com.example.demo;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
class AnonymousLambdaParameters implements LanguageDemonstrationRunner {

	private final JdbcClient db;

	AnonymousLambdaParameters(DataSource db) {
		this.db = JdbcClient.create(db);
	}

	record Customer(Integer id, String name) {
	}

	@Override
	public void run() throws Throwable {
		var allCustomers = this.db.sql("select * from customer ")
			.query((rs, _) -> new Customer(rs.getInt("id"), rs.getString("name")))
			.list();
		System.out.println("all: " + allCustomers);
	}

}

```


That class uses spring's `JdbcClient` to qeurty the underlying database. It pages through the results, one by one, and then invokes our lambda, which conforms to the type `RowMapper<Customer>` to help in adapting our results into records that line up with my domain model. The `RowMapper<T>` interface method `T mapRow(ResultSet rs, int rowNum) throws SQLException;` expects two parameters: the `ResultSet`, which I'll need, and the `rowNum`, which I'll almost never need. Now, I don' need to specify it! Just plug in `_`, like in Kotlin or TypeScript. Nice! 


## Gatherers 

Gatherers are  another nice  feature that is also in preview.    you may know my friend Viktor Klang from his amazing work on Akka, the actor cluster, and the Scala language, whilst he was at Lightbend. These days, he's a java language architect at Oracle, and one of the things he's been working on is the new gatherer api. the streams API, which was also introduced in Java 8, by the way - gave java develoeprs a chance, along with lamdas, to great simplify and modernize their existing code, adn to move in a more fucntional-programming centric direction. but, there are cracks in the abstraction. the Streams API has a number of very convenient operators that work for 99% of the scenarios, but when you find something for which a convenient operator doesn't exist, it can be frustrating. there have been countless proposals for new operators adding o the Streams api in the intervening ten years, and there were even discussions and ocnessions made in the original proposal for lambdas that the programming model be flexible [enough to support introducing new operators](https://cr.openjdk.org/~vklang/Gatherers.html). its finally arrived! Gatherers provide a slightly more low-level abstraction that gives you the ability to plugin all sorts of new operations on Streams, without having to materialize the `Stream` as a `Collection` at any point. heres an example i stole directly, and unabashedly, [from viktor and the team](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html). 


```java 
 
```



## Project Panama: 
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/ForeignInterface.md
https://www.baeldung.com/java-project-panama

