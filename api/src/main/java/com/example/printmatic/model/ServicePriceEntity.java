package com.example.printmatic.model;

import com.example.printmatic.enums.PriceType;
import com.example.printmatic.enums.ServiceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicePriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceEnum service;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @Column(nullable = false)
    private Double servicePrice;

}
