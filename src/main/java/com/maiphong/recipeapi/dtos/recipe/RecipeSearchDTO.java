package com.maiphong.recipeapi.dtos.recipe;

import com.maiphong.recipeapi.dtos.searchdto.SearchDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchDTO extends SearchDTO {
    private String keyword;
    private String categoryName;
}
