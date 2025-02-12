package com.cmcilroy.medicines_shortages_assistant.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    // provides access to the ModelMapper inside the application context
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
