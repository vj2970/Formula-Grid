package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.*;
import com.formulagrid.FormulaGrid.exception.ExternalApiException;
import com.formulagrid.FormulaGrid.model.*;
import com.formulagrid.FormulaGrid.repository.*;
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
    private final RaceResultRepository raceResultRepository;
    private final QualifyingResultRepository qualifyingResultRepository;

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

    public DriverStatisticsDTO getDriverStatistics(String driverId){
        Driver driver = driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: "+driverId));

        //Get all race results
        List<RaceResult> allRaces = raceResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId);

        //Get Qualifying results
        List<QualifyingResult> allQualifying = qualifyingResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId);

        //Get Current Season standings
        Integer currentSeason = Year.now().getValue();
        List<DriverStanding> currentStandings = driverStandingRepository
                .findBySeasonOrderByPositionAsc(currentSeason);

        DriverStanding currentStanding = currentStandings.stream()
                .filter(s -> s.getDriver().getDriverId().equals(driverId))
                .findFirst()
                .orElse(null);

        return calculateStatistics(driver, allRaces, allQualifying, currentStanding, currentSeason);
    }

    //Calculate all statistics
    private DriverStatisticsDTO calculateStatistics(
            Driver driver,
            List<RaceResult> allRaces,
            List<QualifyingResult> allQualifying,
            DriverStanding currentStanding,
            Integer currentSeason){

        //Career Stats
        int totalRaces = allRaces.size();
        int totalWins = (int) allRaces.stream().filter(r -> r.getPosition() == 1).count();
        int totalPodiums = (int) allRaces.stream().filter(r -> r.getPosition() <= 3).count();
        int totalPoles = (int) allQualifying.stream().filter(q -> q.getPosition() == 1).count();
        int totalPoints = allRaces.stream()
                .mapToInt(r -> r.getPoints() != null ? r.getPoints() : 0)
                .sum();

        //Current season stats
        List<RaceResult> currentSeasonRaces = allRaces.stream()
                .filter(r -> r.getSeason().equals(currentSeason))
                .collect(Collectors.toList());

        List<QualifyingResult> currentSeasonQualifying = allQualifying.stream()
                .filter(q -> q.getSeason().equals(currentSeason))
                .collect(Collectors.toList());

        int currentSeasonRaceCount = currentSeasonRaces.size();
        int currentSeasonWins = (int) currentSeasonRaces.stream()
                .filter(r -> r.getPosition() == 1).count();
        int currentSeasonPodiums = (int) currentSeasonRaces.stream()
                .filter(r -> r.getPosition() <= 3).count();
        int currentSeasonPoles = (int) currentSeasonQualifying.stream()
                .filter(q -> q.getPosition() == 1).count();

        //Last 5 races
        List<RaceResult> last5Races = allRaces.stream()
                .limit(5)
                .collect(Collectors.toList());

        int last5Count = last5Races.size();
        int last5Wins = (int) last5Races.stream()
                .filter(r -> r.getPosition() == 1).count();
        int last5Podiums = (int) last5Races.stream()
                .filter(r -> r.getPosition() >= 3).count();
        double last5AvgPosition = last5Races.stream()
                .mapToInt(RaceResult::getPosition)
                .average()
                .orElse(0.0);

        //Performance Rates
        double winRate = totalRaces > 0 ? (double) totalWins / totalRaces * 100 : 0.0;
        double podiumRate = totalRaces > 0 ? (double) totalPodiums / totalRaces * 100 : 0.0;
        double poleRate = !allQualifying.isEmpty() ? (double) totalPoles / allQualifying.size() * 100 : 0.0;
        double pointsPerRace = totalRaces > 0 ? (double) totalPoints / totalRaces : 0.0;

        //Get current team
        String currentTeam = currentStanding != null && currentStanding.getConstructor() != null
                ? currentStanding.getConstructor().getName()
                : "Unknown";

        return DriverStatisticsDTO.builder()
                .driverId(driver.getDriverId())
                .driverName(driver.getGivenName() + " " + driver.getFamilyName())
                .nationality(driver.getNationality())
                .currentTeam(currentTeam)
                .totalRaces(totalRaces)
                .totalWins(totalWins)
                .totalPodiums(totalPodiums)
                .totalPoles(totalPoles)
                .totalPoints(totalPoints)
                .currentSeasonRaces(currentSeasonRaceCount)
                .currentSeasonWins(currentSeasonWins)
                .currentSeasonPodiums(currentSeasonPodiums)
                .currentSeasonPoles(currentSeasonPoles)
                .currentSeasonPoints(currentStanding != null ? currentStanding.getPoints() : 0)
                .currentSeasonPosition(currentStanding != null ? currentStanding.getPosition() : null)
                .winRate(Math.round(winRate * 100.0) / 100.0)
                .podiumRate(Math.round(podiumRate * 100.0) / 100.0)
                .poleRate(Math.round(poleRate * 100.0) / 100.0)
                .pointsPerRace(Math.round(pointsPerRace * 100.0) / 100.0)
                .last5Races(last5Count)
                .last5Wins(last5Wins)
                .last5Podiums(last5Podiums)
                .last5AvgPosition(Math.round(last5AvgPosition * 100.0) / 100.0)
                .build();
    }

    public DriverComparisonDTO compareDrivers(String driverId1, String driverId2){
        //Get Statistics for both drivers
        DriverStatisticsDTO stats1 = getDriverStatistics(driverId1);
        DriverStatisticsDTO stats2 = getDriverStatistics(driverId2);

        //Get Races where both competed
        List<RaceResult> driver1Races = raceResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId1);
        List<RaceResult> driver2Races = raceResultRepository
                .findByDriver_DriverIdOrderBySeasonDescRoundDesc(driverId2);

        //Find Common races
        DriverComparisonDTO.HeadToHeadStats h2h = calculateHeadToHead(driver1Races, driver2Races, driverId1, driverId2);

        return DriverComparisonDTO.builder()
                .driver1(stats1)
                .driver2(stats2)
                .headToHead(h2h)
                .build();
    }

    //Calculate head-to-head statistics
    private DriverComparisonDTO.HeadToHeadStats calculateHeadToHead(
            List<RaceResult> driver1Races,
            List<RaceResult> driver2Races,
            String driverId1,
            String driverId2){

        int totalMet = 0;
        int driver1Wins = 0;
        int driver2Wins = 0;
        int driver1Ahead = 0;
        int driver2Ahead = 0;
        int totalPositions1 = 0;
        int totalPositions2 = 0;

        //Find races where both competed
        for (RaceResult race1 : driver1Races){
            RaceResult race2 = driver2Races.stream()
                    .filter(r -> r.getSeason().equals(race1.getSeason())
                            && r.getRound().equals(race1.getRound()))
                    .findFirst()
                    .orElse(null);

            if (race2 != null){
                totalMet++;

                if (race1.getPosition() == 1) driver1Wins++;
                if (race2.getPosition() == 1) driver2Wins++;

                if (race1.getPosition() < race2.getPosition()) driver1Ahead++;
                if (race2.getPosition() < race1.getPosition()) driver2Ahead++;

                totalPositions1 += race1.getPosition();
                totalPositions2 += race2.getPosition();
            }
        }

        double avgPosition1 = totalMet > 0 ? (double) totalPositions1 / totalMet: 0.0;
        double avgPosition2 = totalMet > 0 ? (double) totalPositions2 / totalMet: 0.0;

        return DriverComparisonDTO.HeadToHeadStats.builder()
                .totalRacesMet(totalMet)
                .driver1Wins(driver1Wins)
                .driver2Wins(driver2Wins)
                .driver1AheadCount(driver1Ahead)
                .driver2AheadCount(driver2Ahead)
                .driver1AvgPosition(Math.round(avgPosition1 * 100.0) / 100.0)
                .driver2AvgPosition(Math.round(avgPosition2 * 100.0) / 100.0)
                .build();
    }

}
