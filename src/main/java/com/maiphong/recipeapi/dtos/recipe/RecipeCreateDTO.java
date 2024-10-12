package com.maiphong.recipeapi.dtos.recipe;

import java.util.*;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {

    @NotNull(message = "Can not null")
    private String title;

    @Length(max = 500, message = "Maximum is 500 characters")
    @NotBlank(message = "Can not empty")
    private String description;

    @NotBlank(message = "Can not empty")
    private String image;

    @PositiveOrZero(message = "time must positive")
    private double preparationTime;

    @PositiveOrZero(message = "time must positive")
    private double cookTime;

    @PositiveOrZero(message = "serving must positive")
    private int serving;

    private UUID categoryId;

}