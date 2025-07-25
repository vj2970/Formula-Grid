package com.vaibhav.formulaGrid.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vaibhav.formulaGrid.dto.UsersDto;
import com.vaibhav.formulaGrid.entity.Users;
import com.vaibhav.formulaGrid.repo.UsersRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        Optional<Users> opt = usersRepo.findUserByUsername(username);
        if(opt == null) throw new UsernameNotFoundException("User Not Found!");

        return new UsersDto(opt.get());
    }

}
