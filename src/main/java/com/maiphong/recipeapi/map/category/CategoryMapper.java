package com.maiphong.recipeapi.map.category;

import org.mapstruct.Mapper;

import com.maiphong.recipeapi.dtos.category.CategoryCreateDTO;
import com.maiphong.recipeapi.dtos.category.CategoryDTO;
import com.maiphong.recipeapi.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDTO categoryDTO);

    Category toCategory(CategoryCreateDTO categoryCreateDTO);

    CategoryDTO toCategoryDTO(Category category);
}
