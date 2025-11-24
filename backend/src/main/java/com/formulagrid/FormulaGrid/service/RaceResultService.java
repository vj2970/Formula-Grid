package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaConstructorStandingsResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaDriverResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaRaceResultsResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaRaceScheduleResponse;
import com.formulagrid.FormulaGrid.model.Circuit;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.model.RaceResult;
import com.formulagrid.FormulaGrid.repository.ConstructorRepository;
import com.formulagrid.FormulaGrid.repository.DriverRepository;
import com.formulagrid.FormulaGrid.repository.RaceResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaceResultService {

    private final RaceResultRepository raceResultRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;

    /**
     * Get race results for a specific season and round
     */
    public List<RaceResult> getRaceResults(Integer season, Integer round){
        List<RaceResult> results = raceResultRepository
                .findBySeasonAndRoundOrderByPositionAsc(season, round);

        if(!results.isEmpty()){
            log.info("Returning {} race results from database for season {} round {}",
                    results.size(), season, round);
            return results;
        }

        log.info("Fetching race results from API for season {} round {}", season, round);
        return fetchAndSaveRaceResults(season, round);
    }

    /**
     * Get last race results for current season
     */
    public List<RaceResult> getLastRaceResults(){
        try{
            String response = joplicaApiClient.getLastRaceResults().block();
            JoplicaRaceResultsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaRaceResultsResponse.class);
            if(joplicaResponse.getMrData().getRaceTable().getRaces().isEmpty()){
                log.warn("No last race results available");
                return List.of();
            }
            JoplicaRaceResultsResponse.RaceInfo raceInfo =
                    joplicaResponse.getMrData().getRaceTable().getRaces().get(0);

            Integer season = Integer.parseInt(raceInfo.getSeason());
            Integer round = Integer.parseInt(raceInfo.getRound());

            List<RaceResult> existing = raceResultRepository
                    .findBySeasonAndRoundOrderByPositionAsc(season, round);
            if(!existing.isEmpty()){
                return existing;
            }
            List<RaceResult> results = parseRaceResults(raceInfo);
            raceResultRepository.saveAll(results);

            log.info("Saved {} race results for season {} round {}",
                    results.size(), season, round);
            return results;

        } catch (Exception e) {
            log.error("Error fetching last race results", e);
            throw new RuntimeException("Failed to fetch last race results", e);
        }
    }

    /**
     * Get all race results for a season
     */
    public List<RaceResult> getSeasonRaceResults(Integer season){
        List<RaceResult> results = raceResultRepository
                .findBySeasonOrderByRoundAscPositionAsc(season);
        if(!results.isEmpty()){
            log.info("Returning {} race results from database for season {}",
                    results.size(), season);
            return results;
        }

        log.info("Fetching season race results from API for season {}", season);
        return fetchAndSaveSeasonRaceResults(season);
    }

    /**
     * Get driver's race history
     */
    public List<RaceResult> getDriverRaceHistory(String driverId){
        return raceResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId);
    }

    /**
     * Get driver's wins
     */
    public List<RaceResult> getDriverWins(String driverId){
        return raceResultRepository
                .findByDriver_DriverIdAndPositionOrderBySeasonDescRoundDesc(driverId, 1);
    }

    /**
     * Get driver's podium finishes
     */
    public List<RaceResult> getDriverPodiums(String driverId){
        return raceResultRepository
                .findByDriver_DriverIdAndPositionOrderBySeasonDescRoundDesc(driverId, 3);
    }

    /**
     * Refresh race results for a specific race
     */
    public List<RaceResult> refreshRaceResults(Integer season, Integer round){
        List<RaceResult> existing = raceResultRepository
                .findBySeasonAndRoundOrderByPositionAsc(season, round);
        if(!existing.isEmpty()){
            raceResultRepository.deleteAll(existing);
            log.info("Deleted {} existing race results for season {} round{}",
                    existing.size(), season, round);
        }

        return fetchAndSaveRaceResults(season, round);
    }

    /**
     * Fetch and save race results from API
     */
    private List<RaceResult> fetchAndSaveRaceResults(Integer season, Integer round){
        try{
            String response = joplicaApiClient.getRaceResults(season, round).block();
            JoplicaRaceResultsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaRaceResultsResponse.class);
            if(joplicaResponse.getMrData().getRaceTable().getRaces().isEmpty()){
                log.warn("No race results available for season {} round {}", season, round);
                return List.of();
            }
            JoplicaRaceResultsResponse.RaceInfo raceInfo =
                    joplicaResponse.getMrData().getRaceTable().getRaces().get(0);

            List<RaceResult> results = parseRaceResults(raceInfo);
            raceResultRepository.saveAll(results);
            log.info("Saved {} race results for season {} round {}",
                    results.size(), season, round);

            return results;

        } catch (Exception e) {
            log.error("Error fetching race results from API", e);
            throw new RuntimeException("Failed to fetch race results", e);
        }
    }

    /**
     * Fetch and save all race results for a season
     */
    private List<RaceResult> fetchAndSaveSeasonRaceResults(Integer season){
        try{
            String response = joplicaApiClient.getSeasonRaceResults(season).block();
            JoplicaRaceResultsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaRaceResultsResponse.class);

            List<RaceResult> allResults = joplicaResponse.getMrData()
                    .getRaceTable()
                    .getRaces()
                    .stream()
                    .flatMap(raceInfo -> parseRaceResults(raceInfo).stream())
                    .collect(Collectors.toList());

            raceResultRepository.saveAll(allResults);
            log.info("Saved {} race results for season {}", allResults.size(), season);
            return allResults;
        } catch (Exception e) {
            log.error("Error fetching season race results from API", e);
            throw new RuntimeException("Failed to fetch season race results", e);
        }
    }

    /**
     * Parse race results from Ergast response
     */
    private List<RaceResult> parseRaceResults(JoplicaRaceResultsResponse.RaceInfo raceInfo){
        return raceInfo.getResults().stream()
                .map(resultInfo -> convertToRaceResult(resultInfo, raceInfo))
                .collect(Collectors.toList());
    }

    /**
     * Convert Ergast result to RaceResult model
     */
    private RaceResult convertToRaceResult(
            JoplicaRaceResultsResponse.ResultInfo resultInfo,
            JoplicaRaceResultsResponse.RaceInfo raceInfo){
        RaceResult result = new RaceResult();

        // Race info
        result.setSeason(Integer.parseInt(raceInfo.getSeason()));
        result.setRound(Integer.parseInt(raceInfo.getRound()));
        result.setRaceName(raceInfo.getRaceName());
        result.setCircuit(convertToCircuit(raceInfo.getCircuit()));

        // Position and points
        result.setPosition(Integer.parseInt(resultInfo.getPosition()));
        result.setPositionText(resultInfo.getPositionText());
        result.setPoints(Integer.parseInt(resultInfo.getPoints()));

        // Driver and Constructor
        result.setDriver(saveOrGetDriver(resultInfo.getDriver()));
        result.setConstructor(saveOrGetConstructor(resultInfo.getConstructor()));

        // Race statistics
        result.setGrid(parseIntOrNull(resultInfo.getGrid()));
        result.setLaps(parseIntOrNull(resultInfo.getLaps()));
        result.setStatus(resultInfo.getStatus());

        // Race time
        if (resultInfo.getTime() != null) {
            result.setTime(resultInfo.getTime().getTime());
            result.setMilliseconds(parseIntOrNull(resultInfo.getTime().getMillis()));
        }

        // Fastest lap
        if (resultInfo.getFastestLap() != null) {
            result.setFastestLap(resultInfo.getFastestLap().getLap());
            result.setRank(parseIntOrNull(resultInfo.getFastestLap().getRank()));

            if (resultInfo.getFastestLap().getTime() != null) {
                result.setFastestLapTime(resultInfo.getFastestLap().getTime().getTime());
            }

            if (resultInfo.getFastestLap().getAverageSpeed() != null) {
                result.setFastestLapSpeed(
                        resultInfo.getFastestLap().getAverageSpeed().getSpeed());
            }
        }
        return result;
    }

    /**
     *Convert circuit info (reuse from RaceService if needed)
     */
    private Circuit convertToCircuit(JoplicaRaceScheduleResponse.CircuitInfo circuitInfo) {
        Circuit circuit = new Circuit();
        circuit.setCircuitId(circuitInfo.getCircuitId());
        circuit.setCircuitName(circuitInfo.getCircuitName());
        circuit.setUrl(circuitInfo.getUrl());

        if (circuitInfo.getLocation() != null) {
            circuit.setLocality(circuitInfo.getLocation().getLocality());
            circuit.setCountry(circuitInfo.getLocation().getCountry());
            circuit.setLat(parseDoubleOrNull(circuitInfo.getLocation().getLat()));
            circuit.setLng(parseDoubleOrNull(circuitInfo.getLocation().getLng()));
        }

        return circuit;
    }

    /**
     * Save or get existing driver
     */
    private Driver saveOrGetDriver(JoplicaDriverResponse.DriverInfo driverInfo) {
        return driverRepository.findByDriverId(driverInfo.getDriverId())
                .orElseGet(() -> {
                    Driver driver = new Driver();
                    driver.setDriverId(driverInfo.getDriverId());
                    driver.setCode(driverInfo.getCode());
                    driver.setPermanentNumber(driverInfo.getPermanentNumber());
                    driver.setGivenName(driverInfo.getGivenName());
                    driver.setFamilyName(driverInfo.getFamilyName());
                    driver.setDateOfBirth(driverInfo.getDateOfBirth());
                    driver.setNationality(driverInfo.getNationality());
                    driver.setUrl(driverInfo.getUrl());
                    return driverRepository.save(driver);
                });
    }

    /**
     * Save or get existing constructor
     */
    private Constructor saveOrGetConstructor(
            JoplicaConstructorStandingsResponse.ConstructorInfo constructorInfo) {
        return constructorRepository.findByConstructorId(constructorInfo.getConstructorId())
                .orElseGet(() -> {
                    Constructor constructor = new Constructor();
                    constructor.setConstructorId(constructorInfo.getConstructorId());
                    constructor.setName(constructorInfo.getName());
                    constructor.setNationality(constructorInfo.getNationality());
                    constructor.setUrl(constructorInfo.getUrl());
                    return constructorRepository.save(constructor);
                });
    }

    /**
     * Helper: Parse integer or return null
     */
    private Integer parseIntOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer value: {}", value);
            return null;
        }
    }

    /**
     * Helper: Parse double or return null
     */
    private Double parseDoubleOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse double value: {}", value);
            return null;
        }
    }

}
