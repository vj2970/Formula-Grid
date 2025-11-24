package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaConstructorStandingsResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaDriverResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaQualifyingResultsResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaRaceScheduleResponse;
import com.formulagrid.FormulaGrid.model.Circuit;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.model.QualifyingResult;
import com.formulagrid.FormulaGrid.repository.ConstructorRepository;
import com.formulagrid.FormulaGrid.repository.DriverRepository;
import com.formulagrid.FormulaGrid.repository.QualifyingResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualifyingResultsService {

    private final QualifyingResultRepository qualifyingResultRepository;
    private final DriverRepository driverRepository;
    private final ConstructorRepository constructorRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;

    /**
     * Get qualifying results for a specific season and round
     */
    public List<QualifyingResult> getQualifyingResults(Integer season, Integer round){
        List<QualifyingResult> results = qualifyingResultRepository
                .findBySeasonAndRoundOrderByPositionAsc(season, round);
        if(!results.isEmpty()){
            log.info("Returning {} qualifying results from database for season {} round {}",
                    results.size(), season, round);
            return results;
        }
        log.info("Fetching qualifying results from API for season {} round {}", season, round);
        return fetchAndSaveQualifyingResults(season, round);
    }

    /**
     * Get driver's pole positions
     */
    public List<QualifyingResult> getDriverPolePositions(String driverId) {
        return qualifyingResultRepository
                .findByDriver_DriverIdAndPositionOrderBySeasonDescRoundDesc(driverId, 1);
    }

    /**
     * Get driver's qualifying history
     */
    public List<QualifyingResult> getDriverQualifyingHistory(String driverId) {
        return qualifyingResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId);
    }

    /**
     * Refresh qualifying results
     */
    public List<QualifyingResult> refreshQualifyingResults(Integer season, Integer round) {
        List<QualifyingResult> existing = qualifyingResultRepository
                .findBySeasonAndRoundOrderByPositionAsc(season, round);

        if (!existing.isEmpty()) {
            qualifyingResultRepository.deleteAll(existing);
            log.info("Deleted {} existing qualifying results for season {} round {}",
                    existing.size(), season, round);
        }

        return fetchAndSaveQualifyingResults(season, round);
    }

    /**
     * Fetch and save qualifying results from API
     */
    private List<QualifyingResult> fetchAndSaveQualifyingResults(Integer season, Integer round) {
        try {
            String response = joplicaApiClient.getQualifyingResults(season, round).block();
            JoplicaQualifyingResultsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaQualifyingResultsResponse.class);

            if (joplicaResponse.getMrData().getRaceTable().getRaces().isEmpty()) {
                log.warn("No qualifying results available for season {} round {}", season, round);
                return List.of();
            }

            JoplicaQualifyingResultsResponse.RaceInfo raceInfo =
                    joplicaResponse.getMrData().getRaceTable().getRaces().get(0);

            List<QualifyingResult> results = raceInfo.getQualifyingResults().stream()
                    .map(qualInfo -> convertToQualifyingResult(qualInfo, raceInfo))
                    .collect(Collectors.toList());

            qualifyingResultRepository.saveAll(results);
            log.info("Saved {} qualifying results for season {} round {}",
                    results.size(), season, round);

            return results;
        } catch (Exception e) {
            log.error("Error fetching qualifying results from API", e);
            throw new RuntimeException("Failed to fetch qualifying results", e);
        }
    }

    /**
     * Convert to QualifyingResult model
     */
    private QualifyingResult convertToQualifyingResult(
            JoplicaQualifyingResultsResponse.QualifyingInfo qualInfo,
            JoplicaQualifyingResultsResponse.RaceInfo raceInfo) {

        QualifyingResult result = new QualifyingResult();

        result.setSeason(Integer.parseInt(raceInfo.getSeason()));
        result.setRound(Integer.parseInt(raceInfo.getRound()));
        result.setRaceName(raceInfo.getRaceName());
        result.setCircuit(convertToCircuit(raceInfo.getCircuit()));

        result.setPosition(Integer.parseInt(qualInfo.getPosition()));
        result.setDriver(saveOrGetDriver(qualInfo.getDriver()));
        result.setConstructor(saveOrGetConstructor(qualInfo.getConstructor()));

        result.setQ1(qualInfo.getQ1());
        result.setQ2(qualInfo.getQ2());
        result.setQ3(qualInfo.getQ3());

        return result;
    }

    // Helper methods (same as RaceResultService)
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
