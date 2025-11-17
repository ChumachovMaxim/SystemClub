package org.example.auth.audit.entity;

public enum AuditType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    REFRESH_TOKEN_NOT_FOUND,
    REFRESH_INVALID_FORMAT,
    REFRESH_FINGERPRINT_MISMATCH
}