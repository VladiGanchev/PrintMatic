package com.example.printmatic.dto.response;

import com.example.printmatic.enums.PaymentType;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaymentDTO {
        private Long id;
        private BigDecimal amount;
        private PaymentType paymentType;
        private LocalDateTime paidAt;
        private String stripeSessionId;

}
