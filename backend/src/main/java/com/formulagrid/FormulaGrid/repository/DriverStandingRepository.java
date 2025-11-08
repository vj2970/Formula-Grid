package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.DriverStanding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverStandingRepository extends MongoRepository<DriverStanding, String> {
    List<DriverStanding> findBySeasonOrderByPositionAsc(Integer season);
}
