package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maiphong.recipeapi.dtos.category.CategoryDTO;
import com.maiphong.recipeapi.dtos.ingredient.IngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeAddIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeCreateDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeDTO;
import com.maiphong.recipeapi.entities.Recipe;
import com.maiphong.recipeapi.entities.RecipeIngredient;
import com.maiphong.recipeapi.entities.RecipeIngredientId;
import com.maiphong.recipeapi.repositories.CategoryRepository;
import com.maiphong.recipeapi.repositories.IngredientRepository;
import com.maiphong.recipeapi.repositories.RecipeIngredientRepository;
import com.maiphong.recipeapi.repositories.RecipeRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, CategoryRepository categoryRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    @Override
    public List<RecipeDTO> findAll() {
        var recipes = recipeRepository.findAll();

        var recipesDTOs = recipes.stream().map(recipe -> {
            var recipeDTO = new RecipeDTO();
            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPreparationTime(recipe.getPreparationTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServing(recipe.getServing());

            if (recipe.getCategory() != null) {
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());
                categoryDTO.setDescription(recipe.getCategory().getDescription());

                recipeDTO.setCategoryDTO(categoryDTO);
            }

            return recipeDTO;
        }).toList();

        return recipesDTOs;
    }

    @Override
    public RecipeDTO findById(UUID id) {
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            return null;
        }

        var recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipe.getId());
        recipeDTO.setTitle(recipe.getTitle());
        recipeDTO.setDescription(recipe.getDescription());
        recipeDTO.setImage(recipe.getImage());
        recipeDTO.setPreparationTime(recipe.getPreparationTime());
        recipeDTO.setCookTime(recipe.getCookTime());
        recipeDTO.setServing(recipe.getServing());

        // Check if entity recipe has category
        if (recipe.getCategory() != null) {
            // Convert Category to CategoryDTO
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());

            // Set categoryDTO to recipeDTO
            recipeDTO.setCategoryDTO(categoryDTO);
        }

        // Check if entity recipe has ingredients
        if (recipe.getIngredients() != null) {
            // Convert Set<RecipeIngredient> to List<RecipeIngredientDTO>
            var ingredientDTOs = recipe.getIngredients().stream().map(recipeIngredient -> {
                var ingredientDTO = new IngredientDTO();
                ingredientDTO.setId(recipeIngredient.getIngredient().getId());
                ingredientDTO.setName(recipeIngredient.getIngredient().getName());

                return ingredientDTO;
            }).toList();

            // Set recipeIngredientDTOs to recipeDTO
            recipeDTO.setIngredientDTOs(ingredientDTOs);

        }

        return recipeDTO;
    }

    @Override
    public RecipeDTO create(RecipeCreateDTO recipeCreateDTO) {
        if (recipeCreateDTO == null) {
            throw new IllegalArgumentException("RecipeDTO is required");
        }

        var exist = recipeRepository.findByTitle(recipeCreateDTO.getTitle());
        if (exist != null) {
            throw new IllegalArgumentException("RecipeDTO is exist!");
        }

        var recipe = new Recipe();
        recipe.setTitle(recipeCreateDTO.getTitle());
        recipe.setDescription(recipeCreateDTO.getDescription());
        recipe.setImage(recipeCreateDTO.getImage());
        recipe.setPreparationTime(recipeCreateDTO.getPreparationTime());
        recipe.setCookTime(recipeCreateDTO.getCookTime());
        recipe.setServing(recipeCreateDTO.getServing());

        if (recipeCreateDTO.getCategoryId() != null) {
            var category = categoryRepository.findById(recipeCreateDTO.getCategoryId()).orElse(null);

            if (category != null) {
                recipe.setCategory(category);
            }
        }

        recipeRepository.save(recipe);

        var updateRecipeDTO = new RecipeDTO();
        updateRecipeDTO.setId(recipe.getId());
        updateRecipeDTO.setTitle(recipe.getTitle());
        updateRecipeDTO.setDescription(recipe.getDescription());
        updateRecipeDTO.setImage(recipe.getImage());
        updateRecipeDTO.setPreparationTime(recipe.getPreparationTime());
        updateRecipeDTO.setCookTime(recipe.getCookTime());
        updateRecipeDTO.setServing(recipe.getServing());

        if (recipe.getCategory() != null) {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());
            updateRecipeDTO.setCategoryDTO(categoryDTO);

        }
        return updateRecipeDTO;
    }

    @Override
    public RecipeDTO update(UUID id, RecipeDTO recipeDTO) {
        if (recipeDTO == null) {
            throw new IllegalArgumentException("RecipeDTO is required");
        }

        // Checl if recipe name is existed
        var existedRecipe = recipeRepository.findByTitle(recipeDTO.getTitle());
        if (existedRecipe != null && !existedRecipe.getId().equals(id)) {
            throw new IllegalArgumentException("Recipe name is existed");
        }

        // Find recipe by id - Managed
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }

        // Update recipe
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setImage(recipeDTO.getImage());
        recipe.setPreparationTime(recipeDTO.getPreparationTime());
        recipe.setCookTime(recipeDTO.getCookTime());
        recipe.setServing(recipeDTO.getServing());

        if (recipe.getCategory() != null) {
            var category = categoryRepository.findById(recipeDTO.getCategoryDTO().getId()).orElse(null);

            if (category != null) {
                recipe.setCategory(category);
            }
        }

        // Save recipe => update
        recipe = recipeRepository.save(recipe);

        // Convert Recipe to RecipeDTO
        var updatedRecipeDTO = new RecipeDTO();
        updatedRecipeDTO.setId(recipe.getId());
        updatedRecipeDTO.setTitle(recipe.getTitle());
        updatedRecipeDTO.setDescription(recipe.getDescription());
        updatedRecipeDTO.setImage(recipe.getImage());
        updatedRecipeDTO.setPreparationTime(recipe.getPreparationTime());
        updatedRecipeDTO.setCookTime(recipe.getCookTime());
        updatedRecipeDTO.setServing(recipe.getServing());

        if (recipe.getCategory() != null) {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());
            updatedRecipeDTO.setCategoryDTO(categoryDTO);

        }
        return updatedRecipeDTO;
    }

    @Override
    public boolean delete(UUID id) {
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }
        recipeRepository.delete(recipe);

        return !recipeRepository.existsById(id);
    }

    @Override
    public List<RecipeDTO> search(String keyword) {
        // Find recipe by keyword
        Specification<Recipe> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(titlePredicate, desPredicate);
        };

        var recipes = recipeRepository.findAll(specification);

        // Covert List<Recipe> to List<RecipeDTO>
        var recipeDTOs = recipes.stream().map(recipe -> {
            var recipeDTO = new RecipeDTO();
            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPreparationTime(recipe.getPreparationTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServing(recipe.getServing());

            if (recipe.getCategory() != null) {
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());
                categoryDTO.setDescription(recipe.getCategory().getDescription());
                recipeDTO.setCategoryDTO(categoryDTO);
            }
            return recipeDTO;
        }).toList();

        return recipeDTOs;

    }

    @Override
    public Page<RecipeDTO> search(String keyword, Pageable pageable) {
        Specification<Recipe> specification = (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return null;
            }

            // WHERE name LIKE %keyword% OR description LIKE %keyword%
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + keyword.toLowerCase() + "%"));
        };

        Page<Recipe> recipes = recipeRepository.findAll(specification, pageable);

        Page<RecipeDTO> recipeDTOs = recipes.map(recipe -> {
            var recipeDTO = new RecipeDTO();
            recipeDTO.setId(recipe.getId());
            recipeDTO.setTitle(recipe.getTitle());
            recipeDTO.setDescription(recipe.getDescription());
            recipeDTO.setImage(recipe.getImage());
            recipeDTO.setPreparationTime(recipe.getPreparationTime());
            recipeDTO.setCookTime(recipe.getCookTime());
            recipeDTO.setServing(recipe.getServing());
            if (recipe.getCategory() != null) {
                var categoryDTO = new CategoryDTO();
                categoryDTO.setId(recipe.getCategory().getId());
                categoryDTO.setName(recipe.getCategory().getName());
                categoryDTO.setDescription(recipe.getCategory().getDescription());
                recipeDTO.setCategoryDTO(categoryDTO);
            }
            return recipeDTO;
        });

        return recipeDTOs;
    }

    @Override
    public boolean addIngredient(UUID id, UUID ingredientId, RecipeAddIngredientDTO recipeAddIngredientDTO) {
        // Check if recipe is existed
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }

        // Check ingredientId is existed
        var ingredient = ingredientRepository.findById(ingredientId).orElse(null);

        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient not found");
        }

        // Check amount not null or empty
        if (recipeAddIngredientDTO.getAmount() == null || recipeAddIngredientDTO.getAmount().isBlank()) {
            throw new IllegalArgumentException("Amount is required");
        }

        var recipeIngredientId = new RecipeIngredientId(recipe.getId(), ingredient.getId());

        // Create RecipeIngredient entity
        var recipeIngredient = new RecipeIngredient();
        recipeIngredient.setId(recipeIngredientId);
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(recipeAddIngredientDTO.getAmount());

        // Save RecipeIngredient
        var recipeIngredientSaved = recipeIngredientRepository.save(recipeIngredient);

        return recipeIngredientSaved != null;
    }

}
