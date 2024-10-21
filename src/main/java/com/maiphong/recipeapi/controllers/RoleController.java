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

import com.maiphong.recipeapi.dtos.role.RoleCreateDTO;
import com.maiphong.recipeapi.dtos.role.RoleDTO;
import com.maiphong.recipeapi.dtos.role.RoleSearchDTO;
import com.maiphong.recipeapi.dtos.searchdto.SortDirection;
import com.maiphong.recipeapi.services.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "roles", description = "The Role API")
public class RoleController {

    private final RoleService roleService;
    private final PagedResourcesAssembler<RoleDTO> pagedResourcesAssembler;

    public RoleController(RoleService roleService,
            PagedResourcesAssembler<RoleDTO> pagedResourcesAssembler) {
        this.roleService = roleService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all roles or search roles by keyword")
    @ApiResponse(responseCode = "200", description = "Return all roles or search roles by keyword")
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

        // Search role by keyword and paging
        var roles = roleService.search(keyword, pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(roles);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    @Operation(summary = "Get all roles or search roles by keyword")
    @ApiResponse(responseCode = "200", description = "Return all roles or search roles by keyword")
    public ResponseEntity<?> search(@RequestBody RoleSearchDTO roleSearchDTO) {
        // Check sort order
        Pageable pageable = null;

        if (roleSearchDTO.getOrder().equals(SortDirection.ASC)) {
            pageable = PageRequest.of(roleSearchDTO.getPage(), roleSearchDTO.getSize(),
                    Sort.by(roleSearchDTO.getSortBy()).ascending());
        } else {
            pageable = PageRequest.of(roleSearchDTO.getPage(), roleSearchDTO.getSize(),
                    Sort.by(roleSearchDTO.getSortBy()).descending());
        }

        // Search role by keyword and paging
        var roles = roleService.search(roleSearchDTO.getKeyword(), pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(roles);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by id")
    @ApiResponse(responseCode = "200", description = "Return role by id")
    @ApiResponse(responseCode = "404", description = "Role not found")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        var role = roleService.findById(id);

        if (role == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(role);
    }

    @PostMapping
    @Operation(summary = "Create new role")
    @ApiResponse(responseCode = "201", description = "Create new role")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> create(@Valid @RequestBody RoleCreateDTO roleDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var newRole = roleService.create(roleDTO);

        // Check if newRole is null => return 400 Bad Request
        if (newRole == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if newRole is not null => return 201 Created with newRole
        return ResponseEntity.status(201).body(newRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role by id")
    @ApiResponse(responseCode = "200", description = "Update role by id")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> edit(
            @PathVariable UUID id,
            @Valid @RequestBody RoleDTO roleDTO,
            BindingResult bindingResult) {
        // Validate roleDTO
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var updatedRoleDTO = roleService.update(id, roleDTO);

        // Check if updatedRole is null => return 400 Bad Request
        if (updatedRoleDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if updatedRole is not null => return 201 Created with
        // updatedRole
        return ResponseEntity.ok(updatedRoleDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role by id")
    @ApiResponse(responseCode = "200", description = "Delete role by id")
    @ApiResponse(responseCode = "404", description = "Role not found")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        var existedRole = roleService.findById(id);
        // Check if role is null => return 404 Not Found
        if (existedRole == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if role is not null => delete role
        var isDeleted = roleService.delete(id);

        return ResponseEntity.ok(isDeleted);
    }

}
