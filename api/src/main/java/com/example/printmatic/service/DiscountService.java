package com.example.printmatic.service;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.printmatic.dto.DiscountDTO;
import com.example.printmatic.dto.DiscountUpdateDTO;
import com.example.printmatic.dto.request.DiscountCreationDTO;
import com.example.printmatic.dto.response.DiscountListResponseDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.DiscountEntity;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.DiscountRepository;
import com.example.printmatic.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;

@Slf4j
@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    public DiscountService(DiscountRepository discountRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.discountRepository = discountRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    /**
     * Calculate final price after applying eligible discounts
     * @param originalPrice The original price before discounts
     * @param pageCount Total number of pages in the order
     * @param user The user making the order
     * @return Final price after applying eligible discounts
     */
    public Pair<BigDecimal, List<String>> calculateFinalPrice(BigDecimal originalPrice, Integer pageCount, UserEntity user, DeadlineEnum deadline) {
        RoleEnum userRole = determineHighestUserRole(user);

        // Get all applicable discounts including deadline-specific ones
        List<DiscountEntity> applicableDiscounts = discountRepository.findApplicableDiscountsAndApplicableDeadlineNull(
                LocalDateTime.now(),
                originalPrice,
                pageCount,
                userRole
        );

        // Get deadline-specific discounts
        List<DiscountEntity> deadlineDiscount = discountRepository.findByApplicableDeadlineAndIsActiveTrue(deadline);

        // Add only matching deadline discount
        if (!deadlineDiscount.isEmpty()) {
            Optional<DiscountEntity> highestPriorityDeadlineDiscount = deadlineDiscount.stream()
                    .max(Comparator.comparing(DiscountEntity::getPriority));

            highestPriorityDeadlineDiscount.ifPresent(applicableDiscounts::add);
        }

        // Sort by priority to ensure consistent application
        applicableDiscounts.sort(Comparator.comparing(DiscountEntity::getPriority));

        BigDecimal finalPrice = originalPrice;
        BigDecimal totalPercentageDiscount = BigDecimal.ZERO;
        List<String> appliedDiscountDescriptions = new ArrayList<>();

        // First apply non-stackable discounts (only the highest priority one)
        Optional<DiscountEntity> bestNonStackableDiscount = applicableDiscounts.stream()
                .filter(d -> !d.isStackable())
                .findFirst();

        if (bestNonStackableDiscount.isPresent()) {
            finalPrice = applyDiscount(finalPrice, bestNonStackableDiscount.get());
            appliedDiscountDescriptions.add(formatDiscountDescription(bestNonStackableDiscount.get()));
        }

        // Then apply stackable discounts
        for (DiscountEntity discount : applicableDiscounts) {
            if (discount.isStackable()) {
                if (discount.isPercentage()) {
                    // Accumulate percentage discounts to apply them together
                    totalPercentageDiscount = totalPercentageDiscount.add(discount.getValue());
                } else {
                    // Apply fixed discounts immediately
                    finalPrice = applyDiscount(finalPrice, discount);
                }
                appliedDiscountDescriptions.add(formatDiscountDescription(discount));

            }
        }

        // Apply accumulated percentage discount
        if (totalPercentageDiscount.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal multiplier = BigDecimal.ONE.subtract(
                    totalPercentageDiscount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );
            finalPrice = finalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }

        return Pair.of(finalPrice.max(BigDecimal.ZERO), appliedDiscountDescriptions);
    }

    private String formatDiscountDescription(DiscountEntity discount) {
        String value = discount.getValue().abs().toString();
        String type = discount.isPercentage() ? "%" : " лв.";
        String effect = discount.getValue().compareTo(BigDecimal.ZERO) > 0 ? "отстъпка" : "надценка";

        return String.format("%s: %s%s %s", discount.getName(), value, type, effect);
    }

    /**
     * Apply a single discount to a price
     */
    private BigDecimal applyDiscount(BigDecimal price, DiscountEntity discount) {
        if (discount.isPercentage()) {
            BigDecimal multiplier = BigDecimal.ONE.subtract(
                    discount.getValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );
            return price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        } else {
            return price.subtract(discount.getValue()).max(BigDecimal.ZERO);
        }
    }

    /**
     * Determine the highest priority role of a user
     */
    private RoleEnum determineHighestUserRole(UserEntity user) {
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.name()))) {
            return RoleEnum.ADMIN;
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.EMPLOYEE.name()))) {
            return RoleEnum.EMPLOYEE;
        }
        return RoleEnum.USER;
    }
    /// Designed for api methods
    /**
     * Add a new discount
     */

    public DiscountListResponseDTO getAllDiscountsDTO(Principal principal) {
        // Verify user exists
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new DiscountListResponseDTO(null, 404, "User not found");
        }

        List<DiscountEntity> discounts = getAllActiveDiscounts();
        List<DiscountDTO> discountDTOs = discounts.stream()
                .map(discount -> modelMapper.map(discount, DiscountDTO.class))
                .collect(Collectors.toList());
        return new DiscountListResponseDTO(discountDTOs, 200, "Discounts fetched successfully");
    }
    @Transactional
    public MessageResponseDTO createDiscount(DiscountCreationDTO discountDTO, Principal principal) {
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new MessageResponseDTO(404, "User not found");
        }

        try {
            if (discountRepository.existsByNameIgnoreCase(discountDTO.getName())) {
                return new MessageResponseDTO(400, "Discount with this name already exists");
            }

            DiscountEntity discount = modelMapper.map(discountDTO, DiscountEntity.class);
            validateDiscount(discount);
            discountRepository.save(discount);
            return new MessageResponseDTO(200, "Discount created successfully");
        } catch (IllegalArgumentException e) {
            return new MessageResponseDTO(400, e.getMessage());
        }
    }

    @Transactional
    public DiscountEntity addDiscount(DiscountEntity discount) {
        if (discountRepository.existsByNameIgnoreCase(discount.getName())) {
            throw new IllegalArgumentException("Discount with this name already exists");
        }

        validateDiscount(discount);
        return discountRepository.save(discount);
    }

    /**
     * Update an existing discount
     */
    @Transactional
    public MessageResponseDTO updateDiscountDTO(Long id, DiscountUpdateDTO updateDTO, Principal principal) {
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new MessageResponseDTO(404, "User not found");
        }

        try {
            DiscountEntity existingDiscount = discountRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Discount not found"));

            existingDiscount.setValue(updateDTO.getValue());
            existingDiscount.setPriority(updateDTO.getPriority());
            existingDiscount.setDescription(updateDTO.getDescription());

            validateDiscount(existingDiscount);
            discountRepository.save(existingDiscount);
            return new MessageResponseDTO(200, "Discount updated successfully");
        } catch (IllegalArgumentException e) {
            return new MessageResponseDTO(400, e.getMessage());
        }
    }

    @Transactional
    public MessageResponseDTO deactivateDiscountWithResponse(Long id, Principal principal) {
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new MessageResponseDTO(404, "User not found");
        }

        try {
            deactivateDiscount(id);
            return new MessageResponseDTO(200, "Discount deactivated successfully");
        } catch (IllegalArgumentException e) {
            return new MessageResponseDTO(400, e.getMessage());
        }
    }


    /**
     * Validate discount entity
     */
    private void validateDiscount(DiscountEntity discount) {
        if (discount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be positive");
        }

        if (discount.isPercentage() && discount.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }

        if (discount.getStartDate() != null && discount.getEndDate() != null &&
                discount.getStartDate().isAfter(discount.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (discount.getMinimumOrderValue() != null &&
                discount.getMinimumOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Minimum order value cannot be negative");
        }

        if (discount.getMinimumPageCount() != null && discount.getMinimumPageCount() < 0) {
            throw new IllegalArgumentException("Minimum page count cannot be negative");
        }
    }

    /**
     * Get all active discounts
     */
    public List<DiscountEntity> getAllActiveDiscounts() {
        return discountRepository.findAllActiveDiscounts(LocalDateTime.now());
    }

    /**
     * Get discount by ID
     */
    public Optional<DiscountEntity> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    /**
     * Deactivate a discount
     */
    @Transactional
    public void deactivateDiscount(Long id) {
        DiscountEntity discount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discount not found"));
        discount.setActive(false);
        discountRepository.save(discount);
    }
}