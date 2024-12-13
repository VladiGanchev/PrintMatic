package com.example.printmatic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.WalletEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.WalletRepository;

@Service
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderRepository orderRepository;

    public WalletEntity addFunds(Long userId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        WalletEntity wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }
        wallet.addFunds(amount);
        walletRepository.save(wallet);
        logger.info("Added {} to wallet of user {}", amount, userId);
        return wallet;
    }

    @Transactional
    public boolean payFromWallet(Long userId, Double amount, OrderEntity order) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        WalletEntity wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("Wallet not found for user ID: " + userId);
        }
    
        if (wallet.withdrawFunds(amount)) {
            order.setStatus(OrderStatus.COMPLETED);  
            order.setPrice(BigDecimal.valueOf(amount));
            orderRepository.save(order);
            logger.info("Payment of {} made from wallet of user {} for order {}", amount, userId, order.getId());
            return true;
        } else {
            logger.warn("Insufficient funds in wallet for user {} to pay {}", userId, amount);
            return false;
        }
    }
}    

