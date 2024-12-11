package com.example.printmatic.dto.response;

import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderDTO {
    private Long id;
    private String title;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private BigDecimal price;
    private String documentUrl;
}