package com.vaibhav.formulaGrid.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vaibhav.formulaGrid.entity.Users;
import com.vaibhav.formulaGrid.entity.Users.Role;
import com.vaibhav.formulaGrid.repo.UsersRepo;

@Service
public class UsersService {

    @Autowired
    private PasswordEncoder passwordEncode;

    @Autowired
    private UsersRepo usersRepo;

    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    public Optional<Users> getUserById(String id) {
        return usersRepo.findById(id);
    }

    public Optional<Users> getUserByUsername(String username){
        return usersRepo.findUserByUsername(username);
    }

    public void addUser(Users users) {
        users.setPassword(passwordEncode.encode(users.getPassword()));
        users.setRole(Role.ROLE_USER);
        usersRepo.save(users);
    }

    public Users updateUser(String id, Users updatedUser) {
        updatedUser.setId(id);
        return usersRepo.save(updatedUser);
    }

    public void deleteUserById(String id) {
        usersRepo.deleteById(id);
    }

    public void deleteUserByUsername(String username) {
        usersRepo.deleteByUsername(username);
    }

}
