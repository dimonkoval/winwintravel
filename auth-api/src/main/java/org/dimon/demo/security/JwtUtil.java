package org.dimon.demo.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.dimon.demo.exception.InvalidJwtAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final Key signingKey;
    private final long expirationMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-expiration-time}") long expirationMillis
    ) {
        byte[] keyBytes = hexStringToByteArray(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;

        logger.info("JwtUtil initialized with HEX secret: {}", secret);
    }

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(signingKey)
                .compact();

        logger.debug("Generated token for user {}: {}", username, token);
        return token;
    }

    public String validateAndGetUsername(String token) {
        logger.debug("Validating token: {}", token);

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            logger.debug("Token validated successfully for user: {}", username);
            return username;
        } catch (JwtException e) {
            throw new InvalidJwtAuthenticationException("JWT validation failed", e);
        }
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}