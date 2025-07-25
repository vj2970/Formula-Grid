package com.vaibhav.formulaGrid.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaibhav.formulaGrid.entity.Users;
import com.vaibhav.formulaGrid.repo.UsersRepo;
import com.vaibhav.formulaGrid.service.UsersService;

@Controller
// @RestController
// @RequestMapping("/private-api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    // @GetMapping("/getAllUsers")
    // public List<Users> getAllUsers(){
    //     return usersService.getAllUsers();
    // }

    @PostMapping("/addUser")
    public String addUser(@RequestBody Users users){
        usersService.addUser(users);
        return "User Added!";
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome.html";
    }

    @GetMapping("/login")
    public String login(){
        return "login.html";
    }

}
