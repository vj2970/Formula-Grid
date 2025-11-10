package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaConstructorStandingsResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaDriverResponse;
import com.formulagrid.FormulaGrid.dto.response.JoplicaDriverStandingsResponse;
import com.formulagrid.FormulaGrid.exception.ExternalApiException;
import com.formulagrid.FormulaGrid.model.Constructor;
import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.model.DriverStanding;
import com.formulagrid.FormulaGrid.repository.ConstructorRepository;
import com.formulagrid.FormulaGrid.repository.DriverRepository;
import com.formulagrid.FormulaGrid.repository.DriverStandingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorRepository constructorRepository;

    public List<Driver> getCurrentSeasonDrivers(){

        //Check for drivers in DB first
        List<Driver> driversFromDb = driverRepository.findAll();
        if(!driversFromDb.isEmpty()){
            log.info("Returning {} drivers from database", driversFromDb.size());
            return driversFromDb;
        }

        //Fetch drivers from API if not in DB
        log.info("Fetching drivers from Ergast API");
        return fetchAndSaveDriversFromApi();
    }

    public List<Driver> fetchAndSaveDriversFromApi(){
        try {
            String response = joplicaApiClient.getCurrentSeasonDrivers().block();
            JoplicaDriverResponse joplicaResponse = objectMapper.readValue(response, JoplicaDriverResponse.class);

            List<Driver> drivers = joplicaResponse.getMrData().getDriverTable().getDrivers().stream()
                    .map(this::convertToDriver)
                    .collect(Collectors.toList());

            driverRepository.saveAll(drivers);
            log.info("Saved {} drivers to database", drivers.size());
            return drivers;
        } catch (Exception e) {
            log.error("Error fetching drivers from API", e);
            throw new ExternalApiException("Failed to fetch drivers from Joplica API", e);
        }
    }

    private Driver convertToDriver(JoplicaDriverResponse.DriverInfo driverInfo){
        Driver driver = new Driver();
        driver.setDriverId(driverInfo.getDriverId());
        driver.setCode(driverInfo.getCode());
        driver.setPermanentNumber(driverInfo.getPermanentNumber());
        driver.setGivenName(driverInfo.getGivenName());
        driver.setFamilyName(driverInfo.getFamilyName());
        driver.setDateOfBirth(driverInfo.getDateOfBirth());
        driver.setNationality(driverInfo.getNationality());
        driver.setUrl(driverInfo.getUrl());
        return driver;
    }

    public List<DriverStanding> getCurrentSeasonDriverStandings(){
        Integer currentSeason = Year.now().getValue();
        List<DriverStanding> standings = driverStandingRepository.findBySeasonOrderByPositionAsc(currentSeason);
        if(!standings.isEmpty()){
            log.info("Returnin {} driver standings from database", standings.size());
            return standings;
        }

        log.info("Fetching driver standings from Joplica API");
        return fetchAndSaveDriverStandingsFromApi();
    }

    public List<DriverStanding> fetchAndSaveDriverStandingsFromApi(){
        try{
            String response = joplicaApiClient.getCurrentSeasonDriverStandings().block();
            JoplicaDriverStandingsResponse joplicaResponse =
                    objectMapper.readValue(response, JoplicaDriverStandingsResponse.class);

            if(joplicaResponse.getMrData().getStandingsTable().getStandingsLists().isEmpty()){
                log.warn("No driver standings data available");
                return List.of();
            }

            JoplicaDriverStandingsResponse.StandingsList standingsList =
                    joplicaResponse.getMrData().getStandingsTable().getStandingsLists().get(0);
            List<DriverStanding> standings = standingsList.getDriverStandings().stream()
                    .map(s -> convertToDriverStanding(s, Integer.parseInt(standingsList.getSeason()),
                    Integer.parseInt(standingsList.getRound())))
                    .collect(Collectors.toList());

            driverStandingRepository.saveAll(standings);
            log.info("Saved {} driver standings to database", standings.size());

            return standings;
        } catch (Exception e) {
            log.error("Error fetching driver standings from API", e);
            throw new ExternalApiException("Failed to fetch drivers standings from Joplica API", e);
        }
    }

    private DriverStanding convertToDriverStanding(
            JoplicaDriverStandingsResponse.DriverStandingInfo standingInfo,
            Integer season, Integer round
    ){
        Driver driver = saveOrGetDriver(standingInfo.getDriver());
        Constructor constructor = null;
        if(!standingInfo.getConstructor().isEmpty()){
            constructor = saveOrGetConstructor(standingInfo.getConstructor().get(0));
        }

        DriverStanding standing = new DriverStanding();
        standing.setSeason(season);
        standing.setRound(round);
        standing.setPosition(Integer.parseInt(standingInfo.getPosition()));
        standing.setPositionText(standingInfo.getPositionText());
        standing.setPoints(Integer.parseInt(standingInfo.getPoints()));
        standing.setWins(Integer.parseInt(standingInfo.getWins()));
        standing.setDriver(driver);
        standing.setConstructor(constructor);

        return standing;
    }

    private Driver saveOrGetDriver(JoplicaDriverResponse.DriverInfo driverInfo){
        return driverRepository.findByDriverId(driverInfo.getDriverId())
                .orElseGet(() -> {
                    Driver newDriver = new Driver();
                    newDriver.setDriverId(driverInfo.getDriverId());
                    newDriver.setCode(driverInfo.getCode());
                    newDriver.setPermanentNumber(driverInfo.getPermanentNumber());
                    newDriver.setGivenName(driverInfo.getGivenName());
                    newDriver.setFamilyName(driverInfo.getFamilyName());
                    newDriver.setDateOfBirth(driverInfo.getDateOfBirth());
                    newDriver.setNationality(driverInfo.getNationality());
                    newDriver.setUrl(driverInfo.getUrl());

                    return driverRepository.save(newDriver);
                });
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
