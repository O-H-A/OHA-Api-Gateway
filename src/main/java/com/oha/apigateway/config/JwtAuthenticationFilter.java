package com.oha.apigateway.config;


import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtConfig jwtConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        String requestPath = String.valueOf(exchange.getRequest().getPath());
        // 인증이 필요하지 않은 요청
        if (matchesAnyPath(requestPath, jwtConfig.getSkipPaths())) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        try {
            Claims claims = jwtConfig.validateToken(token);
            List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("role")).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(claims.get("username"), null, authorities);
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        } catch (JwtConfig.TokenException e) {
            exchange.getAttributes().put("jwtError", e.getMessage());
            return chain.filter(exchange);
        }
    }

    private boolean matchesAnyPath(String requestPath, List<String> skipPaths) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String path : skipPaths) {
            if(pathMatcher.match(path, requestPath))
                return true;
        }
        return false;
    }
}
