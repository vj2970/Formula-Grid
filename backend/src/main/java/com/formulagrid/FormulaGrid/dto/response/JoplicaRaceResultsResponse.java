package com.formulagrid.FormulaGrid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoplicaRaceResultsResponse {

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

        @JsonProperty("Results")
        private List<ResultInfo> results;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultInfo{
        private String number;
        private String position;
        private String positionText;
        private String points;

        @JsonProperty("Driver")
        private JoplicaDriverResponse.DriverInfo driver;

        @JsonProperty("Constructor")
        private JoplicaConstructorStandingsResponse.ConstructorInfo constructor;

        private String grid;
        private String laps;
        private String status;

        @JsonProperty("Time")
        private TimeInfo time;

        @JsonProperty("FastestLap")
        private FastestLapInfo fastestLap;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeInfo{
        private String millis;
        private String time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FastestLapInfo{
        private String rank;
        private String lap;

        @JsonProperty("Time")
        private FastestLapTimeInfo time;

        @JsonProperty("AverageSpeed")
        private AverageSpeedInfo averageSpeed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FastestLapTimeInfo{
        private String time;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AverageSpeedInfo{
        private String units;
        private String speed;
    }

}
