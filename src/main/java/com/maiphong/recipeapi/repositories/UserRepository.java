package com.maiphong.recipeapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.*;

import com.maiphong.recipeapi.entities.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    User findByUserName(String userName);

    User findByUserNameOrEmail(String userName, String email);

    boolean existsByUserName(String userName);
}
