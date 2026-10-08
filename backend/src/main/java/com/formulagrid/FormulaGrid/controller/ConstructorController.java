package com.formulagrid.FormulaGrid.controller;


import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.service.ConstructorService;
import com.formulagrid.FormulaGrid.model.Constructor;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/constructors")
@RequiredArgsConstructor
public class ConstructorController {

    private final ConstructorService constructorService;

    @GetMapping
    public ResponseEntity<List<Constructor>> getCurrentSeasonConstructors() {
        return ResponseEntity.ok(constructorService.getCurrentSeasonConstructors());
    }

    @GetMapping("/{constructorId}")
    public ResponseEntity<Constructor> getConstructorByConstructorId(@PathVariable String constructorId) {
        return ResponseEntity.ok(constructorService.getConstructorByConstructorId(constructorId));
    }

    @GetMapping("/standings")
    public ResponseEntity<List<ConstructorStanding>> getCurrentSeasonStandings(){
        return ResponseEntity.ok(constructorService.getCurrentSeasonStandings());
    }

    @GetMapping("/standings/{season}")
    public ResponseEntity<List<ConstructorStanding>> getConstructorStandings(@PathVariable Integer season) {
        return ResponseEntity.ok(constructorService.getConstructorStandings(season));
    }

    @PostMapping("/standings/{season}/refresh")
    public ResponseEntity<List<ConstructorStanding>> refreshConstructorStandingsForSeason(@PathVariable Integer season) {
        return ResponseEntity.ok(constructorService.refreshConstructorStandingsForSeason(season));
    }

    @PostMapping("/standings/refresh")
    public ResponseEntity<List<ConstructorStanding>> refreshStandings(){
        return ResponseEntity.ok(constructorService.fetchAndSaveStandingsFromApi());
    }

}
