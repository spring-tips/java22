package com.example.demo;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.lang.classfile.ClassFile;
import java.lang.classfile.FieldModel;
import java.lang.classfile.MethodModel;

@Component
@ImportRuntimeHints(ClassParsing.Hints.class)
class ClassParsing implements LanguageDemonstrationRunner {

	static class Hints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.resources().registerResource(DEFAULT_CUSTOMER_SERVICE_CLASS);
		}

	}

	private final byte[] classFileBytes;

	private static final Resource DEFAULT_CUSTOMER_SERVICE_CLASS = new ClassPathResource(
			"/simpleclassfile/DefaultCustomerService.class");

	ClassParsing() throws Exception {
		this.classFileBytes = DEFAULT_CUSTOMER_SERVICE_CLASS.getContentAsByteArray();
	}

	@Override
	public void run() {
		var classModel = ClassFile.of().parse(this.classFileBytes);
		for (var classElement : classModel) {
			switch (classElement) {
				case MethodModel mm -> System.out.printf("Method %s%n", mm.methodName().stringValue());
				case FieldModel fm -> System.out.printf("Field %s%n", fm.fieldName().stringValue());
				default -> {
				}
			}
		}
	}

}
