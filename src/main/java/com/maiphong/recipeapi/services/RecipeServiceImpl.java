package com.maiphong.recipeapi.services;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maiphong.recipeapi.dtos.category.CategoryDTO;
import com.maiphong.recipeapi.dtos.ingredient.IngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeAddIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeAddListIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeCreateDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeEditDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeIngredientDTO;
import com.maiphong.recipeapi.dtos.recipe.RecipeIngredientListWithRecipeIdDTO;
import com.maiphong.recipeapi.entities.Recipe;
import com.maiphong.recipeapi.entities.RecipeIngredient;
import com.maiphong.recipeapi.entities.RecipeIngredientId;
import com.maiphong.recipeapi.map.recipe.RecipeMapper;
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
    private final RecipeMapper recipeMapper;

    public RecipeServiceImpl(RecipeRepository recipeRepository, CategoryRepository categoryRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeMapper = recipeMapper;
    }

    @Override
    public List<RecipeDTO> findAll() {
        var recipes = recipeRepository.findAll();

        var recipesDTOs = recipes.stream().map(recipe -> {
            var recipeDTO = recipeMapper.toRecipeDTO(recipe);
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
        var recipeDTO = recipeMapper.toRecipeDTO(recipe);
        // Check if entity recipe has category
        if (recipe.getCategory() != null) {
            // Convert Category to CategoryDTO
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());

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

            // Set recipeIngredientDTOs to recipeEditDTO
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

        var recipe = recipeMapper.toRecipe(recipeCreateDTO);
        // Check if recipeCreateDTO.getCategoryId() is not null
        if (recipeCreateDTO.getCategoryId() != null) {
            // Find category by id
            var category = categoryRepository.findById(recipeCreateDTO.getCategoryId()).orElse(null);

            // Check if category is not null
            if (category != null) {
                // Set category to recipe
                recipe.setCategory(category);
            }
        }

        recipe = recipeRepository.save(recipe);
        final var recipeFinal = recipe;

        recipeCreateDTO.getIngredients().stream().forEach(recipeAddIngredientDTO -> {
            var ingredient = ingredientRepository.findById(recipeAddIngredientDTO.getIngredientId())
                    .orElse(null);

            if (ingredient == null) {
                throw new IllegalArgumentException("Ingredient not found");
            }

            // Check amount not null or empty
            if (recipeAddIngredientDTO.getAmount() == null || recipeAddIngredientDTO.getAmount().isBlank()) {
                throw new IllegalArgumentException("Amount is required");
            }

            var recipeIngredientId = new RecipeIngredientId(recipeFinal.getId(),
                    recipeAddIngredientDTO.getIngredientId());

            // Create RecipeIngredient entity
            var recipeIngredient = new RecipeIngredient();
            recipeIngredient.setId(recipeIngredientId);
            recipeIngredient.setRecipe(recipeFinal);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setAmount(recipeAddIngredientDTO.getAmount());

            var recipeSaved = recipeIngredientRepository.save(recipeIngredient);

            // Convert recipeSave to DTO to show view
            var recipeIngredientDTO = recipeMapper.toRecipeIngredientDTO(recipeSaved);
            recipeIngredientDTO.setAmount(recipeAddIngredientDTO.getAmount());
        });

        var updateRecipeDTO = recipeMapper.toRecipeDTO(recipe);

        if (recipe.getCategory() != null) {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(recipe.getCategory().getId());
            categoryDTO.setName(recipe.getCategory().getName());
            categoryDTO.setDescription(recipe.getCategory().getDescription());
            updateRecipeDTO.setCategoryDTO(categoryDTO);

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

            // Set recipeIngredientDTOs to recipeEditDTO
            updateRecipeDTO.setIngredientDTOs(ingredientDTOs);

        }
        return updateRecipeDTO;
    }

    @Override
    public RecipeDTO update(UUID id, RecipeEditDTO recipeEditDTO) {
        if (recipeEditDTO == null) {
            throw new IllegalArgumentException("RecipeDTO is required");
        }

        // Checl if recipe name is existed
        var existedRecipe = recipeRepository.findByTitle(recipeEditDTO.getTitle());
        if (existedRecipe != null && !existedRecipe.getId().equals(id)) {
            throw new IllegalArgumentException("Recipe name is existed");
        }

        // Find recipe by id - Managed
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }

        // Update recipe
        recipe = recipeMapper.toRecipe(recipeEditDTO);

        if (recipe.getCategory() != null) {
            var category = categoryRepository.findById(recipeEditDTO.getCategoryId()).orElse(null);

            if (category != null) {
                recipe.setCategory(category);
            }
        }

        // Save recipe => update
        recipe = recipeRepository.save(recipe);
        final var recipeFinal = recipe;

        // Update List Ingredient to Recipe
        // Delete all old RecipeIngredient by recipeId
        recipeIngredientRepository.deleteByRecipeId(id);

        // Add List new Ingredient to Recipe
        recipeEditDTO.getIngredients().stream().forEach(recipeAddIngredientDTO -> {
            // Check ingredientId is existed
            var ingredient = ingredientRepository.findById(recipeAddIngredientDTO.getIngredientId()).orElse(null);

            if (ingredient == null) {
                throw new IllegalArgumentException("Ingredient not found");
            }

            // Check amount not null or empty
            if (recipeAddIngredientDTO.getAmount() == null || recipeAddIngredientDTO.getAmount().isBlank()) {
                throw new IllegalArgumentException("Amount is required");
            }

            var recipeIngredientId = new RecipeIngredientId(recipeFinal.getId(), ingredient.getId());

            // Create RecipeIngredient entity
            var recipeIngredient = new RecipeIngredient();
            recipeIngredient.setId(recipeIngredientId);
            recipeIngredient.setRecipe(recipeFinal);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setAmount(recipeAddIngredientDTO.getAmount());

            // Save RecipeIngredient
            var recipeIngredientSaved = recipeIngredientRepository.save(recipeIngredient);

            // Convert recipeIngredientSaved to RecipeIngredientDTO
            var recipeIngredientDTO = recipeMapper.toRecipeIngredientDTO(recipeIngredientSaved);
            recipeIngredientDTO.setAmount(recipeIngredientSaved.getAmount());

        });

        // Convert Recipe to RecipeDTO
        var updatedRecipeDTO = recipeMapper.toRecipeDTO(recipe);

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
            var recipeDTO = recipeMapper.toRecipeDTO(recipe);

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
            var recipeDTO = recipeMapper.toRecipeDTO(recipe);
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
    public Page<RecipeDTO> search(String keyword, String categoryName, Pageable pageable) {
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

            Predicate keywordRecipe = criteriaBuilder.or(titlePredicate, desPredicate);

            if (categoryName == null) {
                return keywordRecipe;
            }
            Predicate categoryPredicate = criteriaBuilder.equal(criteriaBuilder.lower(root.get("category").get("name")),
                    categoryName.toLowerCase());

            // Tra ve keywordPredicate AND categoryPredicate
            return criteriaBuilder.and(keywordRecipe, categoryPredicate);
        };

        Page<Recipe> recipes = recipeRepository.findAll(specification, pageable);

        Page<RecipeDTO> recipeDTOs = recipes.map(recipe -> {
            var recipeDTO = recipeMapper.toRecipeDTO(recipe);
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
    public boolean addIngredient(UUID id, RecipeAddIngredientDTO recipeAddIngredientDTO) {
        // Check if recipe is existed
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }

        var ingredient = ingredientRepository.findById(recipeAddIngredientDTO.getIngredientId()).orElse(null);

        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient not found");
        }

        // Check amount not null or empty
        if (recipeAddIngredientDTO.getAmount() == null || recipeAddIngredientDTO.getAmount().isBlank()) {
            throw new IllegalArgumentException("Amount is required");
        }

        var recipeIngredientId = new RecipeIngredientId(recipe.getId(), recipeAddIngredientDTO.getIngredientId());

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

    @Override
    public RecipeIngredientListWithRecipeIdDTO addListIngredient(UUID id,
            RecipeAddListIngredientDTO recipeAddListIngredientDTO) {
        // Check if recipe is existed
        var recipe = recipeRepository.findById(id).orElse(null);

        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found");
        }

        var result = new RecipeIngredientListWithRecipeIdDTO();
        result.setRecipeId(id);

        var listRecipeIngredient = new ArrayList<RecipeIngredientDTO>();

        recipeAddListIngredientDTO.getListIngredients().stream().forEach(recipeAddIngredientDTO -> {
            var ingredient = ingredientRepository.findById(recipeAddIngredientDTO.getIngredientId())
                    .orElse(null);

            if (ingredient == null) {
                throw new IllegalArgumentException("Ingredient not found");
            }

            // Check amount not null or empty
            if (recipeAddIngredientDTO.getAmount() == null || recipeAddIngredientDTO.getAmount().isBlank()) {
                throw new IllegalArgumentException("Amount is required");
            }

            var recipeIngredientId = new RecipeIngredientId(recipe.getId(),
                    recipeAddIngredientDTO.getIngredientId());

            // Create RecipeIngredient entity
            var recipeIngredient = new RecipeIngredient();
            recipeIngredient.setId(recipeIngredientId);
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setAmount(recipeAddIngredientDTO.getAmount());

            var recipeSaved = recipeIngredientRepository.save(recipeIngredient);

            // Convert recipeSave to DTO to show view
            var recipeIngredientDTO = recipeMapper.toRecipeIngredientDTO(recipeSaved);
            recipeIngredientDTO.setAmount(recipeAddIngredientDTO.getAmount());

            listRecipeIngredient.add(recipeIngredientDTO);

        });
        result.setIngredients(listRecipeIngredient);

        // Save RecipeIngredient
        return result;

    }

}
