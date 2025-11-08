package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.Race;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRepository extends MongoRepository<Race, String> {
    List<Race> findBySeasonOrderByRoundAsc(Integer season);
}
