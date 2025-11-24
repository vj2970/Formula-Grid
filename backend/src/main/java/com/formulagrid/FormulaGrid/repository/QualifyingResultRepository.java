package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.QualifyingResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualifyingResultRepository extends MongoRepository<QualifyingResult, String> {

    List<QualifyingResult> findBySeasonAndRoundOrderByPositionAsc(Integer season, Integer round);
    List<QualifyingResult> findBySeasonOrderByRoundAscPositionAsc(Integer season);
    List<QualifyingResult> findByDriver_DriverIdOrderBySeasonDescRoundDesc(String driverId);
    List<QualifyingResult> findByDriver_DriverIdAndPositionOrderBySeasonDescRoundDesc(String driverId, Integer position);

}
