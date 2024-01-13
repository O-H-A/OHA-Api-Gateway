package com.oha.apigateway.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@Slf4j
public class JwtConfig {

    private final Key key;

    public JwtConfig() {
        byte[] secretKeyBytes = Base64.getDecoder().decode("efwkjfejwlfjsdkfjhjkujhgfhnsbfejfuyfgshjkdfgweukfhjkwefgwjkhefgdjsnfjhksdfg");
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public Claims validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        }catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new TokenException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new TokenException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new TokenException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new TokenException("토큰이 없습니다.");
        }
    }

    public static class TokenException extends JwtException {
        public TokenException(String message) {
            super(message);
        }
    }

    public List<String> getSkipPaths() {
        return Arrays.asList(
                "/api/gateway/test"
              , "/api/user/login"
        );
    }
}
