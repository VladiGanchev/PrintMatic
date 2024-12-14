package com.example.printmatic.service;

import com.example.printmatic.dto.request.PaymentRequestDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

@Service
@Slf4j
public class StripeService {
    private Stripe stripe;
    private final UserRepository userRepository;

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeKey;

    public StripeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init(){
        stripe.apiKey = stripeKey;
    }

    public SessionResponseDTO createSession(PaymentRequestDTO paymentRequestDTO, Principal principal) {
        try {
            UserEntity user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Long amountInCents = paymentRequestDTO.getAmount()
                    .setScale(2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomerEmail(user.getEmail())
                    .setSuccessUrl("http://localhost:8080/swagger-ui/index.html")  // Consider making these URLs configurable
                    .setCancelUrl("http://localhost:8080/swagger-ui/index.html")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(paymentRequestDTO.getCurrency())
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("Reservation Payment")
                                            .setDescription(paymentRequestDTO.getDescription())
                                            .build())
                                    .setUnitAmount(amountInCents)
                                    .build())
                            .setQuantity(1L)
                            .build())
                    .build();
            Session session = Session.create(params);

            SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
            sessionResponseDTO.setSessionId(session.getId());
            sessionResponseDTO.setStripePaymentURL(session.getUrl());
            sessionResponseDTO.setFailureURL(session.getCancelUrl());
            sessionResponseDTO.setSuccessURL(session.getSuccessUrl());

            return sessionResponseDTO;

        } catch (StripeException e) {
            log.error("Error creating Stripe session: ", e);
            throw new RuntimeException("Failed to create payment session", e);
        }
    }
}
