package com.maiphong.recipeapi.dtos.ingredient;

import com.maiphong.recipeapi.dtos.searchdto.SearchDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSearchDTO extends SearchDTO {
    private String keyword;
}
