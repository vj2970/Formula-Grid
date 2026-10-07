package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.dto.response.ImportProgressDTO;
import com.formulagrid.FormulaGrid.service.DataImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class DataImportController {

    private final DataImportService dataImportService;

    //Import all data for a specific season
    @PostMapping("/season/{season}")
    public ResponseEntity<Map<String, String>> importSeasonData(@PathVariable Integer season){
        dataImportService.importSeasonData(season);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Season " + season + " data import started");

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/historical")
    public ResponseEntity<Map<String, String>> importHistoricalSeasons() {
        new Thread(dataImportService::importHistoricalSeasons).start();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Historical data import started in background");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/race/{season}/{round}")
    public ResponseEntity<Map<String, String>> importRaceData(@PathVariable Integer season, @PathVariable Integer round){
        dataImportService.importRaceData(season, round);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Race data imported for season " + season + " round " + round);

        return ResponseEntity.ok(response);
    }

    //Get Import progress
    @GetMapping("/progress")
    public ResponseEntity<ImportProgressDTO> getImportProgress(){
        return ResponseEntity.ok(dataImportService.getImportProgress());
    }
}
