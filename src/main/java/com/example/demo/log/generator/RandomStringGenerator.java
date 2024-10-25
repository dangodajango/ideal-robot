package com.example.demo.log.generator;

import org.springframework.stereotype.Component;

@Component
public class RandomStringGenerator {

    public static final String ALPHANUMERIC_CHARACTERS = """
            ABCDEFGHIJKLMNOPQRSTUVWXYZ\
            abcdefghijklmnopqrstuvwxyz\
            0123456789""";

    public String generateRandomString() {
        return "";
    }
}
