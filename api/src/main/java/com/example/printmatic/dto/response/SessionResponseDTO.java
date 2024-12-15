package com.example.printmatic.dto.response;

import com.example.printmatic.enums.PaymentStatus;
import com.example.printmatic.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDTO {
    private String sessionId;
    private String stripePaymentURL;
}
