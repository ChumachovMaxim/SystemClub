// src/main/java/org/example/auth/audit/service/AuditService.java
package org.example.auth.audit.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.audit.entity.AuditEvent;
import org.example.auth.audit.entity.AuditType;
import org.example.auth.audit.repository.AuditEventRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditEventRepository repository;

    public void log(String principal, String userId, String ip, AuditType type) {
        AuditEvent event = new AuditEvent();
        event.setPrincipal(principal != null ? principal : "unknown");
        event.setUserId(userId);
        event.setIp(ip);
        event.setType(type.name());
        repository.save(event);
    }

    public void log(String principal, String ip, AuditType type) {
        log(principal, null, ip, type);
    }
}