package com.example.printmatic.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentRequestDTO {

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private String description;
}
