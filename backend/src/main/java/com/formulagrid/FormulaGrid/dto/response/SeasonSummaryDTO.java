package com.formulagrid.FormulaGrid.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeasonSummaryDTO {

    private Integer season;
    private Integer totalRaces;
    private Integer completedRaces;

    // Champion info
    private String driverChampion;
    private String constructorChampion;

    // Season statistics
    private Integer totalDrivers;
    private Integer totalConstructors;
    private Integer differentWinners;
    private Integer differentPolePositions;

    // Most successful
    private String mostWinsDriver;
    private Integer mostWinsCount;
    private String mostPolesDriver;
    private Integer mostPolesCount;
}
