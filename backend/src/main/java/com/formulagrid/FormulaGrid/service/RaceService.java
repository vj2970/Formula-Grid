package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaRaceScheduleResponse;
import com.formulagrid.FormulaGrid.model.Circuit;
import com.formulagrid.FormulaGrid.model.Race;
import com.formulagrid.FormulaGrid.repository.RaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceService {

    private final RaceRepository raceRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;

    public List<Race> getCurrentSeasonRaces(){
        Integer currentSeason = Year.now().getValue();
        List<Race> races = raceRepository.findBySeasonOrderByRoundAsc(currentSeason);

        if(!races.isEmpty()){
            log.info("Returning {} races from database", races.size());
            return races;
        }

        log.info("Fetching races from Joplica API");
        return fetchAndSaveRacesFromApi();
    }

    public List<Race> fetchAndSaveRacesFromApi(){
        try {
            String response = joplicaApiClient.getCurrentSeasonRaces().block();
            JoplicaRaceScheduleResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaRaceScheduleResponse.class);

            List<Race> races = joplicaResponse.getMrData().getRaceTable().getRaces().stream()
                    .map(this::converToRace)
                    .collect(Collectors.toList());

            raceRepository.saveAll(races);
            log.info("Saved {} races to database", races.size());
            return races;
        } catch (Exception e) {
            log.error("Error fetching races from API", e);
            throw new RuntimeException("Failed to fetch races", e);
        }
    }

    private Race converToRace(JoplicaRaceScheduleResponse.RaceInfo raceInfo){
        Race race = new Race();
        race.setSeason(Integer.parseInt(raceInfo.getSeason()));
        race.setRound(Integer.parseInt(raceInfo.getRound()));
        race.setRaceName(raceInfo.getRaceName());
        race.setDate(LocalDate.parse(raceInfo.getDate()));
        race.setUrl(raceInfo.getUrl());
        race.setCircuit(convertToCircuit(raceInfo.getCircuit()));

        if (raceInfo.getTime() != null){
            race.setTime(parseTimeOrNull(raceInfo.getTime()));
        }
        if (raceInfo.getFirstPractice() != null){
            race.setFirstPracticeDate(LocalDate.parse(raceInfo.getFirstPractice().getDate()));
            if (raceInfo.getFirstPractice().getTime() != null){
                race.setTime(parseTimeOrNull(raceInfo.getFirstPractice().getTime()));
            }
        }
        if (raceInfo.getQualifying() != null) {
            race.setQualifyingDate(LocalDate.parse(raceInfo.getQualifying().getDate()));
            if (raceInfo.getQualifying().getTime() != null) {
                race.setQualifyingTime(parseTimeOrNull(raceInfo.getQualifying().getTime()));
            }
        }if (raceInfo.getSprint() != null) {
            race.setSprintDate(LocalDate.parse(raceInfo.getSprint().getDate()));
            if (raceInfo.getSprint().getTime() != null) {
                race.setSprintTime(parseTimeOrNull(raceInfo.getSprint().getTime()));
            }
        }
        if (raceInfo.getSprintQualifying() != null) {
            race.setSprintQualifyingDate(LocalDate.parse(raceInfo.getSprintQualifying().getDate()));
            if (raceInfo.getSprintQualifying().getTime() != null) {
                race.setSprintQualifyingTime(parseTimeOrNull(raceInfo.getSprintQualifying().getTime()));
            }
        }

        return race;
    }

    private Circuit convertToCircuit(JoplicaRaceScheduleResponse.CircuitInfo circuitInfo) {
        Circuit circuit = new Circuit();
        circuit.setCircuitId(circuitInfo.getCircuitId());
        circuit.setCircuitName(circuitInfo.getCircuitName());
        circuit.setUrl(circuitInfo.getUrl());
        if(circuitInfo.getLocation() != null){
            circuit.setLocality(circuitInfo.getLocation().getLocality());
            circuit.setCountry(circuitInfo.getLocation().getCountry());
            if (circuitInfo.getLocation().getLat() != null && !circuitInfo.getLocation().getLat().isEmpty()) {
                try {
                    circuit.setLat(Double.parseDouble(circuitInfo.getLocation().getLat()));
                } catch (NumberFormatException e) {
                    log.warn("Invalid latitude for circuit: {}", circuitInfo.getCircuitId());
                    circuit.setLat(null);
                }
            }

            if (circuitInfo.getLocation().getLng() != null && !circuitInfo.getLocation().getLng().isEmpty()) {
                try {
                    circuit.setLng(Double.parseDouble(circuitInfo.getLocation().getLng()));
                } catch (NumberFormatException e) {
                    log.warn("Invalid longitude for circuit: {}", circuitInfo.getCircuitId());
                    circuit.setLng(null);
                }
            }
        }
        return circuit;
    }

    private LocalTime parseTimeOrNull(String timeString){
        if(timeString == null || timeString.trim().isEmpty()){
            return null;
        }
        try{
            String cleanTime = timeString.replace("Z","");
            if(cleanTime.length() >= 8){
                cleanTime = cleanTime.substring(0, 8);
            }
            return LocalTime.parse(cleanTime);
        } catch (Exception e){
            log.warn("Failed to parse time: {}", timeString);
            return null;
        }
    }

}
