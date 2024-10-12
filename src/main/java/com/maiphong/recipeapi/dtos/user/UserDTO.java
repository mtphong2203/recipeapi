package com.maiphong.recipeapi.dtos.user;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;

    private String firstName;

    private String lastName;

    private String userName;

    private String email;

    private String password;
}