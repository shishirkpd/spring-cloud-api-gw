package com.skp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    private final WebClient webClient;

    public GatewayConfig(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions.route()
                .GET("/api/service1/**", this::handleService1Request)
                .GET("/api/service2/**", this::handleService2Request)
                .build();
    }

    private Mono<ServerResponse> handleService1Request(ServerRequest request) {
        String backendServiceUrl = "http://localhost:8081" + request.uri().getPath();
        return webClient.get()
                .uri(backendServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private Mono<ServerResponse> handleService2Request(ServerRequest request) {
        String backendServiceUrl = "http://localhost:8082" + request.uri().getPath();
        return webClient.get()
                .uri(backendServiceUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Bean
    @Lazy
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
