package com.example.printmatic.service;

import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BigDecimal PRICE_PER_PAGE_A3 = BigDecimal.valueOf(0.30);
    private final BigDecimal PRICE_PER_PAGE_A4 = BigDecimal.valueOf(0.15);
    private final BigDecimal PRICE_PER_PAGE_A5 = BigDecimal.valueOf(0.10);
    private final BigDecimal COLOR_PAGE_MULTIPLIER = BigDecimal.valueOf(5);

    public OrderService(OrderRepository orderRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    public MessageResponseDTO createOrder(OrderCreationDTO orderCreationDTO, Principal principal) {

        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new MessageResponseDTO(404, "User not found");
        }

        int numberOfPages = orderCreationDTO.getTotalPages();
        int colorfulPages = orderCreationDTO.getColorfulPages();
        int grayscalePages = orderCreationDTO.getGrayscalePages();
        int copies = orderCreationDTO.getCopies();

        if (colorfulPages + grayscalePages != numberOfPages) {
            return new MessageResponseDTO(400, "Invalid page count: sum of colorful and grayscale pages must equal total pages");
        }

        OrderEntity orderEntity = modelMapper.map(orderCreationDTO, OrderEntity.class);
        orderEntity.setOwner(user.get());
        orderEntity.setStatus(OrderStatus.PENDING);

        BigDecimal price = calculatePrice(
                numberOfPages,
                colorfulPages,
                grayscalePages,
                copies,
                orderCreationDTO.getPageSize(),
                orderCreationDTO.getPaperType());

        orderEntity.setPrice(price);
        orderRepository.save(orderEntity);

        return new MessageResponseDTO(200, String.format(
                "Order created successfully. Total price: %.2f BGN", price));
    }

    private BigDecimal calculatePrice(int numberOfPages, int colorfulPages, int grayscalePages,
                                     int copies,
                                     PageSize pageSize, PaperType paperType) {

        BigDecimal multiplierNumberOfCopies = determineCopiesMultiplier(copies*grayscalePages);

        BigDecimal basePricePerPage = switch (pageSize) {
            case A3 -> PRICE_PER_PAGE_A3;
            case A4 -> PRICE_PER_PAGE_A4;
            case A5 -> PRICE_PER_PAGE_A5;
        };

        BigDecimal multiplierPaperType = switch (paperType){
            case REGULAR_MATE -> BigDecimal.ONE;
            case GLOSSY -> BigDecimal.valueOf(2);
            case BRIGHT_WHITE -> BigDecimal.valueOf(1.5);
            case PHOTO -> BigDecimal.valueOf(12);
            case HEAVYWEIGHT -> BigDecimal.valueOf(2.5);
        };

        BigDecimal priceForGrayscalePages = basePricePerPage
                .multiply(multiplierNumberOfCopies)
                .multiply(multiplierPaperType)
                .multiply(BigDecimal.valueOf(grayscalePages));

        BigDecimal priceForColorfulPages = basePricePerPage
                .multiply(COLOR_PAGE_MULTIPLIER)
                .multiply(multiplierPaperType)
                .multiply(BigDecimal.valueOf(colorfulPages));

        BigDecimal pricePerDocument = priceForColorfulPages.add(priceForGrayscalePages);

        return pricePerDocument.multiply(BigDecimal.valueOf(copies));
    }

    //for grayscale pages
    private BigDecimal determineCopiesMultiplier(int allGrayscalePages) {
        if (allGrayscalePages > 100) return BigDecimal.valueOf(0.7);  // 30% discount for > 100 pages
        if (allGrayscalePages >= 21) return BigDecimal.valueOf(0.9);  // 10% discount for 21-100 pages
        return BigDecimal.ONE;                             // No discount for 1-20 pages
    }

}
