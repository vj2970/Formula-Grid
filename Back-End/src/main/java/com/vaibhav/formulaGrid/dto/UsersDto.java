package com.vaibhav.formulaGrid.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaibhav.formulaGrid.entity.Users;

public class UsersDto implements UserDetails {

    private Users users;

    public UsersDto(Users users){
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return Collections.singleton(new SimpleGrantedAuthority(users.getRole()));
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

}
