package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maiphong.recipeapi.dtos.category.CategoryCreateDTO;
import com.maiphong.recipeapi.dtos.category.CategoryDTO;
import com.maiphong.recipeapi.entities.Category;
import com.maiphong.recipeapi.repositories.CategoryRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> findAll() {
        var categories = categoryRepository.findAll();

        var categoriesDTOs = categories.stream().map(c -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(c.getId());
            categoryDTO.setName(c.getName());
            categoryDTO.setDescription(c.getDescription());
            return categoryDTO;
        }).toList();

        return categoriesDTOs;
    }

    @Override
    public CategoryDTO findById(UUID id) {
        var category = categoryRepository.findById(id).orElse(null);

        if (category == null) {
            return null;
        }

        var categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());

        return categoryDTO;
    }

    @Override
    public CategoryDTO create(CategoryCreateDTO categoryCreateDTO) {
        if (categoryCreateDTO == null) {
            throw new IllegalArgumentException("CategoryDTO is required");
        }

        var exist = categoryRepository.findByName(categoryCreateDTO.getName());
        if (exist != null) {
            throw new IllegalArgumentException("CategoryDTO is exist!");
        }

        var category = new Category();
        category.setName(categoryCreateDTO.getName());
        category.setDescription(categoryCreateDTO.getDescription());

        categoryRepository.save(category);

        var updateCategoryDTO = new CategoryDTO();
        updateCategoryDTO.setId(category.getId());
        updateCategoryDTO.setName(category.getName());
        updateCategoryDTO.setDescription(category.getDescription());

        return updateCategoryDTO;
    }

    @Override
    public CategoryDTO update(UUID id, CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new IllegalArgumentException("CategoryDTO is required");
        }

        // Checl if category name is existed
        var existedCategory = categoryRepository.findByName(categoryDTO.getName());
        if (existedCategory != null && !existedCategory.getId().equals(id)) {
            throw new IllegalArgumentException("Category name is existed");
        }

        // Find category by id - Managed
        var category = categoryRepository.findById(id).orElse(null);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Update category
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Save category => update
        category = categoryRepository.save(category);

        // Convert Category to CategoryDTO
        var updatedCategoryDTO = new CategoryDTO();
        updatedCategoryDTO.setId(category.getId());
        updatedCategoryDTO.setName(category.getName());
        updatedCategoryDTO.setDescription(category.getDescription());

        return updatedCategoryDTO;
    }

    @Override
    public boolean delete(UUID id) {
        var category = categoryRepository.findById(id).orElse(null);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.delete(category);

        return !categoryRepository.existsById(id);
    }

    @Override
    public List<CategoryDTO> search(String keyword) {
        // Find category by keyword
        Specification<Category> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(namePredicate, desPredicate);
        };

        var categories = categoryRepository.findAll(specification);

        // Covert List<Category> to List<CategoryDTO>
        var categoryDTOs = categories.stream().map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        }).toList();

        return categoryDTOs;

    }

    @Override
    public Page<CategoryDTO> search(String keyword, Pageable pageable) {
        Specification<Category> specification = (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return null;
            }

            // WHERE name LIKE %keyword% OR description LIKE %keyword%
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + keyword.toLowerCase() + "%"));
        };

        Page<Category> categories = categoryRepository.findAll(specification, pageable);

        Page<CategoryDTO> categoryDTOs = categories.map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        });

        return categoryDTOs;
    }

}
