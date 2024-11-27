package com.maiphong.recipeapi.map.user;

import org.mapstruct.Mapper;

import com.maiphong.recipeapi.dtos.user.UserCreateDTO;
import com.maiphong.recipeapi.dtos.user.UserDTO;
import com.maiphong.recipeapi.dtos.user.UserEditDTO;
import com.maiphong.recipeapi.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);

    User toUser(UserCreateDTO userCreateDTO);

    User toUser(UserEditDTO userEditDTO);
}
