package com.example.printmatic.service;

import com.example.printmatic.dto.EmailContentEvent;
import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.OrderDTO;
import com.example.printmatic.dto.response.OrderResultDTO;
import com.example.printmatic.dto.response.UserOrderDTO;
import com.example.printmatic.enums.*;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.UserRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MailService mailService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ServicePriceService servicePriceService;
    private final GoogleCloudStorageService googleCloudStorageService;

    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher,
                        MailService mailService, ModelMapper modelMapper, UserRepository userRepository,
                        ServicePriceService servicePriceService, GoogleCloudStorageService googleCloudStorageService) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.mailService = mailService;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.servicePriceService = servicePriceService;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public OrderResultDTO createOrder(OrderCreationDTO orderCreationDTO, Principal principal) {
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            return new OrderResultDTO(-1L,BigDecimal.ZERO, null, 404, "User not found");
        }
        if (orderCreationDTO.getDeadline() == null) {
            return new OrderResultDTO(-1L,BigDecimal.ZERO, null, 401, "Deadline not set");
        }

        int numberOfPages = orderCreationDTO.getTotalPages();
        int colorfulPages = orderCreationDTO.getColorfulPages();
        int grayscalePages = orderCreationDTO.getGrayscalePages();
        int copies = orderCreationDTO.getCopies();

        if (colorfulPages + grayscalePages != numberOfPages) {
            return new OrderResultDTO(-1L,BigDecimal.ZERO, null, 400, "Invalid page count: sum of colorful and grayscale pages must equal total pages");
        }

        OrderEntity orderEntity = modelMapper.map(orderCreationDTO, OrderEntity.class);
        orderEntity.setOwner(user.get());
        orderEntity.setStatus(OrderStatus.UNPAID);

        Pair<BigDecimal, String> price = servicePriceService.calculateOrderPrice(orderCreationDTO, copies, user);

        orderEntity.setPrice(price.getLeft());

        //calc deadline
        LocalDateTime deadline = switch (orderCreationDTO.getDeadline()) {
            case ONE_HOUR -> LocalDateTime.now().plusHours(1);
            case ONE_DAY -> LocalDateTime.now().plusDays(1);
            case THREE_DAYS -> LocalDateTime.now().plusDays(3);
            case ONE_WEEK -> LocalDateTime.now().plusWeeks(1);
        };
        orderEntity.setDeadline(deadline);
        orderEntity.setCreatedAt(LocalDateTime.now());

        orderRepository.save(orderEntity);

        return new OrderResultDTO(
                orderEntity.getId(),
                orderEntity.getPrice(),
                price.getRight(),
                200,
                String.format("Order created successfully. Total price: %.2f BGN", price.getLeft()));
    }

    //for grayscale pages
    private BigDecimal determineCopiesMultiplier(int allGrayscalePages) {
        if (allGrayscalePages > 100) return BigDecimal.valueOf(0.7);  // 30% discount for > 100 pages
        if (allGrayscalePages >= 21) return BigDecimal.valueOf(0.9);  // 10% discount for 21-100 pages
        return BigDecimal.ONE;                             // No discount for 1-20 pages
    }

    //  TODO: Implement other colors deiscounts

    public Page<OrderDTO> getOrders(SortBy sortBy, Pageable pageable) {
        //order by deadline
        if (sortBy == null) {
            sortBy = SortBy.DEADLINE;
        }
        if (pageable == null) {
            pageable = Pageable.ofSize(10).first();
        }
        return orderRepository.findAllByStatusesInSorted(List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS),sortBy.name(), pageable)
                .map(orderEntity -> modelMapper.map(orderEntity, OrderDTO.class));

    }

    public Page<UserOrderDTO> getOrdersOfUser(Principal principal, Pageable pageable) {
        UserEntity user = userRepository.findByEmail(principal.getName()).get();


        return orderRepository.findAllByOwnerIdOrderByCreatedAtDesc(user.getId(), pageable)
                 .map(orderEntity -> {
                     UserOrderDTO map = modelMapper.map(orderEntity, UserOrderDTO.class);
                     map.setDocumentUrl(googleCloudStorageService.generateClientDownloadUrl(orderEntity.getFileUrl()));
                     return map;
                 });
    }

    public MessageResponseDTO updateOrderStatus(Long id, OrderStatus orderStatus) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isEmpty()) {
            return new MessageResponseDTO(404,"Order with this id has not been found");
        }


        OrderEntity order = orderEntity.get();
        MessageResponseDTO messageResponseDTO = validateStatus(orderStatus, order.getStatus());

        if(messageResponseDTO.status() == 400)
            return messageResponseDTO;

        UserEntity orderOwner = order.getOwner();

        if(orderStatus == OrderStatus.CANCELED || orderStatus == OrderStatus.REJECTED) {
            orderOwner.setBalance(orderOwner.getBalance().add(order.getPrice()));
            userRepository.save(orderOwner);
        }

        if(orderStatus == OrderStatus.COMPLETED){
            EmailContentEvent emailContentEvent = new EmailContentEvent(
                    orderOwner.getEmail(),
                    "Your order '" + order.getTitle() + "' is ready for pickup - PrintMatic Copy Center",
                    mailService.createHtmlBody(order, orderOwner)
            );
            eventPublisher.publishEvent(emailContentEvent);
        }

        order.setStatus(orderStatus);
        orderRepository.save(order);
        return new MessageResponseDTO(200,"Order status has been updated");
    }

    private MessageResponseDTO validateStatus(OrderStatus updateStatus, OrderStatus currentStatus) {
        switch (updateStatus) {
            case IN_PROGRESS -> {
                if (currentStatus == OrderStatus.PENDING) {
                    break;
                }
                return new MessageResponseDTO(400, "Order can only move to in-progress from pending state");
            }
            case COMPLETED -> {
                if (currentStatus == OrderStatus.IN_PROGRESS) {
                    break;
                }
                return new MessageResponseDTO(400, "Order can only be completed from in-progress state");
            }
            case CANCELED -> {
                if (currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.IN_PROGRESS) {
                    break;
                }
                return new MessageResponseDTO(400, "Orders can only be cancelled while pending or in-progress");
            }
            case REJECTED -> {
                if (currentStatus == OrderStatus.PENDING) {
                    break;
                }
                return new MessageResponseDTO(400, "Orders can only be rejected in pending state");
            }
        }
        return new MessageResponseDTO(200,"Order status is correct");
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        return orderEntity.map(entity -> modelMapper.map(entity, OrderDTO.class));
    }
}
