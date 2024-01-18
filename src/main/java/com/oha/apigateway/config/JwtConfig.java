package com.oha.apigateway.config;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class JwtConfig {

    private final Key key;

    public JwtConfig(@Value("${jwt.secret}") String secretKey) {
        key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    public Claims validateToken(String token) {
        try {
            String type = token.split(" ")[0];
            if(!"Bearer".equalsIgnoreCase(type)) {
                throw new UnsupportedJwtException("Not Bearer Token");
            }
            token = token.split(" ")[1];
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
        } catch (NullPointerException | IllegalArgumentException e) {
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
                "/api/user/**"
              , "/api/auth/**"
              , "/api/posting/swagger-ui/**"
              , "/api/posting/api-docs/**"
              , "/api/diary/swagger**"
        );
    }
}
