package com.example.printmatic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.printmatic.model.PaymentEntity;


@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    
    PaymentEntity findByTransactionId(String transactionId);
}
