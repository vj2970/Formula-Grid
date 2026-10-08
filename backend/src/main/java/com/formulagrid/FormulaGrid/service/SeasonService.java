package com.formulagrid.FormulaGrid.service;

import com.formulagrid.FormulaGrid.dto.response.SeasonSummaryDTO;
import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.model.DriverStanding;
import com.formulagrid.FormulaGrid.model.QualifyingResult;
import com.formulagrid.FormulaGrid.model.RaceResult;
import com.formulagrid.FormulaGrid.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import com.formulagrid.FormulaGrid.model.Race;
import com.formulagrid.FormulaGrid.exception.ResourceNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonService {

    private final RaceService raceService;
    private final DriverService driverService;
    private final ConstructorService constructorService;
    private final RaceResultRepository raceResultRepository;
    private final QualifyingResultRepository qualifyingResultRepository;
    private final MongoTemplate mongoTemplate;

    //Get Season Summary
    public SeasonSummaryDTO getSeasonSummary(Integer season){
        var races = raceService.getRaces(season);
        if(races.isEmpty()){
            throw new ResourceNotFoundException("No data found for season " + season);
        }
        var driverStandings = driverService.getDriverStandings(season);
        var constructorStandings = constructorService.getConstructorStandings(season);
        var raceResults = raceResultRepository.findBySeasonOrderByRoundAscPositionAsc(season);
        var qualifyingResults = qualifyingResultRepository.findBySeasonOrderByRoundAscPositionAsc(season);

        return calculateSeasonSummary(season, races.size(), raceResults, qualifyingResults, driverStandings, constructorStandings);
    }

    private SeasonSummaryDTO calculateSeasonSummary(
            Integer season,
            Integer totalRaces,
            List<RaceResult> raceResults,
            List<QualifyingResult> qualifyingResults,
            List<DriverStanding> driverStandings,
            List<ConstructorStanding> constructorStandings
    ){

        // Get champions
        String driverChampion = driverStandings.isEmpty() ? null :
                driverStandings.getFirst().getDriver().getGivenName() + " " + driverStandings.getFirst().getDriver().getFamilyName();

        String constructorChampion = constructorStandings.isEmpty() ? null :
                constructorStandings.getFirst().getConstructor().getName();

        // Count unique drivers and constructors
        long totalDrivers = raceResults.stream()
                .map(r -> r.getDriver().getDriverId())
                .distinct()
                .count();


        long totalConstructors = raceResults.stream()
                .map(r -> r.getConstructor().getConstructorId())
                .distinct()
                .count();

        // Count different winners
        long differentWinners = raceResults.stream()
                .filter(r -> r.getPosition() == 1)
                .map(r -> r.getDriver().getDriverId())
                .distinct()
                .count();

        // Count different pole positions
        long differentPoles = qualifyingResults.stream()
                .filter(q -> q.getPosition() == 1)
                .map(q -> q.getDriver().getDriverId())
                .distinct()
                .count();

        // Find driver with most wins
        Map<String, Long> winsByDriver =  raceResults.stream()
                .filter(r -> r.getPosition() == 1)
                .collect(Collectors.groupingBy(
                        r -> r.getDriver().getGivenName() + " " + r.getDriver().getFamilyName(), Collectors.counting()
                ));

        var mostWinsEntry = winsByDriver.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Find driver with most poles
        Map<String, Long> polesByDriver = qualifyingResults.stream()
                .filter(q -> q.getPosition() == 1)
                .collect(Collectors.groupingBy(
                        q -> q.getDriver().getGivenName() + " " + q.getDriver().getFamilyName(), Collectors.counting()
                ));

        var mostPolesEntry = polesByDriver.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        long completedRaces = raceResults.stream()
                .map(r -> r.getRound())
                .distinct()
                .count();

        return SeasonSummaryDTO.builder()
                .season(season)
                .totalRaces(totalRaces)
                .completedRaces((int) completedRaces)
                .driverChampion(driverChampion)
                .constructorChampion(constructorChampion)
                .totalDrivers((int) totalDrivers)
                .totalConstructors((int) totalConstructors)
                .differentWinners((int) differentWinners)
                .differentPolePositions((int) differentPoles)
                .mostWinsDriver(mostWinsEntry != null ? mostWinsEntry.getKey() : null)
                .mostWinsCount(mostWinsEntry != null ? mostWinsEntry.getValue().intValue() : 0)
                .mostPolesDriver(mostPolesEntry != null ? mostPolesEntry.getKey() : null)
                .mostPolesCount(mostPolesEntry != null ? mostPolesEntry.getValue().intValue() : 0)
                .build();
    }

    /**
     * Get list of available seasons
     */
    public List<Integer> getAvailableSeasons() {
        Set<Integer> seasons = new TreeSet<>(Comparator.reverseOrder());
        seasons.addAll(mongoTemplate.findDistinct(new Query(), "season", Race.class, Integer.class));
        seasons.addAll(mongoTemplate.findDistinct(new Query(), "season", RaceResult.class, Integer.class));
        return new ArrayList<>(seasons);
    }
}
