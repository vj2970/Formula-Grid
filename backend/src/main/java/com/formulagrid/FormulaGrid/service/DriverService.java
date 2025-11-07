package com.formulagrid.FormulaGrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formulagrid.FormulaGrid.client.JoplicaApiClient;
import com.formulagrid.FormulaGrid.dto.response.JoplicaDriverResponse;
import com.formulagrid.FormulaGrid.model.Driver;
import com.formulagrid.FormulaGrid.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final JoplicaApiClient joplicaApiClient;
    private final ObjectMapper objectMapper;

    public List<Driver> getCurrentSeasonDrivers(){

        //Check for drivers in DB first
        List<Driver> driversFromDb = driverRepository.findAll();
        if(!driversFromDb.isEmpty()){
            log.info("Returning {} drivers from database", driversFromDb.size());
            return driversFromDb;
        }

        //Fetch drivers from API if not in DB
        log.info("Fetching drivers from Ergast API");
        return fetchAndSaveDriversFromApi();
    }

    public List<Driver> fetchAndSaveDriversFromApi(){
        try {
            String response = joplicaApiClient.getCurrentSeasonDrivers().block();
            JoplicaDriverResponse joplicaResponse = objectMapper.readValue(response, JoplicaDriverResponse.class);

            List<Driver> drivers = joplicaResponse.getMrData().getDriverTable().getDrivers().stream()
                    .map(this::convertToDriver)
                    .collect(Collectors.toList());

            driverRepository.saveAll(drivers);
            log.info("Saved {} drivers to database", drivers.size());
            return drivers;
        } catch (Exception e) {
            log.error("Error fetching drivers from API", e);
            throw new RuntimeException("Failed to fetch drivers", e);
        }
    }

    private Driver convertToDriver(JoplicaDriverResponse.DriverInfo driverInfo){
        Driver driver = new Driver();
        driver.setDriverId(driverInfo.getDriverId());
        driver.setCode(driverInfo.getCode());
        driver.setPermanentNumber(driverInfo.getPermanentNumber());
        driver.setGivenName(driverInfo.getGivenName());
        driver.setFamilyName(driverInfo.getFamilyName());
        driver.setDateOfBirth(driverInfo.getDateOfBirth());
        driver.setNationality(driverInfo.getNationality());
        driver.setUrl(driverInfo.getUrl());
        return driver;
    }

}
