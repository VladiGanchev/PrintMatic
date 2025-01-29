package com.example.printmatic.controler;

import com.example.printmatic.dto.DiscountUpdateDTO;
import com.example.printmatic.dto.request.DiscountCreationDTO;
import com.example.printmatic.dto.response.DiscountListResponseDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/discounts")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/")
    public ResponseEntity<DiscountListResponseDTO> getAllDiscounts(Principal principal) {
        DiscountListResponseDTO result = discountService.getAllDiscountsDTO(principal);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createDiscount(
            @Valid @RequestBody DiscountCreationDTO discountDTO,
            Principal principal) {
        MessageResponseDTO result = discountService.createDiscount(discountDTO, principal);
        return ResponseEntity.status(result.status()).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountUpdateDTO updateDTO,
            Principal principal) {
        MessageResponseDTO result = discountService.updateDiscountDTO(id, updateDTO, principal);
        return ResponseEntity.status(result.status()).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deactivateDiscount(
            @PathVariable Long id,
            Principal principal) {
        MessageResponseDTO result = discountService.deactivateDiscountWithResponse(id, principal);
        return ResponseEntity.status(result.status()).body(result);
    }
}