package org.example.studio.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricsConfig {

    private final MeterRegistry meterRegistry;

    public CustomMetricsConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        meterRegistry.counter("studio.created");
        meterRegistry.counter("studio.updated");
        meterRegistry.counter("studio.deleted");
        meterRegistry.counter("studio.post.added");
        meterRegistry.counter("studio.admin.added");
        meterRegistry.counter("studio.admin.removed");
        meterRegistry.counter("studio.followed");
        meterRegistry.counter("studio.unfollowed");
    }
}