package com.maiphong.recipeapi.dtos.role;

import com.maiphong.recipeapi.dtos.searchdto.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleSearchDTO extends SearchDTO {
    private String keyword;
}
