package com.maiphong.recipeapi.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "role")
    private Set<UserRole> users;
}