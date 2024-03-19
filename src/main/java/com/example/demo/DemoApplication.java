package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    SymbolLookup symbolLookup() {
        var nativeLinker = Linker.nativeLinker();
        var stdlibLookup = nativeLinker.defaultLookup();
        var loaderLookup = SymbolLookup.loaderLookup();
        return name -> loaderLookup.find(name).or(() -> stdlibLookup.find(name));
    }

    // NB: the demo has a nice feature, too: anonymous parameters
    @Bean
    ApplicationRunner demo(Map<String, LanguageDemonstrationRunner> demos) {
        return _ -> demos.forEach((_, demo) -> {
            try {
                demo.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}

@FunctionalInterface
interface LanguageDemonstrationRunner {
    void run() throws Throwable;
}

@Component
class Gatherers implements LanguageDemonstrationRunner {

    // viktor klang! the legend of klang continues!
    //https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html
    private static <T, R> Gatherer<T, ?, R> scan(
            Supplier<R> initial,
            BiFunction<? super R, ? super T, ? extends R> scanner) {

        class State {
            R current = initial.get();
        }

        return Gatherer.<T, State, R>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.current = scanner.apply(state.current, element);
                    return downstream.push(state.current);
                })
        );
    }

    @Override
    public void run()   {

        var listOfNumberStrings = Stream
                .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(scan(() -> "", (string, number) -> string + number)
                        .andThen(java.util.stream.Gatherers.mapConcurrent(10,
                                s -> s.toUpperCase(Locale.ROOT)))
                )
                .toList();

        System.out.println( listOfNumberStrings);

    }
}

@Component
class StringTemplates implements LanguageDemonstrationRunner {

    @Override
    public void run() throws Throwable {
        var name = "josh";
        System.out.println(STR."name: \{name.toUpperCase()}");
    }
}


@Component
class ManualFfi implements LanguageDemonstrationRunner {

    static final FunctionDescriptor PRINTF_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(JAVA_INT, ADDRESS);

    private final SymbolLookup symbolLookup;

    ManualFfi(SymbolLookup symbolLookup) {
        this.symbolLookup = symbolLookup;
    }

    @Override
    public void run() throws Throwable {
        var symbolName = "printf";
        var nativeLinker = Linker.nativeLinker();
        var methodHandle = this.symbolLookup
                .find(symbolName)
                .map(symbolSegment -> nativeLinker
                        .downcallHandle(symbolSegment, PRINTF_FUNCTION_DESCRIPTOR))
                .orElse(null);
        try (var arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom("hello, manual FFI!");
            Objects.requireNonNull(methodHandle).invoke(cString);
        }
    }
}