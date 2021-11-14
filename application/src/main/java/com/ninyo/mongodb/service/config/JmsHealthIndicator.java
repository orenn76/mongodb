package com.ninyo.mongodb.service.config;

import com.ninyo.messagebroker.client.MessageBrokerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.JmsHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsHealthIndicator {

    @Autowired(required = false)
    private MessageBrokerClient client;

    @Bean("jmsHealthIndicator")
    public org.springframework.boot.actuate.health.JmsHealthIndicator jmsHealthIndicator() {
        ConnectionFactory connectionFactory = client.getProvider().getConnectionFactory();
        return new org.springframework.boot.actuate.health.JmsHealthIndicator(connectionFactory);
    }
}
