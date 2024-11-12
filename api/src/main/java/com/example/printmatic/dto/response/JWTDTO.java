package com.example.printmatic.dto.response;


import java.util.List;

public record JWTDTO(String username, String token, List<String> roles) {}
