package com.maiphong.recipeapi.dtos.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientListWithRecipeIdDTO {
    private UUID recipeId;

    private List<RecipeIngredientDTO> ingredients;
}
