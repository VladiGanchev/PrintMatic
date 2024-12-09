package com.example.printmatic.model;

import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private Integer copies;


    @Column(nullable = false)
    private boolean doubleSided;

    private Integer colorfulPages;

    private Integer grayscalePages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PageSize pageSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaperType paperType;

    private String additionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deadline;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;
}
