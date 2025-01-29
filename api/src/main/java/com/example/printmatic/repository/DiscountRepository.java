package com.example.printmatic.repository;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {

    @Query("SELECT d FROM DiscountEntity d WHERE d.isActive = true " +
            "AND (d.endDate IS NULL OR d.endDate > :currentDateTime) " +
            "AND d.startDate <= :currentDateTime " +
            "ORDER BY d.priority ASC")
    List<DiscountEntity> findAllActiveDiscounts(@Param("currentDateTime") LocalDateTime currentDateTime);

    /*@Query("SELECT d FROM DiscountEntity d WHERE d.isActive = true " +
            "AND (d.endDate IS NULL OR d.endDate > :currentDateTime) " +
            "AND d.startDate <= :currentDateTime " +
            "AND (d.minimumOrderValue IS NULL OR d.minimumOrderValue <= :orderValue) " +
            "AND (d.minimumPageCount IS NULL OR d.minimumPageCount <= :pageCount) " +
            "AND (d.applicableUserRole IS NULL OR d.applicableUserRole = :userRole) " +
            "ORDER BY d.priority ASC")*/

    @Query("SELECT d FROM DiscountEntity d WHERE d.isActive = true " +
            "AND (d.endDate IS NULL OR d.endDate > :currentDateTime) " +
            "AND d.startDate <= :currentDateTime " +
            "AND (d.minimumOrderValue IS NULL OR d.minimumOrderValue <= :orderValue) " +
            "AND (d.minimumPageCount IS NULL OR d.minimumPageCount <= :pageCount) " +
            "AND (d.applicableUserRole IS NULL OR d.applicableUserRole = :userRole) " +
            "AND d.applicableDeadline IS NULL " +  // This is to exclude the deadline based so to eliminate duplicates
            "ORDER BY d.priority ASC")
    List<DiscountEntity> findApplicableDiscountsAndApplicableDeadlineNull(
            @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("orderValue") BigDecimal orderValue,
            @Param("pageCount") Integer pageCount,
            @Param("userRole") RoleEnum userRole
    );

    List<DiscountEntity> findByIsActiveTrue();

    boolean existsByNameIgnoreCase(String name);

    List<DiscountEntity> findByApplicableDeadlineAndIsActiveTrue(DeadlineEnum applicableDeadline);
}