package com.maiphong.recipeapi.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String lastName;

    @Column(name = "user_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String userName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<UserRole> roles;

}
