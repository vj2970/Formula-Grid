package com.formulagrid.FormulaGrid.controller;

import java.util.List;

import com.formulagrid.FormulaGrid.dto.response.DriverStatisticsDTO;
import com.formulagrid.FormulaGrid.model.DriverStanding;
import com.formulagrid.FormulaGrid.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.repository.DriverRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<Driver>> getCurrentSeasonDrivers(){
        return ResponseEntity.ok(driverService.getCurrentSeasonDrivers());
    }

    @PostMapping("/refresh")
    public ResponseEntity<List<Driver>> refreshDrivers(){
        return ResponseEntity.ok(driverService.fetchAndSaveDriversFromApi());
    }

    @GetMapping("/standings")
    public ResponseEntity<List<DriverStanding>> getCurrentSeasonDriverStandings(){
        return ResponseEntity.ok(driverService.getCurrentSeasonDriverStandings());
    }

    @PostMapping("/standings/refresh")
    public ResponseEntity<List<DriverStanding>> refreshDriverStandings(){
        return ResponseEntity.ok(driverService.fetchAndSaveDriverStandingsFromApi());
    }

    //Get comprehensive driver statistics
    @GetMapping("/{driverId}/statistics")
    public ResponseEntity<DriverStatisticsDTO> getDriverStatistics(@PathVariable String driverId){
        return ResponseEntity.ok(driverService.getDriverStatistics(driverId));
    }

}
