package org.example.studio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "studio_users")
@Data
public class StudioUser {
    @Id
    private UUID userId;

    public StudioUser() {}

    public StudioUser(String userId) {
        this.userId = UUID.fromString(userId);
    }
}