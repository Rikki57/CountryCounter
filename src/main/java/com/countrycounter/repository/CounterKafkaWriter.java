package com.countrycounter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CounterKafkaWriter implements CounterInput {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void addValue(String countryCode) {
        sendMessage(countryCode);
    }

    public void sendMessage(String message) {

        kafkaTemplate.send("countryCounter", message);

    }
}
