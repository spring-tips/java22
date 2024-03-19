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
import java.util.Map;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Throwable {
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
class JExtractFfi implements LanguageDemonstrationRunner {

    @Override
    public void run() throws Throwable {

    }

}

@Component
class ManualFfi implements LanguageDemonstrationRunner {

    public static final FunctionDescriptor PRINTF_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(JAVA_INT, ADDRESS);


    private final SymbolLookup symbolLookup;

    ManualFfi(SymbolLookup symbolLookup) {
        this.symbolLookup = symbolLookup;
    }

    private void manualFfi(String greetings) throws Throwable {
        var symbolName = "printf";
        var nativeLinker = Linker.nativeLinker();

        var methodHandle = this.symbolLookup
                .find(symbolName)
                .map(symbolSegment -> nativeLinker
                        .downcallHandle(symbolSegment, PRINTF_FUNCTION_DESCRIPTOR))
                .orElse(null);
        try (var arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom(greetings);
            Objects.requireNonNull(methodHandle).invoke(cString);
        }
    }

    @Override
    public void run() throws Throwable {
        manualFfi("hello, manual FFI!");
    }
}