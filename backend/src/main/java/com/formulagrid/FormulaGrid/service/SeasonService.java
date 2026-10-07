package com.formulagrid.FormulaGrid.service;

import com.formulagrid.FormulaGrid.dto.response.SeasonSummaryDTO;
import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.model.DriverStanding;
import com.formulagrid.FormulaGrid.model.QualifyingResult;
import com.formulagrid.FormulaGrid.model.RaceResult;
import com.formulagrid.FormulaGrid.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeasonService {

    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;
    private final QualifyingResultRepository qualifyingResultRepository;
    private final DriverStandingRepository driverStandingRepository;
    private final ConstructorStandingRepository constructorStandingRepository;

    //Get Season Summary
    public SeasonSummaryDTO getSeasonSummary(Integer season){
        var races = raceRepository.findBySeasonOrderByRoundAsc(season);
        var raceResults = raceResultRepository.findBySeasonOrderByRoundAscPositionAsc(season);
        var qualifyingResults = qualifyingResultRepository.findBySeasonOrderByRoundAscPositionAsc(season);
        var driverStandings = driverStandingRepository.findBySeasonOrderByPositionAsc(season);
        var constructorStandings = constructorStandingRepository.findBySeasonOrderByPositionAsc(season);

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
                driverStandings.get(0).getDriver().getGivenName() + " " + driverStandings.get(0).getDriver().getFamilyName();

        String constructorChampion = constructorStandings.isEmpty() ? null :
                constructorStandings.get(0).getConstructor().getName();

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
        return raceRepository.findAll().stream()
                .map(race -> race.getSeason())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
