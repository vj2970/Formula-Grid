package com.formulagrid.FormulaGrid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection =  "constructors")
public class Constructor {

    @Id
    private String id;

    private String constructorId;
    private String name;
    private String nationality;
    private String url;


}
