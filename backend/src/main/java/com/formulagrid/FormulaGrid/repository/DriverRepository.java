package com.formulagrid.FormulaGrid.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.formulagrid.FormulaGrid.model.Driver;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    Optional<Driver> findByDriverId(String driverId);

}
