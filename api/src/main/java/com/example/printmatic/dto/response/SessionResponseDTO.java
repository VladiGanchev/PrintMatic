package com.example.printmatic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponseDTO {
    private String sessionId;
    private String stripePaymentURL;
    private String successURL;
    private String failureURL;
}
