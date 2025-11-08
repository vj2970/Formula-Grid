package com.formulagrid.FormulaGrid.controller;


import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import com.formulagrid.FormulaGrid.service.ConstructorService;
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

    @GetMapping("/standings")
    public ResponseEntity<List<ConstructorStanding>> getCurrentSeasonStandings(){
        return ResponseEntity.ok(constructorService.getCurrentSeasonStandings());
    }

    @PostMapping("/standings/refresh")
    public ResponseEntity<List<ConstructorStanding>> refreshStandings(){
        return ResponseEntity.ok(constructorService.fetchAndSaveStandingsFromApi());
    }

}
