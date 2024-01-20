package com.oha.apigateway.filter;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CommonServiceFilter extends AbstractGatewayFilterFactory<CommonServiceFilter.Config> {
    private static final Logger logger = LogManager.getLogger(CommonServiceFilter.class);
    public CommonServiceFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(CommonServiceFilter.Config config) {
        return ((exchange, chain) -> {
            if (config.isPreLogger()) {
                logger.info("Common Service Start");
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if (config.isPostLogger()) {
                    logger.info("Common Service End");
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