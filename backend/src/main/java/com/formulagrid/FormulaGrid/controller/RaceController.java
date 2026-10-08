package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.model.Race;
import com.formulagrid.FormulaGrid.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
public class RaceController {

    private final RaceService raceService;

    @GetMapping
    public ResponseEntity<List<Race>> getCurrentSeasonRaces(){
        return ResponseEntity.ok(raceService.getCurrentSeasonRaces());
    }

    @GetMapping("/{season}")
    public ResponseEntity<List<Race>> getRaces(@PathVariable Integer season) {
        return ResponseEntity.ok(raceService.getRaces(season));
    }

    @PostMapping("/refresh")
    public ResponseEntity<List<Race>> refreshRaces(){
        return ResponseEntity.ok(raceService.fetchAndSaveRacesFromApi());
    }

}
