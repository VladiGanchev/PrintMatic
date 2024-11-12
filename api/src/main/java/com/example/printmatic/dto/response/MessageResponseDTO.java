package com.example.printmatic.dto.response;

import java.io.Serializable;

public record MessageResponseDTO(Integer status, String message) implements Serializable {

}
