package com.formulagrid.FormulaGrid.repository;

import com.formulagrid.FormulaGrid.model.Constructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConstructorRepository extends MongoRepository<Constructor, String> {
    Optional<Constructor> findByConstructorId(String constructorId);
}
