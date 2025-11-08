package com.formulagrid.FormulaGrid.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JoplicaApiClient {

    private final WebClient webClient;

    public JoplicaApiClient(@Value("${f1.joplica-api.base-url}") String baseUrl){
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> getCurrentSeasonDriverStandings(){
        return webClient.get()
                .uri("/current/driverStandings.json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching driver standings: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonDrivers(){
        return webClient.get()
                .uri("/current/drivers.json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching drivers: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonConstructorStandings(){
        return webClient.get()
                .uri("/current/constructorStandings.json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching constructor standings: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonRaces(){
        return webClient.get()
                .uri("/current.json")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching races: {}", error.getMessage()));
    }

}
