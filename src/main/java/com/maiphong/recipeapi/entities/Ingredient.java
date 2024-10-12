package com.maiphong.recipeapi.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "ingredient")
    private Set<RecipeIngredient> recipes;

}