package com.order.platform.assester.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * author: user,
 * date: 24.09.2026
 */

@Lazy
@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long EXPIRATION_TIME;

    public JwtService(
            @Value("${jwt.secret}") String SECRET_STRING,
            @Value("${jwt.expiration}") long EXPIRATION_TIME
    ) {
        this.secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
        this.EXPIRATION_TIME = EXPIRATION_TIME;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }
}