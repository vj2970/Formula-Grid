package com.vaibhav.formulaGrid.service;

import com.vaibhav.formulaGrid.entity.Drivers;
import com.vaibhav.formulaGrid.repo.DriversRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriversService {

    @Autowired
    private DriversRepo driversRepo;

    public List<Drivers> getAllDrivers() {
        return driversRepo.findAll();
    }

    public Optional<Drivers> getDriverById(String id) {
        return driversRepo.findById(id);
    }


    public void addDriver(Drivers driver) {
        driversRepo.save(driver);
    }

    public Drivers updateDriver(String id, Drivers updatedDriver) {
        updatedDriver.setId(id);
        return driversRepo.save(updatedDriver);
    }

    public void deleteDriver(String id) {
        driversRepo.deleteById(id);
    }

}
