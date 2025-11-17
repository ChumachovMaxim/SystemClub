CREATE TABLE IF NOT EXISTS audit_events (
                                            id BIGSERIAL PRIMARY KEY,
                                            principal VARCHAR(255),
    user_id VARCHAR(36),  -- UUID как строка
    ip VARCHAR(45),
    type VARCHAR(50) NOT NULL CHECK (type IN (
                                     'LOGIN_SUCCESS', 'LOGIN_FAILURE',
                                     'REFRESH_TOKEN_NOT_FOUND', 'REFRESH_INVALID_FORMAT', 'REFRESH_FINGERPRINT_MISMATCH'
                                             )),
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_audit_principal ON audit_events(principal);
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON audit_events(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_ip ON audit_events(ip);
CREATE INDEX IF NOT EXISTS idx_audit_type ON audit_events(type);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_events(timestamp DESC);