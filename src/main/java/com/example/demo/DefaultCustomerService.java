package com.example.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class DefaultCustomerService {

	@EventListener
	void ready(ApplicationReadyEvent re) {
		System.out.println("a simple component that's ready [" + re + "]");
	}

}
