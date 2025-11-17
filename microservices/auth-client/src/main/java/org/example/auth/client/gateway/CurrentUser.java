package org.example.auth.client.gateway;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public final class CurrentUser {

    private CurrentUser() {}

    public static Optional<String> userId() {
        return getHeader(GatewayHeaders.USER_ID);
    }

    public static String role() {
        return getHeader(GatewayHeaders.ROLE).orElse("USER");
    }

    public static Optional<String> claim(String name) {
        return getHeader(GatewayHeaders.CLAIM_PREFIX + name);
    }

    private static Optional<String> getHeader(String name) {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(attrs -> attrs.getRequest().getHeader(name))
                .map(String::strip)
                .filter(s -> !s.isEmpty());
    }
}