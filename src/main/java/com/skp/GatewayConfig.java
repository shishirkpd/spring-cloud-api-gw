package com.skp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public WebClient webClient() {
        return WebClient.create("");
    }

    @Bean
    public RouterFunction<ServerResponse> route(WebClient webClient) {
        return RouterFunctions.route()
                .GET("/products/**", this::handleService1Request)
                .GET("/users/**", this::handleService2Request)
                .build();
    }

    private Mono<ServerResponse> handleService1Request(ServerRequest request) {
        String backendServiceUrl = "https://dummyjson.com" + request.uri().getPath();
        WebClient webClient = WebClient.create(); // Create a new WebClient instance
        logger.info("Routing the url to: {}", backendServiceUrl);
        return webClient.get()
                .uri(backendServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private Mono<ServerResponse> handleService2Request(ServerRequest request) {
        String backendServiceUrl = "https://dummyjson.com" + request.uri().getPath();
        WebClient webClient = WebClient.create(); // Create a new WebClient instance
        logger.info("Routing the url to: {}", backendServiceUrl);
        return webClient.get()
                .uri(backendServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
