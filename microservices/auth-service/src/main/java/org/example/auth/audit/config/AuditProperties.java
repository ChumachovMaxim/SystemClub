package org.example.auth.audit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit")
public record AuditProperties(
    boolean enabled,
    int retentionDays
) {}