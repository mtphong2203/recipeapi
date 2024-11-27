package com.maiphong.recipeapi.map.ingredient;

import org.mapstruct.Mapper;

import com.maiphong.recipeapi.dtos.ingredient.IngredientCreateDTO;
import com.maiphong.recipeapi.dtos.ingredient.IngredientDTO;
import com.maiphong.recipeapi.entities.Ingredient;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    Ingredient toIngredient(IngredientDTO ingredientDTO);

    Ingredient toIngredient(IngredientCreateDTO ingredientCreateDTO);

    IngredientDTO toIngredientDTO(Ingredient ingredient);
}
