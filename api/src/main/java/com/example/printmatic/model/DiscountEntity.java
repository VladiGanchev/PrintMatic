package com.example.printmatic.model;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private boolean isPercentage;

    // Optional for now
    private BigDecimal minimumOrderValue;

    private Integer minimumPageCount;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private RoleEnum applicableUserRole;

    @Column(nullable = false)
    private boolean isStackable;

    @Column(nullable = false)
    private boolean isActive;

    // Priority for stacking order (lower number = higher priority)
    @Column(nullable = false)
    private Integer priority;


    @Enumerated(EnumType.STRING)
    private DeadlineEnum applicableDeadline;
}