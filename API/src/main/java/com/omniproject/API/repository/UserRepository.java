package com.omniproject.api.repository;

import com.omniproject.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Apenas escrevendo isso, o Spring já cria o comando SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}