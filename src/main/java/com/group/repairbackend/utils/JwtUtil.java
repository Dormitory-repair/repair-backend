package com.group.repairbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    // 密钥（实际项目中应该从配置文件中读取）
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("dormRepairSystemSecretKey1234567890abcdef".getBytes());

    // Token 有效期：24小时
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000;

    // 生成 JWT 令牌
    public static String generateToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 JWT 令牌，返回 Claims
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 验证令牌是否有效
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
