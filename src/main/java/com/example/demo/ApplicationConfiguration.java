package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.util.Map;

@SpringBootApplication
public class ApplicationConfiguration {

	@Bean
	SymbolLookup symbolLookup() {
		var nativeLinker = Linker.nativeLinker();
		var stdlibLookup = nativeLinker.defaultLookup();
		var loaderLookup = SymbolLookup.loaderLookup();
		return name -> loaderLookup.find(name).or(() -> stdlibLookup.find(name));
	}

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

}
