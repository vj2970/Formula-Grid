package com.formulagrid.FormulaGrid.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverComparisonDTO {

    private DriverStatisticsDTO driver1;
    private DriverStatisticsDTO driver2;

    //Head-to-head statistics
    private HeadToHeadStats headToHead;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeadToHeadStats{
        private Integer totalRacesMet;
        private Integer driver1Wins;
        private Integer driver2Wins;
        private Integer driver1AheadCount;
        private Integer driver2AheadCount;
        private double driver1AvgPosition;
        private double driver2AvgPosition;
    }

}
