package com.formulagrid.FormulaGrid.service;

import com.formulagrid.FormulaGrid.dto.response.ImportProgressDTO;
import com.formulagrid.FormulaGrid.model.RaceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.stream.IntStream;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportService {

    private final RaceResultService raceResultService;
    private final QualifyingResultsService qualifyingResultsService;
    private final RaceService raceService;
    private final DriverService driverService;
    private final ConstructorService constructorService;

    private volatile String importStatus = "READY";
    private volatile String importMessage = "Data import service ready";
    private volatile Integer currentSeason;
    private volatile Integer totalSeasons;
    private volatile Integer completedSeasons;

    //Import all data for a season
    public void importSeasonData(Integer season){
        log.info("Starting data import for season {}", season);

        raceService.getRaces(season);                       // calendar
        driverService.getDriverStandings(season);           // final standings
        constructorService.getConstructorStandings(season);

        for (int round = 1; round <= 30; round++){
            List<RaceResult> results = raceResultService.getRaceResults(season, round);
            if(results.isEmpty()) break;
            qualifyingResultsService.getQualifyingResults(season, round);
            try{
                Thread.sleep(500);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }

        log.info("Successfully import data for season {}", season);
    }

    /**
     * Import historical seasons (2020-2024)
     */
    @Async
    public void importHistoricalSeasons() {
//        List<Integer> seasons = List.of(2020, 2021, 2022, 2023, 2024);
        List<Integer> seasons = IntStream.rangeClosed(2000, 2025)
                .boxed()
                .toList();

        totalSeasons = seasons.size();
        completedSeasons = 0;
        importStatus = "RUNNING";

        log.info("Starting historical data import for {} seasons", seasons.size());

        for (Integer season : seasons) {
            try {
                importSeasonData(season);
                importMessage = "Importing season " + season;

                log.info("Starting import for season {}", season);

                importSeasonData(season);

                completedSeasons++;

                importMessage = "Completed season " + season;

                log.info("Completed import for season {}", season);

                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("Failed to import season {}", season, e);

                importStatus = "FAILED";
                importMessage = "Failed to import season " + season;

                return;
            }
        }

        importStatus = "COMPLETED";
        importMessage = "Historical data import completed";
        currentSeason = null;

        log.info("Historical data import completed");
    }

    /**
     * Import specific race data (results + qualifying)
     */
    public void importRaceData(Integer season, Integer round) {
        log.info("Importing data for season {} round {}", season, round);

        try {
            // Import race results
            raceResultService.getRaceResults(season, round);

            // Import qualifying results
            qualifyingResultsService.getQualifyingResults(season, round);

            log.info("Successfully imported data for season {} round {}", season, round);
        } catch (Exception e) {
            log.error("Error importing race data", e);
            throw new RuntimeException("Failed to import race data", e);
        }
    }

    /**
     * Get import progress
     */
    public ImportProgressDTO getImportProgress() {
        // This would track progress in real implementation
        // For now, return basic info
        return ImportProgressDTO.builder()
                .status(importStatus)
                .message(importMessage)
                .currentSeason(currentSeason)
                .totalSeasons(totalSeasons)
                .completedSeasons(completedSeasons)
                .build();
    }
}
