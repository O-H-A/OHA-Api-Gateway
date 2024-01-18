package com.oha.apigateway.filter;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostingServiceFilter extends AbstractGatewayFilterFactory<PostingServiceFilter.Config> {
    private static final Logger logger = LogManager.getLogger(PostingServiceFilter.class);
    public PostingServiceFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (config.isPreLogger()) {
                logger.info("Posting Service Start");
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if (config.isPostLogger()) {
                    logger.info("Posting Service End");
                }
            }));
        });
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}