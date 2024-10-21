package com.maiphong.recipeapi.dtos.recipe;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredientDTO {
    private UUID ingredientId;
    private String name;
    private String amount;
}
