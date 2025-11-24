package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.model.QualifyingResult;
import com.formulagrid.FormulaGrid.service.QualifyingResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qualifying")
@RequiredArgsConstructor
public class QualifyingResultController {

    private final QualifyingResultsService qualifyingResultService;

    /**
     * Get qualifying results for a specific race
     */
    @GetMapping("/{season}/{round}")
    public ResponseEntity<List<QualifyingResult>> getQualifyingResults(
            @PathVariable Integer season,
            @PathVariable Integer round) {
        return ResponseEntity.ok(qualifyingResultService.getQualifyingResults(season, round));
    }

    /**
     * Get driver's pole positions
     */
    @GetMapping("/driver/{driverId}/poles")
    public ResponseEntity<List<QualifyingResult>> getDriverPolePositions(
            @PathVariable String driverId) {
        return ResponseEntity.ok(qualifyingResultService.getDriverPolePositions(driverId));
    }

    /**
     * Get driver's qualifying history
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<QualifyingResult>> getDriverQualifyingHistory(
            @PathVariable String driverId) {
        return ResponseEntity.ok(qualifyingResultService.getDriverQualifyingHistory(driverId));
    }

    /**
     * Refresh qualifying results from API
     */
    @PostMapping("/{season}/{round}/refresh")
    public ResponseEntity<List<QualifyingResult>> refreshQualifyingResults(
            @PathVariable Integer season,
            @PathVariable Integer round) {
        return ResponseEntity.ok(qualifyingResultService.refreshQualifyingResults(season, round));
    }

}
