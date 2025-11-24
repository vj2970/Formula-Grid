package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.model.RaceResult;
import com.formulagrid.FormulaGrid.service.RaceResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class RaceResultController {

    private final RaceResultService raceResultService;

    @GetMapping("/{season}/{round}")
    public ResponseEntity<List<RaceResult>> getRaceResults(
            @PathVariable Integer season,
            @PathVariable Integer round){
        return ResponseEntity.ok(raceResultService.getRaceResults(season, round));
    }

    @GetMapping("/last")
    public ResponseEntity<List<RaceResult>> getLastRaceResults(){
        return ResponseEntity.ok(raceResultService.getLastRaceResults());
    }

    /**
     * Get all race results for a season
     */
    @GetMapping("/season/{season}")
    public ResponseEntity<List<RaceResult>> getSeasonRaceResults(
            @PathVariable Integer season) {
        return ResponseEntity.ok(raceResultService.getSeasonRaceResults(season));
    }

    /**
     * Get current season race results
     */
    @GetMapping("/current")
    public ResponseEntity<List<RaceResult>> getCurrentSeasonRaceResults() {
        Integer currentSeason = Year.now().getValue();
        return ResponseEntity.ok(raceResultService.getSeasonRaceResults(currentSeason));
    }

    /**
     * Get driver's race history
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RaceResult>> getDriverRaceHistory(
            @PathVariable String driverId) {
        return ResponseEntity.ok(raceResultService.getDriverRaceHistory(driverId));
    }

    /**
     * Get driver's wins
     */
    @GetMapping("/driver/{driverId}/wins")
    public ResponseEntity<List<RaceResult>> getDriverWins(
            @PathVariable String driverId) {
        return ResponseEntity.ok(raceResultService.getDriverWins(driverId));
    }

    /**
     * Get driver's podium finishes
     */
    @GetMapping("/driver/{driverId}/podiums")
    public ResponseEntity<List<RaceResult>> getDriverPodiums(
            @PathVariable String driverId) {
        return ResponseEntity.ok(raceResultService.getDriverPodiums(driverId));
    }

    /**
     * Refresh race results from API
     */
    @PostMapping("/{season}/{round}/refresh")
    public ResponseEntity<List<RaceResult>> refreshRaceResults(
            @PathVariable Integer season,
            @PathVariable Integer round) {
        return ResponseEntity.ok(raceResultService.refreshRaceResults(season, round));
    }


}
