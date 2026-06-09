package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
@NoArgsConstructor
public class JsonWebTokenServiceImpl implements JsonWebTokenService {

    @Value("${security.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${security.refresh-token-expiration}")
    private long refreshTokenExpiration;
    @Value("${security.secret}")
    private String secret;


    @Override
    public String getAccessToken(String subject, Map<String, Object> claims) {
        return buildJwt(
                subject,
                Instant.now().plus(accessTokenExpiration, ChronoUnit.MINUTES),
                claims
        );
    }

    @Override
    public String getRefreshToken(String subject, Map<String, Object> claims) {
        return buildJwt(
                subject,
                Instant.now().plus(refreshTokenExpiration, ChronoUnit.MINUTES),
                claims
        );
    }

    @Override
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public boolean isValid(String token) {
        Date expiration = getClaims(token).getExpiration();
        return new Date().before(expiration);
    }

    private String buildJwt(String subject, Instant expiration, Map<String, Object> claims) {
        return Jwts
                .builder()
                .subject(subject)
                .signWith(getSecretKey())
                .expiration(Date.from(expiration))
                .issuedAt(new Date())
                .claims(claims)
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    private Claims getClaims(String jwt) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
