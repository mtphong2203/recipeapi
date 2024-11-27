package com.maiphong.recipeapi.map.recipe;

import org.mapstruct.Mapper;

import com.maiphong.recipeapi.dtos.recipe.RecipeCreateDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeEditDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeIngredientDTO;
import com.maiphong.recipeapi.entities.Recipe;
import com.maiphong.recipeapi.entities.RecipeIngredient;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    Recipe toRecipe(RecipeDTO recipeDTO);

    Recipe toRecipe(RecipeCreateDTO recipeCreateDTO);

    Recipe toRecipe(RecipeEditDTO recipeEditDTO);

    RecipeDTO toRecipeDTO(Recipe recipe);

    RecipeIngredient toRecipeIngredient(RecipeIngredientDTO recipeIngredientDTO);

    RecipeIngredientDTO toRecipeIngredientDTO(RecipeIngredient recipeIngredient);

}
