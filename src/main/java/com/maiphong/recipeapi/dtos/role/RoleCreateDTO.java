package com.maiphong.recipeapi.dtos.role;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is not empty")
    @Length(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    @Length(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
