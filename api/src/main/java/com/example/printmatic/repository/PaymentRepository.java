package com.example.printmatic.repository;

import com.example.printmatic.model.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PaymentEntity> findByStripeSessionId(String stripeSessionId);
}
