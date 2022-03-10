package com.countrycounter.repository;

import java.util.Map;

public interface CounterOutput {
    Map<String, Long> getValues();
}
