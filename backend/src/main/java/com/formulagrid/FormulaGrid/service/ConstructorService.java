package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaConstructorStandingsResponse;
import com.formulagrid.FormulaGrid.exception.ExternalApiException;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.repository.ConstructorRepository;
import com.formulagrid.FormulaGrid.repository.ConstructorStandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConstructorService {
    
    private final ConstructorRepository constructorRepository;
    private final ConstructorStandingRepository standingRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;


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

    public List<ConstructorStanding> fetchAndSaveStandingsFromApi(){
        try {
            String response = joplicaApiClient.getCurrentSeasonConstructorStandings().block();
            JoplicaConstructorStandingsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaConstructorStandingsResponse.class);

            if(joplicaResponse.getMrData().getStandingsTable().getStandingsLists().isEmpty()){
                log.warn("No standings data available");
                return List.of();
            }

            JoplicaConstructorStandingsResponse.StandingsList standingsList =
                    joplicaResponse.getMrData().getStandingsTable().getStandingsLists().get(0);

            List<ConstructorStanding> standings = standingsList.getConstructorStandings().stream()
                    .map(s -> convertToConstructorStanding(s, Integer.parseInt(standingsList.getSeason()),
                            Integer.parseInt(standingsList.getRound())))
                    .collect(Collectors.toList());

            standingRepository.saveAll(standings);
            log.info("Saved {} constructor standings to database", standings.size());

            return standings;

        } catch (Exception e) {
            log.error("Error fetching constructor standings from API", e);
            throw new ExternalApiException("Failed to fetch constructor standings from Joplica API", e);
        }
    }

    private ConstructorStanding convertToConstructorStanding(
            JoplicaConstructorStandingsResponse.ConstructorStandingInfo standingInfo,
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

    private Constructor saveOrGetConstructor(JoplicaConstructorStandingsResponse.ConstructorInfo constructorInfo){
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
