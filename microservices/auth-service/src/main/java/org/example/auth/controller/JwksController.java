package org.example.auth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
public class JwksController {

    @Value("classpath:keys/public.pem")
    private Resource publicKeyResource;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getKey() throws Exception {
        String pem = new String(publicKeyResource.getInputStream().readAllBytes());
        String key = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);

        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(publicKey.getModulus().toByteArray());

        return Map.of("keys", List.of(Map.of(
                "kty", "RSA",
                "use", "sig",
                "kid", "2025-main",
                "alg", "RS256",
                "n", n,
                "e", "AQAB"
        )));
    }
}