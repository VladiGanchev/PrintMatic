package com.example.printmatic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
}
