package com.example.printmatic.model;

import com.example.printmatic.enums.PaymentStatus;
import com.example.printmatic.enums.PaymentType;
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

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column
    private String stripeSessionId;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne(fetch = FetchType.EAGER)
    private OrderEntity order;
}