package com.maiphong.recipeapi.dtos.recipe;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeAddIngredientDTO {
    @NotNull(message = "Ingredient ID is required")
    private UUID ingredientId;

    @NotNull(message = "Amount is required")
    private String amount;
}
