package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.RaceResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultRepository extends MongoRepository<RaceResult, String> {

    List<RaceResult> findBySeasonAndRoundOrderByPositionAsc(Integer season, Integer round);
    List<RaceResult> findBySeasonOrderByRoundAscPositionAsc(Integer season);
    List<RaceResult> findByDriver_DriverIdOrderBySeasonDescRoundDesc(String driverId);
    List<RaceResult> findByDriver_DriverIdAndPositionLessThanEqualOrderBySeasonDescRoundDesc(String driverId, Integer position);
    List<RaceResult> findByDriver_DriverIdAndPositionOrderBySeasonDescRoundDesc(String driverId, Integer position);

}
