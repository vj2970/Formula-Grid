package com.vaibhav.formulaGrid.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class Users {

    private enum Role {ROLE_ADMIN, ROLE_USER};

    @Id
    private String id;
    private Name name;
    private String username;
    private String password;
    private Role role;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Name{
        private String first;
        private String last;
    }

}
