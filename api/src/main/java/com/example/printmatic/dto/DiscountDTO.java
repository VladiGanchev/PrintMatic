package com.example.printmatic.dto;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal value;
    private boolean isPercentage;
    private BigDecimal minimumOrderValue;
    private Integer minimumPageCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private RoleEnum applicableUserRole;
    private DeadlineEnum applicableDeadline;
    private boolean isStackable;
    private boolean isActive;
    private Integer priority;
}
