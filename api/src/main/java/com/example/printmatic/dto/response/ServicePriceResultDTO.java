package com.example.printmatic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ServicePriceResultDTO {
    Long id;
    String service;
    String priceType;
    Double price;
    Integer status;
    String message;
}
