package com.example.demo;

import org.springframework.stereotype.Component;

@Component
class StringTemplates implements LanguageDemonstrationRunner {

	@Override
    public void run() throws Throwable {
        var name = "josh";
        System.out.println(STR."""
            the name is: \{name.toUpperCase()}
        """);
    }

}
