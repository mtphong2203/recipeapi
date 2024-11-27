package com.maiphong.recipeapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.maiphong.recipeapi.dtos.auth.LoginDTO;
import com.maiphong.recipeapi.dtos.auth.LoginResponseDTO;
import com.maiphong.recipeapi.dtos.user.UserCreateDTO;
import com.maiphong.recipeapi.services.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(AuthService authService, AuthenticationManagerBuilder authenticationManagerBuilder,
            TokenService tokenService, UserService userService) {
        this.authService = authService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Return access token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUserName(), loginRequest.getPassword());

        // Call the authenticate method of the AuthenticationManagerBuilder instance
        // to authenticate the user
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenService.generateAccessToken(authentication);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @Operation(summary = "Register")
    @ApiResponse(responseCode = "200", description = "Return true if register success")
    @ApiResponse(responseCode = "400", description = "Return false if username exists")
    public ResponseEntity<Boolean> register(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        if (authService.existsByUsername(userCreateDTO.getUsername())) {
            return ResponseEntity.badRequest().body(false);
        }

        var result = userService.create(userCreateDTO);

        return ResponseEntity.ok(result);
    }

}