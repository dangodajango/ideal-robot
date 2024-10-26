package com.example.demo.log.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HttpVerbs {
    GET("Get"),
    POST("Post"),
    PUT("Put"),
    DELETE("Delete"),
    HEAD("Head");

    private final String verb;
}