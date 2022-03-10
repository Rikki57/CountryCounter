package com.countrycounter.controller;

import com.countrycounter.service.CounterInputService;
import com.countrycounter.service.CounterOutputService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CounterController {
    private final CounterOutputService counterOutputService;

    private final CounterInputService counterInputService;

    public CounterController(CounterOutputService counterOutputService, CounterInputService counterInputService) {
        this.counterOutputService = counterOutputService;
        this.counterInputService = counterInputService;
    }

    @Operation(description = "Method for getting statistics of counting countries. Returns list of countries with their counters")
    @GetMapping(value = "/statistics", produces = "application/json")
    public Map<String, Long> getCounters() {
        return counterOutputService.getCounters();
    }

    @Operation(description = "Method for adding value to current country. Country id is the request body")
    @PostMapping(value = "/countryCounter")
    public ResponseEntity<Boolean> add(@RequestBody String country) {
        return ResponseEntity.ok(counterInputService.count(country));
    }
}