package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.recipeapi.dtos.recipe.RecipeAddIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeAddListIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeCreateDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeEditDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeIngredientListWithRecipeIdDTO;

public interface RecipeService {
    List<RecipeDTO> findAll();

    RecipeDTO findById(UUID id);

    RecipeDTO create(RecipeCreateDTO recipeDTO);

    RecipeDTO update(UUID id, RecipeEditDTO recipeDTO);

    boolean delete(UUID id);

    List<RecipeDTO> search(String name);

    Page<RecipeDTO> search(String keyword, Pageable pageable);

    Page<RecipeDTO> search(String keyword, String categoryName, Pageable pageable);

    boolean addIngredient(UUID id, RecipeAddIngredientDTO recipeAddIngredientDTO);

    RecipeIngredientListWithRecipeIdDTO addListIngredient(UUID id,
            RecipeAddListIngredientDTO recipeAddListIngredientDTO);

}
