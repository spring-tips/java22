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
