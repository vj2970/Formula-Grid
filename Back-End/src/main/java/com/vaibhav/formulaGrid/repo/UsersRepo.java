package com.vaibhav.formulaGrid.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vaibhav.formulaGrid.entity.Users;

@Repository
public interface UsersRepo extends MongoRepository<Users, String> {
    public Optional<Users> findUserByUsername(String username);
    public void deleteByUsername(String username);

}
