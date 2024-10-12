package com.maiphong.recipeapi.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.recipeapi.dtos.category.CategoryCreateDTO;
import com.maiphong.recipeapi.dtos.category.CategoryDTO;

public interface CategoryService {
    List<CategoryDTO> findAll();

    CategoryDTO findById(UUID id);

    CategoryDTO create(CategoryCreateDTO categoryDTO);

    CategoryDTO update(UUID id, CategoryDTO categoryDTO);

    boolean delete(UUID id);

    List<CategoryDTO> search(String name);

    Page<CategoryDTO> search(String keyword, Pageable pageable);
}
