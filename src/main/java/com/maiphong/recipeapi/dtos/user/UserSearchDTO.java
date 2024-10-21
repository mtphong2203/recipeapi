package com.maiphong.recipeapi.dtos.user;

import com.maiphong.recipeapi.dtos.searchdto.SearchDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO extends SearchDTO {
    private String keyword;
}
