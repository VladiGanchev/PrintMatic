package com.example.printmatic.controler;

import com.example.printmatic.dto.request.PaymentRequestDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.service.StripeService;
import com.stripe.model.checkout.Session;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/payment")
public class PaymentController {
    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/createSession")
    public SessionResponseDTO createSession(@RequestBody @Valid PaymentRequestDTO paymentRequestDTO,
                                            Principal principal) {
        return stripeService.createSession(paymentRequestDTO, principal);
    }
}
