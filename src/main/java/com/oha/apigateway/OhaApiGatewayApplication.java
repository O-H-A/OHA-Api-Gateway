package com.oha.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class OhaApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OhaApiGatewayApplication.class, args);
    }

    @GetMapping("/api/gateway/test")
    public Mono<String> test() {
        return Mono.just("gateway");
    }
}
