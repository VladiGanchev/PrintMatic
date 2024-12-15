package com.example.printmatic.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderPaymentSuccessDTO {
    Long orderId;
    String stripeSessionId;
}
