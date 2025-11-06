package com.formulagrid.FormulaGrid.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver {

    @Id
    private String id;

    private String driverId;
    private String code;
    private String permanentNumber;
    private String givenName;
    private String familyName;
    private String dateOfBirth;
    private String nationality;
    private String url;

}
