package com.vaibhav.formulaGrid.controller;

import com.vaibhav.formulaGrid.entity.Drivers;
import com.vaibhav.formulaGrid.service.DriversService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/private-api/drivers")
public class DriversController {

    @Autowired
    private DriversService driversService;

    @GetMapping("/findAllDrivers")
    public List<Drivers> findAll() {
        return driversService.getAllDrivers();
    }

    @PostMapping("/addDrivers")
    public String addDrivers(@RequestBody Drivers drivers) {
        driversService.addDriver(drivers);
        return "Added Successfully";
    }

}
