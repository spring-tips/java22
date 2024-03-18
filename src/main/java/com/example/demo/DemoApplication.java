package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.util.Objects;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;

@SpringBootApplication
public class DemoApplication {

    public static final FunctionDescriptor PRINTF_FUNCTION_DESCRIPTOR =
            FunctionDescriptor.of(JAVA_INT, ADDRESS);

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(DemoApplication.class, args);
        printf("hello, moto");
    }

    private static void printf(String greetings) throws Throwable {
        var symbolName = "printf";
        var nativeLinker = Linker.nativeLinker();
        var stdlibLookup = nativeLinker.defaultLookup();
        var loaderLookup = SymbolLookup.loaderLookup();
        var compositeLookup = (SymbolLookup) name -> loaderLookup
                .find(name)
                .or(() -> stdlibLookup.find(name));
        var methodHandle = compositeLookup
                .find(symbolName)
                .map(symbolSegment -> nativeLinker.downcallHandle(symbolSegment, PRINTF_FUNCTION_DESCRIPTOR))
                .orElse(null);
        try (var arena = Arena.ofConfined()) {
            var cString = arena.allocateFrom(greetings);
            Objects.requireNonNull(methodHandle).invoke(cString);
        }
    }


}

