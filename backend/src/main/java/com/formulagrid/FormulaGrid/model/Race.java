package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "races")
public class Race {

    @Id
    private String id;

    private Integer season;
    private Integer round;
    private String raceName;
    private Circuit circuit;
    private LocalDate date;
    private LocalTime time;
    private String url;

    private LocalDate firstPracticeDate;
    private LocalTime firstPracticeTime;
    private LocalDate secondPracticeDate;
    private LocalTime secondPracticeTime;
    private LocalDate thirdPracticeDate;
    private LocalTime thirdPracticeTime;
    private LocalDate qualifyingDate;
    private LocalTime qualifyingTime;
    private LocalDate sprintDate;
    private LocalTime sprintTime;
    private LocalDate sprintQualifyingDate;
    private LocalTime sprintQualifyingTime;

}
