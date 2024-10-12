package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.recipeapi.dtos.ingredient.IngredientCreateBatchDTO;
import com.maiphong.recipeapi.dtos.ingredient.IngredientCreateDTO;
import com.maiphong.recipeapi.dtos.ingredient.IngredientDTO;

public interface IngredientService {
    List<IngredientDTO> findAll();

    IngredientDTO findById(UUID id);

    IngredientDTO create(IngredientCreateDTO categoryDTO);

    List<IngredientDTO> create(IngredientCreateBatchDTO ingredientCreateBatchDTO);

    IngredientDTO update(UUID id, IngredientDTO categoryDTO);

    boolean delete(UUID id);

    List<IngredientDTO> search(String name);

    Page<IngredientDTO> search(String keyword, Pageable pageable);
}
