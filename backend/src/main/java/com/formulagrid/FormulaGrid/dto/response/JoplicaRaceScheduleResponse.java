package com.formulagrid.FormulaGrid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoplicaRaceScheduleResponse {

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

        @JsonProperty("Races")
        private List<RaceInfo> races;
    }

    @Data@JsonIgnoreProperties(ignoreUnknown = true)
    public static class RaceInfo{
        private String season;
        private String round;
        private String url;
        private String raceName;
        private String date;
        private String time;

        @JsonProperty("Circuit")
        private CircuitInfo circuit;

        @JsonProperty("FirstPractice")
        private SessionInfo firstPractice;

        @JsonProperty("SecondPractice")
        private SessionInfo secondPractice;

        @JsonProperty("ThirdPractice")
        private SessionInfo thirdPractice;

        @JsonProperty("Qualifying")
        private SessionInfo qualifying;

        @JsonProperty("Sprint")
        private SessionInfo sprint;

        @JsonProperty("SprintQualifying")
        private SessionInfo sprintQualifying;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CircuitInfo{
        private String circuitId;
        private String circuitName;
        private String url;

        @JsonProperty("Location")
        private LocationInfo location;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo{
        private String lat;
        private String lng;
        private String locality;
        private String country;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SessionInfo{
        private String date;
        private String time;
    }

}
