package com.countrycounter.service;

import com.countrycounter.repository.CounterOutput;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CounterOutputService {
    private final CounterOutput counterOutput;

    public CounterOutputService(CounterOutput counterOutput) {
        this.counterOutput = counterOutput;
    }

    public Map<String, Long> getCounters() {
        return counterOutput.getValues();
    }
}
