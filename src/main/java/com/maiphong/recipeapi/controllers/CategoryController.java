package com.maiphong.recipeapi.controllers;

import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maiphong.recipeapi.dtos.category.CategoryCreateDTO;
import com.maiphong.recipeapi.dtos.category.CategoryDTO;
import com.maiphong.recipeapi.dtos.category.CategorySearchDTO;
import com.maiphong.recipeapi.dtos.searchdto.SortDirection;
import com.maiphong.recipeapi.services.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "categories", description = "The Category API")
public class CategoryController {

    private final CategoryService categoryService;
    private final PagedResourcesAssembler<CategoryDTO> pagedResourcesAssembler;

    public CategoryController(CategoryService categoryService,
            PagedResourcesAssembler<CategoryDTO> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all categories or search categories by keyword")
    @ApiResponse(responseCode = "200", description = "Return all categories or search categories by keyword")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "name") String sortBy, // Xac dinh truong sap xep
            @RequestParam(required = false, defaultValue = "asc") String order, // Xac dinh chieu sap xep
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "2") Integer size) {
        // Check sort order
        Pageable pageable = null;

        if (order.equals("asc")) {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        }

        // Search category by keyword and paging
        var categories = categoryService.search(keyword, pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(categories);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    // @Operation(summary = "Get all categories or search categories by keyword")
    // @ApiResponse(responseCode = "200", description = "Return all categories or
    // search categories by keyword")
    public ResponseEntity<?> search(@RequestBody CategorySearchDTO categorySearchDTO) {
        // Check sort order
        Pageable pageable = null;

        if (categorySearchDTO.getOrder().equals(SortDirection.ASC)) {
            pageable = PageRequest.of(categorySearchDTO.getPage(), categorySearchDTO.getSize(),
                    Sort.by(categorySearchDTO.getSortBy()).ascending());
        } else {
            pageable = PageRequest.of(categorySearchDTO.getPage(), categorySearchDTO.getSize(),
                    Sort.by(categorySearchDTO.getSortBy()).descending());
        }

        // Search category by keyword and paging
        var categories = categoryService.search(categorySearchDTO.getKeyword(), pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(categories);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    // @Operation(summary = "Get category by id")
    // @ApiResponse(responseCode = "200", description = "Return category by id")
    // @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        var category = categoryService.findById(id);

        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(summary = "Create new category")
    @ApiResponse(responseCode = "201", description = "Create new category")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> create(@Valid @RequestBody CategoryCreateDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var newCategory = categoryService.create(categoryDTO);

        // Check if newCategory is null => return 400 Bad Request
        if (newCategory == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if newCategory is not null => return 201 Created with newCategory
        return ResponseEntity.status(201).body(newCategory);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by id")
    @ApiResponse(responseCode = "200", description = "Update category by id")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> edit(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult bindingResult) {
        // Validate categoryDTO
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var updatedCategoryDTO = categoryService.update(id, categoryDTO);

        // Check if updatedCategory is null => return 400 Bad Request
        if (updatedCategoryDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if updatedCategory is not null => return 201 Created with
        // updatedCategory
        return ResponseEntity.ok(updatedCategoryDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id")
    @ApiResponse(responseCode = "200", description = "Delete category by id")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        // Check if category is not null => delete category
        var isDeleted = categoryService.delete(id);

        return ResponseEntity.ok(isDeleted);
    }

}
