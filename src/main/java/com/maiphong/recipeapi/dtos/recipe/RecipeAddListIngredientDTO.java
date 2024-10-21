package com.maiphong.recipeapi.dtos.recipe;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeAddListIngredientDTO {
    @NotNull(message = "Can not null")
    private List<RecipeAddIngredientDTO> listIngredients;
}
