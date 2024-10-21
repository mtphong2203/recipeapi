package com.maiphong.recipeapi.dtos.user;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {
    private UUID id;
    @NotNull(message = "First name is required")
    @NotBlank(message = "First name is not empty")
    @Length(min = 3, max = 255, message = "First name must be between 3 and 255 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @NotBlank(message = "Last name is not empty")
    @Length(min = 3, max = 255, message = "Last name must be between 3 and 255 characters")
    private String lastName;

    @NotNull(message = "User name is required")
    @NotBlank(message = "User name is not empty")
    @Length(min = 3, max = 50, message = "User name must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is not empty")
    @Length(min = 3, max = 50, message = "Email must be between 3 and 50 characters")
    private String email;

    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is not empty")
    @Length(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password; // password before hashing - Admin@1234
}
