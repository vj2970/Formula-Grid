package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "race_results")
public class RaceResult {

    @Id
    private String id;

    private Integer season;
    private Integer round;
    private String raceName;
    private Circuit circuit;

    private Integer position;
    private String positionText;
    private Integer points;
    private Driver driver;
    private Constructor constructor;

    private Integer grid;
    private Integer laps;
    private String status;
    private String time;
    private Integer milliseconds;
    private String fastestLap;
    private Integer rank;
    private String fastestLapTime;
    private String fastestLapSpeed;

}
