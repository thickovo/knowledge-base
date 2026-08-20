package com.gao.knowledgebase.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final String JWT_SECRET = "knowledge-base-secret-key-2026-32bytes";
    private static final Long JWT_EXPIRE = 604800000L;

    public String generateToken(String username){
        String token = Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRE))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes())).compact();
        return token;
    }
    public String getUsernameFromToken(String token){
        return   Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET.getBytes())
                .build().parseClaimsJws(token)
                .getBody().getSubject();
    }
}
