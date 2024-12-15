package com.example.printmatic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResultDTO {
    private Long orderId;
    private Integer status;
    private String message;
}
