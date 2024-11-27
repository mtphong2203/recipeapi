package com.maiphong.recipeapi.services;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.recipeapi.dtos.user.UserCreateDTO;
import com.maiphong.recipeapi.dtos.user.UserDTO;
import com.maiphong.recipeapi.dtos.user.UserEditDTO;

public interface UserService {
    List<UserDTO> findAll();

    List<UserDTO> search(String keyword);

    Page<UserDTO> search(String keyword, Pageable pageable);

    UserDTO findById(UUID id);

    boolean create(UserCreateDTO userCreateDTO);

    boolean update(UUID id, UserEditDTO userEditDTO);

    boolean delete(UUID id);
}
