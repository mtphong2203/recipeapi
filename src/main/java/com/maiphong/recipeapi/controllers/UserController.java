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

import com.maiphong.recipeapi.dtos.user.UserCreateDTO;
import com.maiphong.recipeapi.dtos.user.UserDTO;
import com.maiphong.recipeapi.dtos.user.UserEditDTO;
import com.maiphong.recipeapi.dtos.user.UserSearchDTO;
import com.maiphong.recipeapi.dtos.searchdto.SortDirection;
import com.maiphong.recipeapi.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "users", description = "The User API")
public class UserController {

    private final UserService userService;
    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler;

    public UserController(UserService userService,
            PagedResourcesAssembler<UserDTO> pagedResourcesAssembler) {
        this.userService = userService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    @Operation(summary = "Get all users or search users by keyword")
    @ApiResponse(responseCode = "200", description = "Return all users or search users by keyword")
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

        // Search user by keyword and paging
        var users = userService.search(keyword, pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(users);

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping("/search")
    @Operation(summary = "Get all users or search users by keyword")
    @ApiResponse(responseCode = "200", description = "Return all users or search users by keyword")
    public ResponseEntity<?> search(@RequestBody UserSearchDTO userSearchDTO) {
        // Check sort order
        Pageable pageable = null;

        if (userSearchDTO.getOrder().equals(SortDirection.ASC)) {
            pageable = PageRequest.of(userSearchDTO.getPage(), userSearchDTO.getSize(),
                    Sort.by(userSearchDTO.getSortBy()).ascending());
        } else {
            pageable = PageRequest.of(userSearchDTO.getPage(), userSearchDTO.getSize(),
                    Sort.by(userSearchDTO.getSortBy()).descending());
        }

        // Search user by keyword and paging
        var users = userService.search(userSearchDTO.getKeyword(), pageable);

        // Convert to PagedModel - Enhance data with HATEOAS - Easy to navigate with
        // links
        var pagedModel = pagedResourcesAssembler.toModel(users);

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "Return user by id")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        var user = userService.findById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "Create new user")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var newUser = userService.create(userDTO);

        // Check if newUser is null => return 400 Bad Request
        if (newUser == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if newUser is not null => return 201 Created with newUser
        return ResponseEntity.status(201).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by id")
    @ApiResponse(responseCode = "200", description = "Update user by id")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    public ResponseEntity<?> edit(
            @PathVariable UUID id,
            @Valid @RequestBody UserEditDTO userEditDTO,
            BindingResult bindingResult) {
        // Validate userDTO
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        var updatedUserDTO = userService.update(id, userEditDTO);

        // Check if updatedUser is null => return 400 Bad Request
        if (updatedUserDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if updatedUser is not null => return 201 Created with
        // updatedUser
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponse(responseCode = "200", description = "Delete user by id")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        var existedUser = userService.findById(id);
        // Check if user is null => return 404 Not Found
        if (existedUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user is not null => delete user
        var isDeleted = userService.delete(id);

        return ResponseEntity.ok(isDeleted);
    }

}
