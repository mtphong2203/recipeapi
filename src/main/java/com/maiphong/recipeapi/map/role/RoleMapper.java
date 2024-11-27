package com.maiphong.recipeapi.map.role;

import org.mapstruct.Mapper;

import com.maiphong.recipeapi.dtos.role.RoleCreateDTO;
import com.maiphong.recipeapi.dtos.role.RoleDTO;
import com.maiphong.recipeapi.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleDTO roleDTO);

    Role toRole(RoleCreateDTO roleCreateDTO);

    RoleDTO toRoleDTO(Role role);
}
