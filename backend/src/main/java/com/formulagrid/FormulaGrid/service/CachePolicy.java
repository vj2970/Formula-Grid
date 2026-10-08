package com.formulagrid.FormulaGrid.service;

import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;
import java.time.Year;

public class CachePolicy {

    private static final  long CURRENT_SEASON_TTL_HOURS = 6;
    private CachePolicy() {}

    static boolean isStale(Integer season, LocalDateTime fetchedAt){
        if(fetchedAt == null) return true;
        if(season < Year.now().getValue()) return fetchedAt.getYear() <= season;
        return fetchedAt.isBefore(LocalDateTime.now().minusHours(CURRENT_SEASON_TTL_HOURS));
    }
}
