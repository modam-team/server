package com.example.modam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperConfig {
    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    @Primary
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }
}
