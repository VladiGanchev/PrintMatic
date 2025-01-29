package com.example.printmatic.dto.request;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCreationDTO {
    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Value is required")
    private BigDecimal value;

    @NotNull(message = "Percentage flag is required")
    private boolean isPercentage;

    private BigDecimal minimumOrderValue;
    private Integer minimumPageCount;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;
    private RoleEnum applicableUserRole;
    private DeadlineEnum applicableDeadline;

    @NotNull(message = "Stackable flag is required")
    private boolean isStackable;

    @NotNull(message = "Active flag is required")
    private boolean isActive;

    @NotNull(message = "Priority is required")
    private Integer priority;
}

