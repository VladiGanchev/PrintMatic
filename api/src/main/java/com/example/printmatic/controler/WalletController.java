package com.example.printmatic.controler;

import com.example.printmatic.service.WalletService;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService; 

    @PostMapping("/addFunds")
    public String addFunds(@RequestParam Long userId, @RequestParam Double amount) {
        walletService.addFunds(userId, amount);
        return "Funds added to wallet";
    }

    @PostMapping("/pay/{orderId}")
    public String payFromWallet(@RequestParam Long userId, @PathVariable Long orderId) {

        OrderEntity order = orderService.getOrderById(orderId);
        
        if (order == null) {
            return "Order not found";
        }

        boolean success = walletService.payFromWallet(userId, order.getPrice().doubleValue(), order);

        if (success) {
            return "Payment successful";
        } else {
            return "Insufficient funds in wallet";
        }
    }
}

