package com.oha.apigateway.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Configuration
@Slf4j
public class JwtConfig {

    private final Key key;

    public JwtConfig(@Value("${jwt.secret}") String secretKey) {
        byte[] secretKeyBytes = Base64.getMimeDecoder().decode(secretKey);
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
            throw new TokenException("세션이 만료되었습니다. 다시 로그인해 주세요.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new TokenException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new TokenException("접근 권한이 없습니다. 로그인 후 이용해주세요.");
        }
    }

    public static class TokenException extends JwtException {
        public TokenException(String message) {
            super(message);
        }
    }

    public List<String> getSkipPaths() {
        return Arrays.asList(
                "/**"
        );
    }
}
