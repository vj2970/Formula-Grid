package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "constructor_standings")
public class ConstructorStanding {

    @Id
    private String id;

    private Integer season;
    private Integer round;
    private Integer position;
    private String positionText;
    private Integer points;
    private Constructor constructor;

}
