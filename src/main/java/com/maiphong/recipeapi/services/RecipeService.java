package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.recipeapi.dtos.recipe.RecipeAddIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeCreateDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeDTO;

public interface RecipeService {
    List<RecipeDTO> findAll();

    RecipeDTO findById(UUID id);

    RecipeDTO create(RecipeCreateDTO recipeDTO);

    RecipeDTO update(UUID id, RecipeDTO recipeDTO);

    boolean delete(UUID id);

    List<RecipeDTO> search(String name);

    Page<RecipeDTO> search(String keyword, Pageable pageable);

    boolean addIngredient(UUID id, RecipeAddIngredientDTO recipeAddIngredientDTO);

}
