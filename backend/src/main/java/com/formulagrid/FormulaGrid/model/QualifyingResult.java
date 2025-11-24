package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "qualifying_results")
public class QualifyingResult {

    @Id
    private String id;

    private Integer season;
    private Integer round;
    private String raceName;
    private Circuit circuit;

    private Integer position;
    private Driver driver;
    private Constructor constructor;

    private String q1;
    private String q2;
    private String q3;

}
