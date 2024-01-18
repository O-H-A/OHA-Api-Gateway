package com.oha.apigateway.filter;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    private static final Logger logger = LogManager.getLogger(GlobalFilter.class);
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (config.isPreLogger()) {
                logger.info("-------------------------------------------------------------------------");
                logger.info("["+ exchange.getRequest().getId() + "] Incoming request " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getPath()+exchange.getRequest().getQueryParams() + " from " + exchange.getRequest().getRemoteAddress());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if (config.isPostLogger()) {
                    logger.info("["+ exchange.getRequest().getId() + "] Outgoing response status "  + exchange.getResponse().getStatusCode());
                    logger.info("-------------------------------------------------------------------------");
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