package com.formulagrid.FormulaGrid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoplicaQualifyingResultsResponse {

    @JsonProperty("MRData")
    private MRData mrData;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MRData{
        @JsonProperty("RaceTable")
        private RaceTable raceTable;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RaceTable{
        private String season;
        private String round;

        @JsonProperty("Races")
        private List<RaceInfo> races;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RaceInfo{
        private String season;
        private String round;
        private String raceName;

        @JsonProperty("Circuit")
        private JoplicaRaceScheduleResponse.CircuitInfo circuit;

        @JsonProperty("QualifyingResults")
        private List<QualifyingInfo> qualifyingResults;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QualifyingInfo{
        private String number;
        private String position;

        @JsonProperty("Driver")
        private JoplicaDriverResponse.DriverInfo driver;

        @JsonProperty("Constructor")
        private JoplicaConstructorStandingsResponse.ConstructorInfo constructor;

        @JsonProperty("Q1")
        private String q1;

        @JsonProperty("Q2")
        private String q2;

        @JsonProperty("Q3")
        private String q3;
    }

}
