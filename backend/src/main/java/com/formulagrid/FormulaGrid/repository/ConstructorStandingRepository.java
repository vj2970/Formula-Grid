package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.ConstructorStanding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstructorStandingRepository extends MongoRepository<ConstructorStanding, String> {
    List<ConstructorStanding> findBySeasonOrderByPositionAsc(Integer season);
}
