package com.formulagrid.FormulaGrid.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Year;

@Slf4j
@Component
public class JoplicaApiClient {

    private final WebClient webClient;

    private final Integer current = Year.now().getValue();

    public JoplicaApiClient(@Value("${f1.joplica-api.base-url}") String baseUrl){
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<String> getCurrentSeasonDriverStandings(){
        return webClient.get()
                .uri("/{current}/driverStandings.json", current)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching driver standings: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonDrivers(){
        return webClient.get()
                .uri("/{current}/drivers.json", current)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching drivers: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonConstructorStandings(){
        return webClient.get()
                .uri("/{current}/constructorStandings.json", current)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching constructor standings: {}", error.getMessage()));
    }

    public Mono<String> getCurrentSeasonRaces(){
        return webClient.get()
                .uri("/{current}.json", current)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching races: {}", error.getMessage()));
    }

    //Get race results for a specific season and round /2024/1/results.json
    public Mono<String> getRaceResults(Integer season, Integer round){
        return webClient.get()
                .uri("/{season}/{round}/results.json", season, round)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching race results: {}", error.getMessage()));
    }

    //Get all race results for a season /2024/results.json
    public Mono<String> getSeasonRaceResults(Integer season){
        return webClient.get()
                .uri("/{season}/results.json", season)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching season race results: {}", error.getMessage()));
    }

    //Get qualifying results for a specific season and round /2024/1/qualifying.json
    public Mono<String> getQualifyingResults(Integer season, Integer round){
        return webClient.get()
                .uri("/{season}/{round}/qualifying.json", season, round)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching qualifying results: {}", error.getMessage()));
    }

    //Get current season last race results /current/last/results.json
    public Mono<String> getLastRaceResults(){
        return webClient.get()
                .uri("/{current}/last/results.json", current)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error fetching last race results: {}", error.getMessage()));
    }


}
