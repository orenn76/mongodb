package com.ninyo.mongodb.service;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ninyomongodb.service", "com.ninyomessagebroker", "com.ninyocommon", "com.ninyosecurity.secured"})
@EnableAutoConfiguration
@EnableEncryptableProperties
public class MongoDBApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoDBApplication.class, args);
    }
}
