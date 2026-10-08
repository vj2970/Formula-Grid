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

        List<ConstructorStanding> standings = standingRepository.findBySeasonOrderByPositionAsc(currentSeason);
        if(!standings.isEmpty()){
            log.info("Returning {} constructor from database", standings.size());
            return standings;
        }

        log.info("Fetching constructor standings from Jolplica API");
        return fetchAndSaveStandingsFromApi();
    }

    public List<ConstructorStanding> getConstructorStandings(Integer season){
        List<ConstructorStanding> standings = standingRepository.findBySeasonOrderByPositionAsc(season);
        if (!standings.isEmpty()) return standings;
        log.info("Fetching {} constructor standings from Jolpica", season);
        return fetchAndSaveStandings(jolpicaApiClient.getConstructorStandings(season));
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
                log.warn("No Standings data available");
                return List.of();
            }

            var standingsList = lists.getFirst();
            Integer season = Integer.parseInt(standingsList.getSeason());
            Integer round = Integer.parseInt(standingsList.getRound());

            List<ConstructorStanding> standings = standingsList.getConstructorStandings().stream()
                    .map(s -> convertToConstructorStanding(s, season, round))
                    .toList();

            standingRepository.deleteBySeason(season);
            standingRepository.saveAll(standings);
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
