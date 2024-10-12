package com.maiphong.recipeapi.dtos.ingredient;

import java.util.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientCreateBatchDTO {
    // Validate list of ingredients
    @NotEmpty(message = "Ingredients is required")
    private List<IngredientCreateDTO> ingredients;
}
