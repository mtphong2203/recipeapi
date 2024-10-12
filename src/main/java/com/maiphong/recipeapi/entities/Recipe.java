package com.maiphong.recipeapi.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "preparation_time", nullable = false)
    private double preparationTime;

    @Column(name = "cook_time", nullable = false)
    private double cookTime;

    @Column(name = "serving", nullable = false)
    private int serving;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "recipe")
    private Set<RecipeIngredient> ingredients;

}