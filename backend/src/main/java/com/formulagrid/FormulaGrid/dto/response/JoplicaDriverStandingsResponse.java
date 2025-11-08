package com.formulagrid.FormulaGrid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoplicaDriverStandingsResponse {
    @JsonProperty("MRData")
    private MRData mrData;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MRData{
        @JsonProperty("StandingsTable")
        private StandingsTable standingsTable;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingsTable{
        private String season;

        @JsonProperty("StandingsLists")
        private List<StandingsList> standingsLists;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingsList{
        private String season;
        private String round;

        @JsonProperty("DriverStandings")
        private List<DriverStandingInfo> driverStandings;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriverStandingInfo{
        private String position;
        private String positionText;
        private String points;
        private String wins;

        @JsonProperty("Driver")
        private JoplicaDriverResponse.DriverInfo driver;

        @JsonProperty("Constructors")
        private List<JoplicaConstructorStandingsResponse.ConstructorInfo> constructor;
    }

}
