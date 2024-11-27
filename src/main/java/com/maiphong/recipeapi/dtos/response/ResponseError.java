package com.maiphong.recipeapi.dtos.response;

import org.springframework.http.HttpStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseError {
    public String message;

    public HttpStatus status;
}
