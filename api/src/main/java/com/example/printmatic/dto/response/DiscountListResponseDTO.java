package com.example.printmatic.dto.response;

import com.example.printmatic.dto.DiscountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountListResponseDTO {
    private List<DiscountDTO> discounts;
    private Integer status;
    private String message;
}
