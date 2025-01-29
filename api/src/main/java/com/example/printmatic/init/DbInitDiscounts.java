package com.example.printmatic.init;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.DiscountEntity;
import com.example.printmatic.repository.DiscountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DbInitDiscounts {

    private final DiscountRepository discountRepository;

    public DbInitDiscounts(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public void seedDiscounts() {
        if (discountRepository.count() == 0) {
            // Bulk order discount
            DiscountEntity bulkDiscount = new DiscountEntity();
            bulkDiscount.setName("Bulk Order Discount");
            bulkDiscount.setDescription("10% off for orders with more than 100 pages");
            bulkDiscount.setValue(BigDecimal.valueOf(10)); // 10%
            bulkDiscount.setPercentage(true);
            bulkDiscount.setMinimumPageCount(100);
            bulkDiscount.setStartDate(LocalDateTime.now());
            bulkDiscount.setStackable(true);
            bulkDiscount.setActive(true);
            bulkDiscount.setPriority(1);

            // Employee discount
            DiscountEntity employeeDiscount = new DiscountEntity();
            employeeDiscount.setName("Employee Discount");
            employeeDiscount.setDescription("15% off for employees");
            employeeDiscount.setValue(BigDecimal.valueOf(15)); // 15%
            employeeDiscount.setPercentage(true);
            employeeDiscount.setMinimumOrderValue(BigDecimal.ZERO);
            employeeDiscount.setStartDate(LocalDateTime.now());
            employeeDiscount.setStackable(false);
            employeeDiscount.setActive(true);
            employeeDiscount.setPriority(0);
            employeeDiscount.setApplicableUserRole(RoleEnum.EMPLOYEE);

            // Large order discount
            DiscountEntity largeOrderDiscount = new DiscountEntity();
            largeOrderDiscount.setName("Large Order Discount");
            largeOrderDiscount.setDescription("5 BGN off for orders over 50 BGN");
            largeOrderDiscount.setValue(BigDecimal.valueOf(5)); // 5 BGN
            largeOrderDiscount.setPercentage(false);
            largeOrderDiscount.setMinimumOrderValue(BigDecimal.valueOf(50));
            largeOrderDiscount.setStartDate(LocalDateTime.now());
            largeOrderDiscount.setStackable(true);
            largeOrderDiscount.setActive(true);
            largeOrderDiscount.setPriority(2);

            // Large grayscale volume discount
            DiscountEntity largeGrayscaleDiscount = new DiscountEntity();
            largeGrayscaleDiscount.setName("Large Grayscale Volume Discount");
            largeGrayscaleDiscount.setDescription("30% off for orders with more than 100 grayscale pages");
            largeGrayscaleDiscount.setValue(BigDecimal.valueOf(30)); // 30%
            largeGrayscaleDiscount.setPercentage(true);
            largeGrayscaleDiscount.setMinimumPageCount(100);
            largeGrayscaleDiscount.setStartDate(LocalDateTime.now());
            largeGrayscaleDiscount.setStackable(false);
            largeGrayscaleDiscount.setActive(true);
            largeGrayscaleDiscount.setPriority(1);

            // Medium grayscale volume discount
            DiscountEntity mediumGrayscaleDiscount = new DiscountEntity();
            mediumGrayscaleDiscount.setName("Medium Grayscale Volume Discount");
            mediumGrayscaleDiscount.setDescription("10% off for orders between 21-100 grayscale pages");
            mediumGrayscaleDiscount.setValue(BigDecimal.valueOf(10)); // 10%
            mediumGrayscaleDiscount.setPercentage(true);
            mediumGrayscaleDiscount.setMinimumPageCount(21);
            mediumGrayscaleDiscount.setStartDate(LocalDateTime.now());
            mediumGrayscaleDiscount.setStackable(false);
            mediumGrayscaleDiscount.setActive(true);
            mediumGrayscaleDiscount.setPriority(2);

            // Rush order premium (implemented as negative discount)
            DiscountEntity rushOrderPremium = new DiscountEntity();
            rushOrderPremium.setName("Rush Order Premium");
            rushOrderPremium.setDescription("20% premium for 1-hour rush orders");
            rushOrderPremium.setValue(BigDecimal.valueOf(-20)); // -20% is a premium
            rushOrderPremium.setPercentage(true);
            rushOrderPremium.setStartDate(LocalDateTime.now());
            rushOrderPremium.setStackable(true);
            rushOrderPremium.setActive(true);
            rushOrderPremium.setPriority(0);
            rushOrderPremium.setApplicableDeadline(DeadlineEnum.ONE_HOUR);

            // Same day premium
            DiscountEntity sameDayPremium = new DiscountEntity();
            sameDayPremium.setName("Same Day Premium");
            sameDayPremium.setDescription("10% premium for same-day orders");
            sameDayPremium.setValue(BigDecimal.valueOf(-10)); // -10% is a premium
            sameDayPremium.setPercentage(true);
            sameDayPremium.setStartDate(LocalDateTime.now());
            sameDayPremium.setStackable(true);
            sameDayPremium.setActive(true);
            sameDayPremium.setPriority(1);
            sameDayPremium.setApplicableDeadline(DeadlineEnum.ONE_DAY);

            // Weekly planning discount
            DiscountEntity weeklyPlanningDiscount = new DiscountEntity();
            weeklyPlanningDiscount.setName("Weekly Planning Discount");
            weeklyPlanningDiscount.setDescription("10% off for one-week delivery time");
            weeklyPlanningDiscount.setValue(BigDecimal.valueOf(10));
            weeklyPlanningDiscount.setPercentage(true);
            weeklyPlanningDiscount.setStartDate(LocalDateTime.now());
            weeklyPlanningDiscount.setStackable(true);
            weeklyPlanningDiscount.setActive(true);
            weeklyPlanningDiscount.setPriority(1);
            weeklyPlanningDiscount.setApplicableDeadline(DeadlineEnum.ONE_WEEK);

            discountRepository.saveAll(List.of(largeGrayscaleDiscount, mediumGrayscaleDiscount, employeeDiscount, largeOrderDiscount));
            discountRepository.saveAll(List.of(rushOrderPremium, sameDayPremium, weeklyPlanningDiscount));
        }
    }
}