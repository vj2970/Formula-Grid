package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Circuit {

    private String circuitId;
    private String circuitName;
    private String locality;
    private String country;
    private Double lat;
    private Double lng;
    private String url;
}
