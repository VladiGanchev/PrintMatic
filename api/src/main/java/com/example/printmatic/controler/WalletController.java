package com.example.printmatic.controler;

import com.example.printmatic.service.WalletService;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/addFunds")
    public String addFunds(@RequestParam BigDecimal amount, Principal principal) {
        walletService.addFunds(principal, amount);
        return "Funds added to wallet";
    }

    @PostMapping("/pay/{orderId}")
    public String payFromWallet(@PathVariable Long orderId, Principal principal) {

        boolean success = walletService.payFromWallet(orderId, principal);

        if (success) {
            return "Payment successful";
        } else {
            return "Insufficient funds in wallet";
        }
    }
}

