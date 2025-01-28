package com.example.printmatic.repository;

import com.example.printmatic.model.ServicePriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePriceRepository extends JpaRepository<ServicePriceEntity, Long> {
}
