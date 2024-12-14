package com.example.printmatic.model;

import com.example.printmatic.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private String description;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private String paymentIntentId;

    private String chargeId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToOne(fetch = FetchType.EAGER)
    private OrderEntity order;
}
