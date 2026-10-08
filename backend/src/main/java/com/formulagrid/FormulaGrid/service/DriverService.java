package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JolpicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.*;
import com.formulagrid.FormulaGrid.exception.ExternalApiException;
import com.formulagrid.FormulaGrid.model.*;
import com.formulagrid.FormulaGrid.repository.*;
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
public class DriverService {

    private final DriverRepository driverRepository;
    private final JolpicaApiClient jolpicaApiClient;
    private final ObjectMapper objectMapper;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorRepository constructorRepository;
    private final RaceResultRepository raceResultRepository;
    private final QualifyingResultRepository qualifyingResultRepository;

    public Driver getDriverByDriverId(String driverId) {
        return driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));
    }

    public List<Driver> getCurrentSeasonDrivers(){
        return getCurrentSeasonDriverStandings().stream()
                .map(DriverStanding::getDriver)
                .collect(Collectors.toList());
    }

    public List<Driver> fetchAndSaveDriversFromApi(){
        try {
            String response = jolpicaApiClient.getCurrentSeasonDrivers().block();
            JolpicaDriverResponse jolpicaResponse = objectMapper.readValue(response, JolpicaDriverResponse.class);

            List<Driver> drivers = jolpicaResponse.getMrData().getDriverTable().getDrivers().stream()
                    .map(this::convertToDriver)
                    .collect(Collectors.toList());

            drivers.forEach(d -> driverRepository.findByDriverId(d.getDriverId())
                    .ifPresent(existing -> d.setId(existing.getId())));
            driverRepository.saveAll(drivers);
            log.info("Saved {} drivers to database", drivers.size());
            return drivers;
        } catch (Exception e) {
            log.error("Error fetching drivers from API", e);
            throw new ExternalApiException("Failed to fetch drivers from Jolpica API", e);
        }
    }

    private Driver convertToDriver(JolpicaDriverResponse.DriverInfo driverInfo){
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
        return loadDriverStandings(currentSeason, jolpicaApiClient.getCurrentSeasonDriverStandings());
    }

    public List<DriverStanding> getDriverStandings(Integer season){
        List<DriverStanding> standings = loadDriverStandings(season, jolpicaApiClient.getDriverStandings(season));
        if(standings.isEmpty()) throw new ResourceNotFoundException("No driver standings available for season " + season);
        return standings;
    }

    public List<DriverStanding> refreshDriverStandingsForSeason(Integer season){
        return fetchAndSaveDriverStandings(jolpicaApiClient.getDriverStandings(season));
    }

    public List<DriverStanding> loadDriverStandings(Integer season, Mono<String> call){
        List<DriverStanding> cached = driverStandingRepository.findBySeasonOrderByPositionAsc(season);
        if(!cached.isEmpty() && !CachePolicy.isStale(season, cached.getFirst().getFetchedAt())) return cached;
        try{
            return fetchAndSaveDriverStandings(call);
        } catch (ExternalApiException e) {
            if(!cached.isEmpty()){
                log.warn("Jolpica unavailable, serving cached {} driver standings", season);
                return cached;
            }
            throw e;
        }
    }

    public List<DriverStanding> fetchAndSaveDriverStandingsFromApi(){
        return fetchAndSaveDriverStandings(jolpicaApiClient.getCurrentSeasonDriverStandings());
    }

    public List<DriverStanding> fetchAndSaveDriverStandings(Mono<String> call){
        try{
            String response = call.block();
            JolpicaDriverStandingsResponse jolpicaResponse = objectMapper.readValue(response, JolpicaDriverStandingsResponse.class);

            var lists = jolpicaResponse.getMrData().getStandingsTable().getStandingsLists();
            if (lists.isEmpty()) {
                assert response != null;
                log.warn("Jolpica returned no driver standings. Response starts with: {}",
                        response.substring(0, Math.min(300, response.length())));
                return List.of();
            }

            var standingsList = lists.getFirst();
            Integer season = Integer.parseInt(standingsList.getSeason());
            Integer round = Integer.parseInt(standingsList.getRound());
            LocalDateTime fetchedAt = LocalDateTime.now();

            List<DriverStanding> standings = standingsList.getDriverStandings().stream()
                    .map(s -> convertToDriverStanding(s, season, round))
                    .toList();
            standings.forEach(s -> s.setFetchedAt(fetchedAt));

            driverStandingRepository.deleteBySeason(season);
            driverStandingRepository.saveAll(standings);
            log.info("Saved {} driver standings for season {} (after round {})",
                    standings.size(), season, round);
            return standings;
        } catch (Exception e) {
            log.error("Error fetching driver standings", e);
            throw new ExternalApiException("Failed to fetch driver standings from Jolpica API", e);
        }
    }

    private DriverStanding convertToDriverStanding(
            JolpicaDriverStandingsResponse.DriverStandingInfo standingInfo,
            Integer season, Integer round
    ){
        Driver driver = saveOrGetDriver(standingInfo.getDriver());
        Constructor constructor = null;
        if(standingInfo.getConstructor() != null && !standingInfo.getConstructor().isEmpty()){
            constructor = saveOrGetConstructor(standingInfo.getConstructor().getFirst());
        }

        DriverStanding standing = new DriverStanding();
        standing.setSeason(season);
        standing.setRound(round);
        standing.setPosition(standingInfo.getPosition() != null
                ? Integer.parseInt(standingInfo.getPosition())
                : null);
        standing.setPositionText(standingInfo.getPositionText());
        standing.setPoints(standingInfo.getPoints() != null
                ? Double.parseDouble(standingInfo.getPoints())
                : 0.0);
        standing.setWins(standingInfo.getWins() != null
                ? Integer.parseInt(standingInfo.getWins())
                : 0);
        standing.setDriver(driver);
        standing.setConstructor(constructor);

        return standing;
    }

    private Driver saveOrGetDriver(JolpicaDriverResponse.DriverInfo driverInfo){
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

    public DriverStatisticsDTO getDriverStatistics(String driverId){
        Driver driver = getDriverByDriverId(driverId);

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
                .toList();

        List<QualifyingResult> currentSeasonQualifying = allQualifying.stream()
                .filter(q -> q.getSeason().equals(currentSeason))
                .toList();

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
                .toList();

        int last5Count = last5Races.size();
        int last5Wins = (int) last5Races.stream()
                .filter(r -> r.getPosition() == 1).count();
        int last5Podiums = (int) last5Races.stream()
                .filter(r -> r.getPosition() <= 3).count();
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
                .currentSeasonPoints(currentStanding != null ? currentStanding.getPoints() : 0.0)
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
        DriverComparisonDTO.HeadToHeadStats h2h = calculateHeadToHead(driver1Races, driver2Races);

        return DriverComparisonDTO.builder()
                .driver1(stats1)
                .driver2(stats2)
                .headToHead(h2h)
                .build();
    }

    //Calculate head-to-head statistics
    private DriverComparisonDTO.HeadToHeadStats calculateHeadToHead(
            List<RaceResult> driver1Races,
            List<RaceResult> driver2Races){

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
