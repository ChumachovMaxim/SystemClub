package org.example.auth.audit.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "audit_events")
@Data
public class AuditEvent {
    @Id @GeneratedValue private Long id;
    private String principal;
    private String userId;
    private String ip;
    private String type;
    private Instant timestamp = Instant.now();
}