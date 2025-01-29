package com.example.printmatic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountUpdateDTO {
    @NotNull(message = "Value is required")
    private BigDecimal value;

    @NotNull(message = "Priority is required")
    private Integer priority;

    @NotNull(message = "Description is required")
    private String description;
}
