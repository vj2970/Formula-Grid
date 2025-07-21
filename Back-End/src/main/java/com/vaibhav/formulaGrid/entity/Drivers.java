package com.vaibhav.formulaGrid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "drivers")
public class Drivers {

    @Id
    private String id;
    private Name name;
    private int number;
    private String nationality;
    private LocalDate dateOfBirth;
    private String team;
    private CareerStats careerStats;
    private List<SeasonStats> seasons;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Name {
        private String first;
        private String last;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CareerStats {
        private int races;
        private int wins;
        private int podiums;
        private int championships;
        private double points;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SeasonStats {
        private int year;
        private String team;
        private double points;
        private int wins;
        private int podiums;
    }


}
