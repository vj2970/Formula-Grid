package com.formulagrid.FormulaGrid.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatisticsDTO {

    private String driverId;
    private String driverName;
    private String nationality;
    private String currentTeam;

    // Career statistics
    private Integer totalRaces;
    private Integer totalWins;
    private Integer totalPodiums;
    private Integer totalPoles;
    private Integer totalPoints;

    // Season statistics
    private Integer currentSeasonRaces;
    private Integer currentSeasonWins;
    private Integer currentSeasonPodiums;
    private Integer currentSeasonPoles;
    private Integer currentSeasonPoints;
    private Integer currentSeasonPosition;

    // Performance metrics
    private Double winRate;          // Wins / Total Races
    private Double podiumRate;       // Podiums / Total Races
    private Double poleRate;         // Poles / Total Races
    private Double pointsPerRace;    // Total Points / Total Races

    // Recent form (last 5 races)
    private Integer last5Races;
    private Integer last5Wins;
    private Integer last5Podiums;
    private Double last5AvgPosition;

}
