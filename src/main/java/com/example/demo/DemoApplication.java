package com.example.demo;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.util.Map;

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
			} //
			catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});
	}

}
