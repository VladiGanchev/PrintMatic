package com.example.printmatic.controler;

import com.example.printmatic.model.DiscountEntity;
import com.example.printmatic.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/")
    public ResponseEntity<List<DiscountEntity>> getAllDiscounts() {
        List<DiscountEntity> result = discountService.getAllActiveDiscounts();
        return ResponseEntity.status(200).body(result);
    }
}
