package com.countrycounter.service;

import com.countrycounter.repository.CounterInput;
import org.springframework.stereotype.Service;

@Service
public class CounterInputService {
    private final CounterInput counterInput;

    public CounterInputService(CounterInput counterInput) {
        this.counterInput = counterInput;
    }

    public Boolean count(String country) {
        counterInput.addValue(country);
        return true;
    }
}
