package com.cmcilroy.medicines_shortages_assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicinesShortagesAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicinesShortagesAssistantApplication.class, args);
	}

}
