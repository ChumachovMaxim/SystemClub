package org.example.studio.repository;

import org.example.studio.entity.StudioUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudioUserRepository extends JpaRepository<StudioUser, UUID> {
}