Here's your text with grammar and spelling corrections:

# Hello, Java 22

Hi, Spring fans! Happy [Java 22](https://blogs.oracle.com/java/post/the-arrival-of-java-22) release day, to those who celebrate! Did you get the bits already? Go, go, go! Java 22 is a _significant_ improvement that I think is a worthy upgrade for everyone. I love Java 22, and of course, I love GraalVM, and both have releases today! Java is of course our favorite runtime and language, and GraalVM is  a high-performance JDK distribution that supports additional languages and allows ahead-of-time (AOT) compilation (they're called GraalVM native images). GraalVM   includes all the niceties of the new Java 22 release, with some extra utilities, so I always recommend just downloading that one. I'm interested, specifically, in the GraalVM native image capability. The resulting binaries start almost instantly and take   considerably less  RAM compared to their JRE cousins. GraalVM isn't new, but it's worth remembering that Spring Boot has a great engine to support turning your Spring Boot applications into GraalVM native images. 



## Installation

Here's what I did.

I'm using the fantastic [SDKMAN](https://sdkman.io) package manager for Java. I'm also running on an Apple Silicon chip running macOS. This, and the fact that I like and encourage the use of GraalVM, will be somewhat important later, so don't forget. There'll be a test! I installed a pre-release download of the [GraalVM Community Edition](https://www.graalvm.org/community/), which I downloaded [from here](https://github.com/graalvm/oracle-graalvm-ea-builds/releases/tag/jdk-22.0.0-ea.07). GraalVM Community is the opensource version. GraalVM also have a commercial release, which is _free_, but not opensource. I'm not a lawyer, but my understanding is that you can use it as much as you like. The GraalVM team will remind you that it allows you to build even faster native images through technologies like profile guided optimization (PGO). I then unzipped it and installed it manually using the SDKMAN command line utility, like this: `sdk install java 22.07-graalce $HOME/bin/graalvm-jdk-22+36.1/Contents/Home`.  Then I did: `sdk default java 22.07-graalce` and opened up a new shell. Verify that everything is working by typing `javac --version` and `java --version` and `native-image --version`. By the time you read this, however, you probably won't need to do any of this. It'll have been released on SDKMAN itself. Just do `sdk list java` and if you see `22-graalce` (or something like that), then install that: `sdk install java 22-graalce`. Or, if you're reading this in the far-flung future  (do we have flying cars yet?) and there's `50-graalce`, then by all the means install that! Bigger versions are better!          


## You Gotta Start Somewhere...

At this point, I wanted to start building! So, I went to my second favorite place on the internet, the Spring Initializr - [start.spring.io](https://start.spring.io) - and generated a new project, using the following specifications:

* I selected the `3.3.0-snapshot` version of Spring Boot. 3.3 is not yet GA, but it should be in a few short months. In the meantime, onward and upward! This release has better support for Java 22.
* I selected `Maven` as the build tool.
* I added `GraalVM Native Support` support, `H2 Database`, and  `JDBC API` support.

I opened the project in my IDE, like this: `idea pom.xml`. Now I needed to configure a few of the Maven plugins to support both Java 22 and some of the preview features we're going to look at in this article. Here's my fully configured `pom.xml`. It's a little dense, so I'll see you after the code for the walkthrough.

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

I know, I know! There's a lot! But, not really. This `pom.xml` is almost identical to what I got from the Spring Initializr. The main changes are:

* I redefined the `maven-surefire-plugin` and `maven-compiler-plugin` to support preview features.
* I added the `spring-javaformat-maven-plugin` to support formatting my source code.
* I added two new dependencies: `org.graalvm.sdk:graal-sdk:23.1.2` and `org.graalvm.nativeimage:svm:23.1.2`, both of which are exclusively for the creation of the GraalVM `Feature` implementation that we will need later.
* I added configuration stanzas to the `<configuration>` sections of the `native-maven-plugin`, and the `spring-boot-maven-plugin`.

In no time at all, Spring Boot 3.3 will be GA and support Java 22, and so maybe half of this build file will disappear. (Talk about _Spring cleaning_!)

## A Quick Programming Note

Throughout this article, I'm going to refer to a functional interface type called `LanguageDemonstrationRunner`. It's just a functional interface I created that is declared to throw a `Throwable`, so that I don't have to worry about it. 

```java 
package com.example.demo;

@FunctionalInterface
interface LanguageDemonstrationRunner {

    void run() throws Throwable;

}
```

I have an `ApplicationRunner` which in turn injects all implementations of my functional interface and then invokes their `run` method, catching and handling `Throwable`. 


```java
    
    // ...	
    @Bean
	ApplicationRunner demo(Map<String, LanguageDemonstrationRunner> demos) {
		return _ -> demos.forEach((_, demo) -> {
			try {
				demo.run();
			} //
			catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}
    // ...
```

OK, that established.. onward!


## Bye, JNI!

This release sees the long-awaited release of [Project Panama](https://openjdk.org/projects/panama). This is one of the three features I've most been waiting for. The other two features, virtual threads and GraalVM native images, have been a reality for at least six months now.  Project Panama is  the thing that lets us leverage the galaxy of C, C++ code that's been so long denied us. Come to think of it, it probably supports  basically any kind of binary if it supports [ELF](https://en.wikipedia.org/wiki/Executable_and_Linkable_Format), I'd imagine. Rust programs and Go programs can be compiled to C-compatible binaries, for example, so I imagine (but haven't tried) that this means easy enough interop with those languages, too. Broadly, in this section, when I talk about "native code," I'm talking about binaries that are compiled in such a way that they can be invoked like a C library might.

Historically, Java has been very insular. It has _not_ been easy for Java developers to repurpose native C and C++ code. It makes sense. Native, operating system-specific code would only serve to undermine Java's promise of _Write Once, Run Anywhere_. It's always been a bit taboo. But I don't see why it should be.  To be fair, we've done alright, despite the absence of easy native code interop. There is [JNI](https://en.wikipedia.org/wiki/Java_Native_Interface), which stands for _Joylessly Navigating the Inferno_, I'm pretty sure. In order to use JNI, you must write more, _new_ C/C++ code to glue together whatever language you want to use with Java. (How is this productive? Who thought this was a good idea?) Most people _want_ to use JNI like they _want_ a root canal!  

Most people don't. We've simply had to reinvent everything in an idiomatic, Java-style way.  For nearly anything you could want to do, there is probably   a pure Java solution out there that runs anywhere Java does. It works fine  until  it doesn't. Java has missed out on key opportunities here. Imagine if Kubernetes had been built in Java? Imagine if the current AI revolution was powered by Java? There are a lot of reasons why these two notions would've been inconceivable when Numpy, Scipy, and Kubernetes were first created, but today? Today, they released Project Panama.

Project Panama introduces an easy way to link into native code. There are two levels of support. You can, in a rather low-level way, manipulate memory and pass data back and forth into native code. I said "back and forth," but I probably should've said "down and up" to native code. Project Panama supports "downcalls," calls into native code from Java, and "upcalls," calls from native code into Java. You can invoke functions, allocate and free memory, read and update fields in `struct`s, etc.  

Let's take a look at a simple example. The code uses the new `java.lang.foreign.*` APIs to look up a symbol called `printf` (which is basically `System.out.print()`), allocate memory (sort of like `malloc`) buffer, and then pass that buffer to the `printf` function. 

```java

package com.example.demo;

import org.springframework.stereotype.Component;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@Component
class ManualFfi implements LanguageDemonstrationRunner {

    // this is package private because we'll need it later
	static final FunctionDescriptor PRINTF_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(JAVA_INT, ADDRESS);

	private final SymbolLookup symbolLookup;

    // SymbolLookup is a Panama API, but I have an implementation I'm injecting
	ManualFfi(SymbolLookup symbolLookup) {
		this.symbolLookup = symbolLookup;
	}

	@Override
	public void run() throws Throwable {
		var symbolName = "printf";
		var nativeLinker = Linker.nativeLinker();
		var methodHandle = this.symbolLookup.find(symbolName)
			.map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, PRINTF_FUNCTION_DESCRIPTOR))
			.orElse(null);
		try (var arena = Arena.ofConfined()) {
			var cString = arena.allocateFrom("hello, Panama!");
			Objects.requireNonNull(methodHandle).invoke(cString);
		}
	}

}
```

Nice! Run this and you'll see it prints out `hello, Panama!`. You might be wondering why I didn't pick something more interesting as an example. It turns out that there's precious little that you can both take for granted across all operating systems _and_ perceive as having done something on your computer. IO seemed to be about all I could think of, and console IO is even easier to follow. 

But what about GraalVM native images? It doesn't support _every_ thing you might want to do. And, at least for the moment, it doesn't run on Apple Silicon.   I developed this example and set [up a GitHub Action](https://raw.githubusercontent.com/spring-tips/java22/main/.github/workflows/maven.yml) to see the results in an x86 Linux environment. Otherwise, I was pleased with the result. Remember, GraalVM wants to know about some of the dynamic things you're going to do at runtime. So, you need to tell

 it. For most things, you need to write a `.json` configuration file (or let Spring's AOT engine write them for you). Here, since this is so new, you have to write a GraalVM `Feature`, which in turn has callback methods that get invoked during GraalVM's native compilation. We just need to tell GraalVM the signature of the native function we'll be invoking via Panama at runtime. 

```java
package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

import static com.example.demo.ManualFfi.PRINTF_FUNCTION_DESCRIPTOR;

public class DemoFeature implements Feature {

	@Override
	public void duringSetup(DuringSetupAccess access) {
		RuntimeForeignAccess.registerForDowncall(PRINTF_FUNCTION_DESCRIPTOR);
	}

}
```

And then we need to wire up the feature, telling GraalVM about it, by passing in the `--features` attribute to the GraalVM native image Maven plugin configuration. We also need to unlock the foreign API support and unlock experimental stuff. (I don't know why this is experimental in GraalVM native images when it's not experimental any longer in Java 22 itself). Also, we need to tell GraalVM to allow native access for all unnamed types. So, altogether, here's the final Maven plugin configuration.

```xml
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
```

This is an awesome result. I compiled the code in this example into a GraalVM native image running on GitHub Actions runners and then executed it. The application, which - I remind you - has the Spring JDBC support and everything on the classpath - executes in 0.031 seconds (31 milliseconds), takes tens of megabytes of RAM, and invokes the native C code, from the GraalVM native image! I'm so happy, y'all. I've waited for this day for so long.

But this does feel a little low-level. At the end of the day, you are using a Java API to programmatically create and maintain structures in native code. It's sort of like using SQL from JDBC. JDBC lets you manipulate SQL database records in Java, but you're not writing SQL in Java and compiling it in Java and executing it in SQL. There's an abstraction delta; you're sending strings into the SQL engine and then getting records back out as `ResultSet`. The same is true for the low-level API in Panama.

So, they released a tool called `jextract`. You can point it at a C header file, like `stdio.h`, in which the `printf` function is defined, and it'll generate Java code that mimics the call signature of the underlying C code. I didn't use it in this example because the resulting Java code ends up being tied to the underlying platform. I pointed it to `stdio.h` and got a whole bunch of macOS specific definitions. I could hide all of that behind a runtime check for the operating system and then dynamically load a particular implementation, but, eh, this blog's already too long. If you want to see how to run `jextract`, here's the bash script I used that worked for macOS and Linux. YMMV.

```bash
#!/usr/bin/env bash
LINUX=https://download.java.net/java/early_access/jextract/22/3/openjdk-22-jextract+3-13_linux-x64_bin.tar.gz
MACOS=https://download.java.net/java/early_access/jextract/22/3/openjdk-22-jextract+3-13_macos-x64_bin.tar.gz

OS=$(uname)

DL=""
STDIO=""

if [ "$OS" = "Darwin" ]; then
    DL="$MACOS"
    STDIO=/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/include/stdio.h
elif [ "$OS" = "Linux" ]; then
    DL=$LINUX
    STDIO=/usr/include/stdio.h
else
    echo "Are you running on Windows? This might work inside the Windows Subsystem for Linux, but I haven't tried it yet.."
fi

LOCAL_TGZ=tmp/jextract.tgz
REMOTE_TGZ=$DL
JEXTRACT_HOME=jextract-22

mkdir -p "$(

 dirname  $LOCAL_TGZ )"
wget -O $LOCAL_TGZ $REMOTE_TGZ
tar -zxf "$LOCAL_TGZ" -C .
export PATH=$PATH:$JEXTRACT_HOME/bin

jextract  --output src/main/java  -t com.example.stdio $STDIO
```

Just think about it. We have easy foreign function interop, virtual threads giving us amazing scalability, and statically linked, self-contained GraalVM native image binaries. Tell me why you'd start a new project in Go, again? :)

## A Brave New World

Java 22 is an amazing new release. It brings with it a bevy of huge features and quality of life improvements. Remember, it can't always be this good! Nobody can introduce paradigm-changing new features consistently every six months. It's just not possible. So, let's be thankful and enjoy it while we can, shall we? :) The last release, Java 21, was, in my estimation, maybe the single biggest release I've seen since perhaps Java 5, maybe even earlier. It might be the biggest ever!

There are a ton of features there that are well worth your attention, including _data-oriented programming_ and _virtual threads_.

I covered this, and a lot more, in a blog I did to support the release six months ago, [_Hello, Java 21_](https://spring.io/blog/2023/09/20/hello-java-21).

## Virtual Threads (Yes, I Know This Was Released Six Months Ago!)

Virtual threads are the really important bit, though. Read the blog I just linked you to, towards the bottom. (Don't be like the Primeagen, who read the article but managed to sort of move on before even getting to the best part - the virtual threads!)

Virtual threads are a way to squeeze more out of your cloud infrastructure spend, your hardware, etc., if you're running IO-bound services. They make it so that you can take existing code written against the blocking IO APIs in `java.io`, switch to virtual threads, and suddenly handle with nary a few lines of code changes scale to much higher levels. The effect, usually, is that your system is no longer constantly waiting for threads to be available so the average response time goes down, and - even nicer - you will see the system handle many more requests at the same time! I can't stress this enough. Virtual threads are _awesome_! And if you're using Spring Boot 3.2, you need only specify `spring.threads.virtual.enabled=true` to benefit from them!

Virtual threads are part of a slate of new features, which have been more than half a decade in coming, designed to make Java the lean, mean scale machine we all knew it deserved to be. And it's working! Virtual threads was 1/3, and is the only one that has been delivered in a GA form.

There are two other features: structured concurrency and scoped values, both of which have yet to land. Structured concurrency gives you a more elegant programming model for building concurrent code, and scoped values give you an efficient and more versatile alternative to `ThreadLocal<T>`, particularly useful in the context of virtual threads, where you can now realistically have _millions_ of threads. Imagine having duplicated data for each of those!

These features are in preview in Java 22. I don't know that they're worth showing, just yet. Virtual threads are the magic piece, in my mind, and they are so magic precisely because you don't really need to know about them! Just set that one property, and you're off.

Virtual threads give you the amazing scale of something like `async`/`await` in Python, Rust, C#, TypeScript, JavaScript, or `suspend` in Kotlin, but without the inherent verbosity of code and busy work required to use those language features. It's one of the few times where, save for maybe Go's implementation, Java is just straight-up better in the result. Go's implementation is ideal, but only because they had this baked in to the 1.0 version. Indeed, Java's implementation is more remarkable precisely because it coexists with the older platform threads model.

## Unnamed Variables and Patterns

When you're creating threads, or working with Java 8 streams and collectors, you're going to be creating lots of lambdas. Indeed, there are plenty of situations - like the `JdbcClient` and its `RowMapper` interface - in Spring where you'll be working with lambdas.

Fun fact: Lambdas were first introduced in 2014's Java 8 release. (Yes, that was a _decade_ ago! People were doing the ice bucket challenges, the world was obsessed with selfie sticks, _Frozen_, and _Flappy Bird_.)

Lambdas are amazing. They introduce a new unit of reuse in the Java language. And the best part is that they were designed in such a way

 as to sort of graft onto the existing rules of the runtime, including adapting so-called _functional interfaces_ or SAMs (single abstract method) interfaces automatically to lambdas. My only complaint with them is that it was annoying having to make things final that were referenced from within the lambda that belong to a containing scope. That's since been fixed. And it is annoying having to spell out every parameter to a lambda even if I have no intention of using it, and now, with Java 22, that too has been fixed!

```java 
package com.example.demo;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
class AnonymousLambda Parameters implements LanguageDemonstrationRunner {

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

That class uses Spring's `JdbcClient` to query the underlying database. It pages through the results, one by one, and then invokes our lambda, which conforms to the type `RowMapper<Customer>` to help in adapting our results into records that line up with my domain model. The `RowMapper<T>` interface method `T mapRow(ResultSet rs, int rowNum) throws SQLException;` expects two parameters: the `ResultSet`, which I'll need, and the `rowNum`, which I'll almost never need. Now, I don't need to specify it! Just plug in `_`, like in Kotlin or TypeScript. Nice!

## Gatherers

Gatherers are another nice feature that is also in preview. You may know my friend Viktor Klang from his amazing work on Akka, the actor cluster, and the Scala language, whilst he was at Lightbend. These days, he's a Java language architect at Oracle, and one of the things he's been working on is the new Gatherer API. The streams API, which was also introduced in Java 8, by the way - gave Java developers a chance, along with lambdas, to greatly simplify and modernize their existing code, and to move in a more functional-programming-centric direction. But, there are cracks in the abstraction. The Streams API has a number of very convenient operators that work for 99% of the scenarios, but when you find something for which a convenient operator doesn't exist, it can be frustrating. There have been countless proposals for new operators adding to the Streams API in the intervening ten years, and there were even discussions and concessions made in the original proposal for lambdas that the programming model be flexible [enough to support introducing new operators](https://cr.openjdk.org/~vklang/Gatherers.html). It's finally arrived! Gatherers provide a slightly more low-level abstraction that gives you the ability to plug in all sorts of new operations on Streams, without having to materialize the `Stream` as a `Collection` at any point. Here's an example I stole directly, and unabashedly, [from Viktor and the team](https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html).

```java 
package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

@Component
class Gatherers implements LanguageDemonstrationRunner {

    private static <T, R> Gatherer<T, ?, R> scan(Supplier<R> initial,
                                                 BiFunction<? super R, ? super T, ? extends R> scanner) {

        class State {

            R current = initial.get();

        }
        return Gatherer.<T, State, R>ofSequential(State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.current = scanner.apply(state.current, element);
                    return downstream.push(state.current);
                }));
    }

    @Override
    public void run() {
        var listOfNumberStrings = Stream
                .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(scan(() -> "", (string, number) -> string + number)
                        .andThen(java.util.stream.Gatherers.mapConcurrent(10, s -> s.toUpperCase(Locale.ROOT)))
                )
                .toList();
        System.out.println(listOfNumberStrings);
    }

}

```

The main thrust of that code is that there's a method here, `scan`, which returns an implementation of `Gatherer<T,?,R>`. Each `

Gatherer<T,O,R>` expects an initializer and an integrator. It'll come with a default combiner and a default finisher, though you can override both. This implementation reads through all those entries (numbers) and builds up a string for each entry that then accumulates every successive string. The result is that you get `1`, `12`, `123`, `1234`, etc.

Still don't quite understand? I get the feeling that's going to be okay. This is a bit in the weeds for most folks, I'd imagine. Most of us don't need to write our own Gatherers. But you can. My friend [Gunnar Morling](https://www.morling.dev/blog/zipping-gatherer/) did just that the other day, in fact. I wonder what this implies for awesome projects like Eclipse Collections? Will they ship Gatherers? What other projects might? The genius of the Gatherers approach is that now the community can scratch its own itch. I'd love to see a lot of common sense gatherers, eh, well, gathered into one place.

The example above demonstrates that gatherers are also composable. We actually have two `Gatherer` in play: the one that does the scanning, and the one that maps every item to uppercase, and it does it concurrently.