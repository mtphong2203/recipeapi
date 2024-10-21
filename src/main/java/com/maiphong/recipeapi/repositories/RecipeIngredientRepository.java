package com.maiphong.recipeapi.repositories;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

import com.maiphong.recipeapi.entities.RecipeIngredient;
import com.maiphong.recipeapi.entities.RecipeIngredientId;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {
    void deleteByRecipeId(UUID id);
}
