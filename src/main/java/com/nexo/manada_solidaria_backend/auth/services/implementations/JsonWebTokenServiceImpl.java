package com.nexo.manada_solidaria_backend.auth.services.implementations;

import com.nexo.manada_solidaria_backend.auth.services.interfaces.JsonWebTokenService;
import com.nexo.manada_solidaria_backend.common.exceptions.custom_exceptions.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
@NoArgsConstructor
@Slf4j
public class JsonWebTokenServiceImpl implements JsonWebTokenService {

    @Value("${security.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${security.refresh-token-expiration}")
    private long refreshTokenExpiration;
    @Value("${security.secret}")
    private String secret;

    public enum TokenType {
        ACCESS,
        REFRESH
    }


    @Override
    public String getAccessToken(String subject, Map<String, Object> claims) {
        return buildJwt(
                subject,
                Instant.now().plus(accessTokenExpiration, ChronoUnit.MINUTES),
                claims,
                TokenType.ACCESS
        );
    }

    @Override
    public String getRefreshToken(String subject, Map<String, Object> claims) {
        return buildJwt(
                subject,
                Instant.now().plus(refreshTokenExpiration, ChronoUnit.MINUTES),
                claims,
                TokenType.REFRESH
        );
    }

    @Override
    public String getSubject(String token, TokenType type) {
        return getClaims(token, type).getSubject();
    }

    @Override
    public boolean isValid(String token, TokenType type) {
        Date expiration = getClaims(token, type).getExpiration();
        return new Date().before(expiration);
    }

    private String buildJwt(String subject, Instant expiration, Map<String, Object> claims, TokenType type) {
        return Jwts
                .builder()
                .subject(subject)
                .signWith(getSecretKey())
                .expiration(Date.from(expiration))
                .issuedAt(new Date())
                .claims(claims)
                .claim("token_type", type)
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    private Claims getClaims(String jwt, TokenType type) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSecretKey())
                    .require("token_type", type.name())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
        } catch (SignatureException e) {
            throw new InvalidJwtException("Token is not valid");
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtException("Token is expired");
        } catch (Exception e) {
            log.error("Error decoding token {}", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error decoding token");
        }
    }
}
