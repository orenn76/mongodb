package com.ninyo.mongodb.sdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ninyomongodb.sdk", "com.ninyosecurity.secured"})
@EnableAutoConfiguration
public class MongoDBTestApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MongoDBTestApplication.class, args);
    }

}