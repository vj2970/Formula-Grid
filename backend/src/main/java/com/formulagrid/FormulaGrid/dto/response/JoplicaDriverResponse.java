package com.formulagrid.FormulaGrid.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoplicaDriverResponse {

    @JsonProperty("MRData")
    private MRData mrData;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MRData{
        @JsonProperty("DriverTable")
        private DriverTable driverTable;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriverTable{
        @JsonProperty("Drivers")
        private List<DriverInfo> drivers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriverInfo{
        private String driverId;
        private String code;
        private String permanentNumber;
        private String givenName;
        private String familyName;
        private String dateOfBirth;
        private String nationality;
        private String url;
    }

}
