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

    public enum Role {ROLE_ADMIN, ROLE_USER};

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


    public String getRole() {
        // TODO Auto-generated method stub
        if(role == Role.ROLE_ADMIN) return "ADMIN";
        else return "USER";
    }

}
