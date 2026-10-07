package com.formulagrid.FormulaGrid.service;

import com.formulagrid.FormulaGrid.dto.response.ImportProgressDTO;
import com.formulagrid.FormulaGrid.model.RaceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportService {

    private final RaceResultService raceResultService;
    private final QualifyingResultsService qualifyingResultsService;

    //Import all data for a season
    public void importSeasonData(Integer season){

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

        log.info("Starting data import for season {}", season);

        try {
            log.info("Importing race results for season {}", season);
            raceResultService.getSeasonRaceResults(season);

            log.info("Successfully import data for season {}", season);
        } catch (Exception e) {
            log.error("Error importing season {} data", season, e);
            throw new RuntimeException("Failed to import season data", e);
        }
    }

    /**
     * Import historical seasons (2020-2024)
     */
    public void importHistoricalSeasons() {
        List<Integer> seasons = List.of(2020, 2021, 2022, 2023, 2024);

        log.info("Starting historical data import for seasons: {}", seasons);

        for (Integer season : seasons) {
            try {
                importSeasonData(season);
                log.info("Completed import for season {}", season);

                // Small delay to avoid overwhelming the API
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("Failed to import season {}", season, e);
            }
        }

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
                .status("Ready")
                .message("Data import service ready")
                .build();
    }
}
