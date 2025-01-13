package com.example.printmatic.service;

import com.example.printmatic.dto.request.OrderPaymentSuccessDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.PaymentRepository;
import com.example.printmatic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private UserEntity testUser;
    private OrderEntity testOrder;
    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {

        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();


        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("password123");
        testUser.setBalance(BigDecimal.valueOf(1000.00));
        testUser.setOrders(new ArrayList<>());
        testUser.setPayments(new ArrayList<>());
        testUser.setRoles(new ArrayList<>());
        testUser = userRepository.save(testUser);


        testOrder = new OrderEntity();
        testOrder.setTitle("Test Order");
        testOrder.setFileUrl("http://example.com/test.pdf");
        testOrder.setCopies(1);
        testOrder.setDoubleSided(false);
        testOrder.setColorfulPages(10);
        testOrder.setGrayscalePages(0);
        testOrder.setPageSize(PageSize.A4);
        testOrder.setPaperType(PaperType.REGULAR_MATE);
        testOrder.setStatus(OrderStatus.UNPAID);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setPrice(BigDecimal.valueOf(100.00));
        testOrder.setOwner(testUser);
        testOrder = orderRepository.save(testOrder);


        mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(testUser.getEmail());
    }

    @Test
    @Transactional
    void createSessionForPayingOrder_ShouldCreateValidSession() {
        // When
        SessionResponseDTO sessionResponse = paymentService.createSessionForPayingOrder(testOrder.getId(), mockPrincipal);

        // Then
        assertNotNull(sessionResponse);
        assertNotNull(sessionResponse.getSessionId());
        assertNotNull(sessionResponse.getStripePaymentURL());
        assertTrue(sessionResponse.getStripePaymentURL().contains("checkout.stripe.com"));
    }

    @Test
    @Transactional
    void orderSuccess_ShouldProcessPaymentSuccessfully() {
        // Given
        // Create a real Stripe session with a test card
        SessionResponseDTO sessionResponse = paymentService.createSessionForPayingOrder(testOrder.getId(), mockPrincipal);

        OrderPaymentSuccessDTO successDTO = new OrderPaymentSuccessDTO();
        successDTO.setOrderId(testOrder.getId());
        successDTO.setStripeSessionId(sessionResponse.getSessionId());

        // When
        MessageResponseDTO response = paymentService.orderSuccess(successDTO, mockPrincipal);

        // Then
        assertEquals(200, response.status());
        assertEquals("Payment successful", response.message());

        // Verify database state
        OrderEntity updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PENDING, updatedOrder.getStatus());
        assertNotNull(updatedOrder.getPayment());
        assertEquals(sessionResponse.getSessionId(), updatedOrder.getPayment().getStripeSessionId());
    }

    @Test
    @Transactional
    void depositBalanceSuccess_ShouldUpdateUserBalance() {
        // Given
        BigDecimal depositAmount = BigDecimal.valueOf(500.00);
        SessionResponseDTO sessionResponse = paymentService.addToBalanceSession(depositAmount, mockPrincipal);
        BigDecimal initialBalance = testUser.getBalance();

        // When
        MessageResponseDTO response = paymentService.depositBalanceSuccess(sessionResponse.getSessionId(), mockPrincipal);

        // Then
        assertEquals(200, response.status());
        assertEquals("Deposit balance successful", response.message());

        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.getBalance().compareTo(initialBalance) > 0);
    }

    @Test
    @Transactional
    void payFromBalance_ShouldSucceedWithSufficientBalance() {
        // Given
        BigDecimal initialBalance = testUser.getBalance();
        BigDecimal orderPrice = testOrder.getPrice();

        // When
        MessageResponseDTO response = paymentService.payFromBalance(testOrder.getId(), mockPrincipal);

        // Then
        assertEquals(200, response.status());
        assertEquals("Payment from balance successful", response.message());

        // Verify user balance updated
        UserEntity updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(initialBalance.subtract(orderPrice), updatedUser.getBalance());

        // Verify order status
        OrderEntity updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PENDING, updatedOrder.getStatus());
        assertNotNull(updatedOrder.getPayment());
    }

    @Test
    @Transactional
    void payFromBalance_ShouldFailWithInsufficientBalance() {

        testUser.setBalance(BigDecimal.valueOf(50.00)); // Less than order price
        userRepository.save(testUser);

        MessageResponseDTO response = paymentService.payFromBalance(testOrder.getId(), mockPrincipal);


        assertEquals(500, response.status());
        assertEquals("Payment not successful", response.message());

        OrderEntity updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.UNPAID, updatedOrder.getStatus());
        assertNull(updatedOrder.getPayment());
    }
}
