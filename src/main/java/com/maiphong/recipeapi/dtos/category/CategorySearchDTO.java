package com.maiphong.recipeapi.dtos.category;

import com.maiphong.recipeapi.dtos.searchdto.SearchDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchDTO extends SearchDTO {
    private String keyword;
}
