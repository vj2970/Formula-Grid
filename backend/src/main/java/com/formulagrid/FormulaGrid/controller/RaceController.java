package com.formulagrid.FormulaGrid.controller;

import com.formulagrid.FormulaGrid.model.Race;
import com.formulagrid.FormulaGrid.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/refresh")
    public ResponseEntity<List<Race>> refreshRaces(){
        return ResponseEntity.ok(raceService.fetchAndSaveRacesFromApi());
    }

}
