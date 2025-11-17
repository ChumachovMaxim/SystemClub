package org.example.auth.client.gateway;

public final class GatewayHeaders {
    private GatewayHeaders() {}

    public static final String USER_ID = "X-Jwt-Sub";
    public static final String ROLE = "X-Jwt-Claims-role";
    public static final String CLAIM_PREFIX = "X-Jwt-Claims-";
}