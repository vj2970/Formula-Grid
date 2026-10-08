package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JolpicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JolpicaConstructorStandingsResponse;
import com.formulagrid.FormulaGrid.exception.ExternalApiException;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.repository.ConstructorRepository;
import com.formulagrid.FormulaGrid.repository.ConstructorStandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.formulagrid.FormulaGrid.exception.ResourceNotFoundException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructorService {
    
    private final ConstructorRepository constructorRepository;
    private final ConstructorStandingRepository standingRepository;
    private final JolpicaApiClient jolpicaApiClient;
    private final ObjectMapper objectMapper;

    public List<Constructor> getCurrentSeasonConstructors() {
        return getCurrentSeasonStandings().stream()
                .map(ConstructorStanding::getConstructor)
                .collect(Collectors.toList());
    }

    public Constructor getConstructorByConstructorId(String constructorId) {
        return constructorRepository.findByConstructorId(constructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Constructor not found: " + constructorId));
    }

    public List<ConstructorStanding> getCurrentSeasonStandings(){
        Integer currentSeason = Year.now().getValue();
        return loadConstructorStandings(currentSeason, jolpicaApiClient.getCurrentSeasonConstructorStandings());
    }

    public List<ConstructorStanding> getConstructorStandings(Integer season){
        List<ConstructorStanding> standings = loadConstructorStandings(season, jolpicaApiClient.getConstructorStandings(season));
        if (standings.isEmpty()) {
            throw new ResourceNotFoundException("No constructor standings available for season " + season);
        }
        return standings;
    }

    public List<ConstructorStanding> refreshConstructorStandingsForSeason(Integer season){
        return fetchAndSaveStandings(jolpicaApiClient.getConstructorStandings(season));
    }

    private List<ConstructorStanding> loadConstructorStandings(Integer season, Mono<String> call) {
        List<ConstructorStanding> cached = standingRepository.findBySeasonOrderByPositionAsc(season);

        if (!cached.isEmpty() && !CachePolicy.isStale(season, cached.getFirst().getFetchedAt())) {
            return cached;
        }

        try {
            return fetchAndSaveStandings(call);
        } catch (ExternalApiException e) {
            if (!cached.isEmpty()) {
                log.warn("Jolpica unavailable, serving cached {} constructor standings", season);
                return cached;
            }
            throw e;
        }
    }

    public List<ConstructorStanding> fetchAndSaveStandingsFromApi(){
        return fetchAndSaveStandings(jolpicaApiClient.getCurrentSeasonConstructorStandings());
    }

    public List<ConstructorStanding> fetchAndSaveStandings(Mono<String> call){
        try{
            String response = call.block();
            JolpicaConstructorStandingsResponse jolpicaResponse = objectMapper.readValue(response, JolpicaConstructorStandingsResponse.class);

            var lists = jolpicaResponse.getMrData().getStandingsTable().getStandingsLists();
            if(lists.isEmpty()){
                assert response != null;
                log.warn("Jolpica returned no constructor standings. Response starts with: {}",
                        response.substring(0, Math.min(300, response.length())));
                return List.of();
            }

            var standingsList = lists.getFirst();
            Integer season = Integer.parseInt(standingsList.getSeason());
            Integer round = Integer.parseInt(standingsList.getRound());
            LocalDateTime fetchedAt = LocalDateTime.now();

            List<ConstructorStanding> standings = standingsList.getConstructorStandings().stream()
                    .map(s -> convertToConstructorStanding(s, season, round))
                    .toList();
            standings.forEach(s -> s.setFetchedAt(fetchedAt));

            standingRepository.deleteBySeason(season);
            standingRepository.saveAll(standings);
            log.info("Saved {} constructor standings for season {} (after round {})",
                    standings.size(), season, round);
            return standings;
        } catch (Exception e) {
            log.error("Error fetching constructor standings", e);
            throw new ExternalApiException("Failed to fetch constructor standings from Jolpica API", e);
        }
    }

    private ConstructorStanding convertToConstructorStanding(
            JolpicaConstructorStandingsResponse.ConstructorStandingInfo standingInfo,
            Integer season, Integer round){

        Constructor constructor = saveOrGetConstructor(standingInfo.getContructor());
        ConstructorStanding standing = new ConstructorStanding();
        standing.setSeason(season);
        standing.setRound(round);
        standing.setPosition(Integer.parseInt(standingInfo.getPosition()));
        standing.setPositionText(standingInfo.getPositionText());
        standing.setPoints(Integer.parseInt(standingInfo.getPoints()));
        standing.setWins(Integer.parseInt(standingInfo.getWins()));
        standing.setConstructor(constructor);

        return standing;
    }

    private Constructor saveOrGetConstructor(JolpicaConstructorStandingsResponse.ConstructorInfo constructorInfo){
        return constructorRepository.findByConstructorId(constructorInfo.getConstructorId())
                .orElseGet(() -> {
                    Constructor newConstructor = new Constructor();
                    newConstructor.setConstructorId(constructorInfo.getConstructorId());
                    newConstructor.setName(constructorInfo.getName());
                    newConstructor.setNationality(constructorInfo.getNationality());
                    newConstructor.setUrl(constructorInfo.getUrl());
                    return constructorRepository.save(newConstructor);
                });
    }
    
    
}
