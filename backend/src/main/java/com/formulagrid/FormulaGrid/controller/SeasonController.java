package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.dto.response.SeasonSummaryDTO;
import com.formulagrid.FormulaGrid.service.SeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@RequiredArgsConstructor
public class SeasonController {

    private final SeasonService seasonService;

    /**
     * Get season summary
     **/
    @GetMapping("/{season}/summary")
    public ResponseEntity<SeasonSummaryDTO> getSeasonSummary(@PathVariable Integer season){
        return ResponseEntity.ok(seasonService.getSeasonSummary(season));
    }

    /**
     * Get list of available seasons
     **/
    @GetMapping("/available")
    public ResponseEntity<List<Integer>> getAvailableSeasons() {
        return ResponseEntity.ok(seasonService.getAvailableSeasons());
    }
}
