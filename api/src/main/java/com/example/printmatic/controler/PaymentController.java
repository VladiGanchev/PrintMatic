package com.example.printmatic.controler;

import com.example.printmatic.dto.request.OrderPaymentSuccessDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.service.PaymentService;
import jakarta.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService stripeService) {
        this.paymentService = stripeService;
    }

    @PostMapping("/createOrderSession/{orderId}")
    public ResponseEntity<SessionResponseDTO> createOrderSession(@PathVariable Long orderId,
                                                                Principal principal) {
        try {
            SessionResponseDTO sessionForPayingOrder = paymentService.createSessionForPayingOrder(orderId, principal);
            return ResponseEntity.ok(sessionForPayingOrder);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/orderSuccess")
    public ResponseEntity<MessageResponseDTO> orderSuccess(@RequestBody OrderPaymentSuccessDTO orderPaymentSuccessDTO, Principal principal) {
        MessageResponseDTO message = paymentService.orderSuccess(orderPaymentSuccessDTO, principal);
        return ResponseEntity.status(message.status()).body(message);
    }

    @GetMapping("/addToBalanceSession")
    public ResponseEntity<SessionResponseDTO> addToBalanceSession(@PathParam("amount") BigDecimal amount,
                                                           Principal principal) {
        try {
            SessionResponseDTO sessionForBalance = paymentService.addToBalanceSession(amount, principal);
            return ResponseEntity.ok(sessionForBalance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/depositBalanceSuccess")
    public ResponseEntity<MessageResponseDTO> depositBalanceSuccess(@PathParam("stripeId") String stripeId,
                                                                    Principal principal) {
        MessageResponseDTO result = paymentService.depositBalanceSuccess(stripeId, principal);
        return ResponseEntity.status(result.status()).body(result);
    }

    @PostMapping("/payOrderFromBalance/{orderId}")
    public ResponseEntity<MessageResponseDTO> payOrderFromBalance(@PathVariable Long orderId,
                                                                  Principal principal) {
        try{
        MessageResponseDTO message = paymentService.payFromBalance(orderId, principal);
        return ResponseEntity.status(message.status()).body(message);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
