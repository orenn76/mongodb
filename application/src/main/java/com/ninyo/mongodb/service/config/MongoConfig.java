package com.ninyo.mongodb.service.config;

import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${mongodb.connectionTimeout:20000}")
    private int connectionTimeout;

    @Bean
    public MongoClientOptions mongoOptions() {
        return MongoClientOptions.builder().connectTimeout(connectionTimeout).build();
    }

}
