package com.example.printmatic.dto.response;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MultipleServicePriceResultDTO {
    Set<ServicePriceObject> servicePriceSet;
    Integer status;
    String message;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class ServicePriceObject {
        Long id;
        String service;
        String priceType;
        Double price;
    }
}
