package org.example.auth.config;

import org.example.auth.filter.JwtFilter;
import org.example.auth.filter.RequestIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final RequestIdFilter requestIdFilter;

    public SecurityConfig(JwtFilter jwtFilter, RequestIdFilter requestIdFilter) {
        this.jwtFilter = jwtFilter;
        this.requestIdFilter = requestIdFilter;
    }


    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allow-credentials:false}")
    private boolean allowCredentials;


///*Включить при HTTPS*///
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(c -> c.configurationSource(corsConfigurationSource()))
//                .csrf(AbstractHttpConfigurer::disable)
//                .headers(headers -> headers
//                        // HSTS — принудительный HTTPS
//                        .httpStrictTransportSecurity(hsts -> hsts
//                                .maxAgeInSeconds(31536000)
//                                .includeSubDomains(true)
//                                .preload(true)
//                        )
//                        // CSP — защита от XSS
//                        .contentSecurityPolicy(csp -> csp
//                                .policyDirectives(
//                                        "default-src 'self'; " +
//                                                "script-src 'self' 'unsafe-inline'; " +
//                                                "style-src 'self' 'unsafe-inline'; " +
//                                                "img-src 'self' data:; " +
//                                                "connect-src 'self' ${ALLOWED_API:}; " +
//                                                "frame-ancestors 'none';"
//                                )
//                        )
//                        // Защита от MIME-сниффинга
//                        .ContentTypeOptions(opt -> opt.enabled(true))
//                        // Запрет кликов по ссылкам из iframe
//                        .frameOptions(frame -> frame.deny())
//                        // Блокировка XSS в браузере
//                        .xssProtection(xss -> xss
//                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_BLOCK)
//                        )
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // === DEV ONLY (в prod — выключено через профиль) ===
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/.well-known/jwks.json").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(requestIdFilter, SecurityContextHolderFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}