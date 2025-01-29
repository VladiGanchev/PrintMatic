package com.example.printmatic.service;

import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.OrderDTO;
import com.example.printmatic.dto.response.OrderResultDTO;
import com.example.printmatic.dto.response.UserOrderDTO;
import com.example.printmatic.enums.*;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.ServicePriceRepository;
import com.example.printmatic.repository.UserRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServicePriceService servicePriceService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private MailService mailService;

    @Mock
    private GoogleCloudStorageService googleCloudStorageService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @Mock
    private Principal principal;

    @InjectMocks
    private OrderService orderService;

    private UserEntity testUser;
    private OrderEntity testOrder;
    private OrderCreationDTO testOrderCreationDTO;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setBalance(BigDecimal.valueOf(100));

        testOrderCreationDTO = new OrderCreationDTO();
        testOrderCreationDTO.setTitle("Test Order");
        testOrderCreationDTO.setTotalPages(10);
        testOrderCreationDTO.setColorfulPages(2);
        testOrderCreationDTO.setGrayscalePages(8);
        testOrderCreationDTO.setCopies(1);
        testOrderCreationDTO.setPageSize(PageSize.A4);
        testOrderCreationDTO.setPaperType(PaperType.REGULAR_MATE);
        testOrderCreationDTO.setDeadline(DeadlineEnum.ONE_DAY);

        testOrder = new OrderEntity();
        testOrder.setId(1L);
        testOrder.setOwner(testUser);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setPrice(BigDecimal.valueOf(10));
        testOrder.setTitle("Test Order");
    }

    @Test
    void createOrder_Success() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(servicePriceService.calculateOrderPrice(any(), any())).thenReturn(Pair.of(BigDecimal.ZERO, ""));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(testOrder);

        OrderResultDTO result = orderService.createOrder(testOrderCreationDTO, principal);

        assertEquals(200, result.getStatus());
        assertNotNull(result.getPrice());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void createOrder_UserNotFound() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        OrderResultDTO result = orderService.createOrder(testOrderCreationDTO, principal);

        assertEquals(404, result.getStatus());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void createOrder_InvalidPageCount() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        testOrderCreationDTO.setColorfulPages(5);
        testOrderCreationDTO.setGrayscalePages(2);
        testOrderCreationDTO.setTotalPages(10);

        OrderResultDTO result = orderService.createOrder(testOrderCreationDTO, principal);

        assertEquals(400, result.getStatus());
        assertTrue(result.getMessage().contains("Invalid page count"));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        MessageResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.IN_PROGRESS);

        assertEquals(200, result.status());
        verify(orderRepository).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        MessageResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.IN_PROGRESS);

        assertEquals(404, result.status());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderStatus_InvalidStatusTransition() {
        testOrder.setStatus(OrderStatus.COMPLETED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        MessageResponseDTO result = orderService.updateOrderStatus(1L, OrderStatus.IN_PROGRESS);

        assertEquals(400, result.status());
        verify(orderRepository, never()).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderStatus_RefundOnCancellation() {
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setPrice(BigDecimal.TEN);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        orderService.updateOrderStatus(1L, OrderStatus.CANCELED);

        verify(userRepository).save(any(UserEntity.class));
        assertEquals(testUser.getBalance(), BigDecimal.valueOf(110));
    }

    @Test
    void getOrdersOfUser_Success() {
        Page<OrderEntity> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(orderRepository.findAllByOwnerIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(orderPage);
        when(googleCloudStorageService.generateClientDownloadUrl(any())).thenReturn("test-url");

        Page<UserOrderDTO> result = orderService.getOrdersOfUser(principal, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getOrders_Success() {
        Page<OrderEntity> orderPage = new PageImpl<>(Collections.singletonList(testOrder));
        when(orderRepository.findAllByStatusesInSorted(any(), any(), any(Pageable.class)))
                .thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrders(SortBy.DEADLINE, PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<OrderDTO> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(testOrder.getId(), result.get().getId());
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<OrderDTO> result = orderService.getOrderById(1L);

        assertTrue(result.isEmpty());
    }
}
