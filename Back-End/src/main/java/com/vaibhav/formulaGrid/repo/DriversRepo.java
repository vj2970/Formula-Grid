package com.vaibhav.formulaGrid.repo;

import com.vaibhav.formulaGrid.entity.Drivers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriversRepo extends MongoRepository<Drivers, String> {
}
