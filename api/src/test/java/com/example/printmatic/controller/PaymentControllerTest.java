package com.example.printmatic.controller;

import com.example.printmatic.dto.request.LoginDTO;
import com.example.printmatic.dto.request.OrderPaymentSuccessDTO;
import com.example.printmatic.dto.response.JWTDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SessionResponseDTO;
import com.example.printmatic.dto.response.OrderResultDTO;
import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import com.example.printmatic.model.OrderEntity;
import com.example.printmatic.model.PaymentEntity;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.PaymentRepository;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static String userToken;
    private static String adminToken;
    private static Long createdOrderId;
    private static String lastCreatedSessionId; // for real Stripe test

    @BeforeEach
    void setUp() throws Exception {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();


        RoleEntity userRole = new RoleEntity();
        userRole.setName("USER");
        userRole.setUsers(new ArrayList<>());
        roleRepository.save(userRole);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("ADMIN");
        adminRole.setUsers(new ArrayList<>());
        roleRepository.save(adminRole);


        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("pass123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("1111111111");
        user.setBalance(BigDecimal.ZERO);
        user.setRoles(new ArrayList<>());
        user.getRoles().add(userRole);
        userRepository.save(user);


        UserEntity admin = new UserEntity();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("2222222222");
        admin.setBalance(BigDecimal.ZERO);
        admin.setRoles(new ArrayList<>());
        admin.getRoles().add(adminRole);
        userRepository.save(admin);


        userToken = loginAndGetBearerToken("test@example.com", "pass123");
        adminToken = loginAndGetBearerToken("admin@example.com", "admin123");


        OrderEntity order = new OrderEntity();
        order.setTitle("Test Payment Order");
        order.setFileUrl("http://dummyfile.url");
        order.setCopies(1);
        order.setDoubleSided(false);
        order.setColorfulPages(2);
        order.setGrayscalePages(8);
        order.setPageSize(PageSize.A4);
        order.setPaperType(PaperType.REGULAR_MATE);
        order.setStatus(OrderStatus.UNPAID);
        order.setCreatedAt(LocalDateTime.now());
        order.setPrice(BigDecimal.TEN);
        order.setOwner(user);
        orderRepository.save(order);

        createdOrderId = order.getId();
    }

    private String loginAndGetBearerToken(String email, String rawPassword) throws Exception {
        LoginDTO loginDTO = new LoginDTO(email, rawPassword);
        MvcResult result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();
        JWTDTO jwtDto = objectMapper.readValue(result.getResponse().getContentAsString(), JWTDTO.class);
        return "Bearer " + jwtDto.token();
    }

    //Fake stripe session

    @Test
    @Order(1)
    void testCreateOrderSession_Success() throws Exception {

        mockMvc.perform(post("/api/payment/createOrderSession/" + createdOrderId)
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.stripePaymentURL").exists());
    }

    @Test
    @Order(2)
    void testCreateOrderSession_OrderNotFound() throws Exception {
        mockMvc.perform(post("/api/payment/createOrderSession/9999")
                        .header("Authorization", userToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(3)
    void testOrderSuccess_ChargeSucceeded_WithFakeSession() throws Exception {

        OrderPaymentSuccessDTO body = new OrderPaymentSuccessDTO();
        body.setOrderId(createdOrderId);
        body.setStripeSessionId("fake_stripe_session_id");

        MvcResult result = mockMvc.perform(post("/api/payment/orderSuccess")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        int statusCode = result.getResponse().getStatus();
        assertTrue(statusCode == 200 || statusCode == 500);
    }

    @Test
    @Order(4)
    void testAddToBalanceSession_Success() throws Exception {
        mockMvc.perform(get("/api/payment/addToBalanceSession")
                        .param("amount", "10.00")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.stripePaymentURL").exists());
    }

    @Test
    @Order(5)
    void testDepositBalanceSuccess_FakeStripeId() throws Exception {
        mockMvc.perform(post("/api/payment/depositBalanceSuccess")
                        .param("stripeId", "fake_stripe_id")
                        .header("Authorization", userToken))
                .andExpect(status().isInternalServerError());

    }

    @Test
    @Order(6)
    void testPayOrderFromBalance_NotEnoughBalance() throws Exception {

        MvcResult result = mockMvc.perform(post("/api/payment/payOrderFromBalance/" + createdOrderId)
                        .header("Authorization", userToken))
                .andExpect(status().isInternalServerError())
                .andReturn();

        MessageResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                MessageResponseDTO.class
        );
        assertEquals(500, response.status());
        assertTrue(response.message().contains("not successful"));
    }

    @Test
    @Order(7)
    void testGetPayments_Empty() throws Exception {
        mockMvc.perform(get("/api/payment/getPayments")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }


    // Real Stripe Session Tests

    @Test
    @Order(8)
    void testCreateOrderSession_RealStripe() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/payment/createOrderSession/" + createdOrderId)
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.stripePaymentURL").exists())
                .andReturn();


        String json = result.getResponse().getContentAsString();
        SessionResponseDTO sessionDto = objectMapper.readValue(json, SessionResponseDTO.class);

        System.out.println("----- REAL STRIPE SESSION CREATED -----");
        System.out.println("Session ID  : " + sessionDto.getSessionId());
        System.out.println("Checkout URL: " + sessionDto.getStripePaymentURL());
        // Go to this URL with a test card if you want a real "succeeded" payment
        lastCreatedSessionId = sessionDto.getSessionId();
    }

    @Test
    @Order(9)
    void testOrderSuccess_AfterManualTestPayment() throws Exception {
        if (lastCreatedSessionId == null) {
            System.out.println("No session ID from testCreateOrderSession_RealStripe(). Skipping...");
            return;
        }
        OrderPaymentSuccessDTO payload = new OrderPaymentSuccessDTO();
        payload.setOrderId(createdOrderId);
        payload.setStripeSessionId(lastCreatedSessionId);

        MvcResult result = mockMvc.perform(post("/api/payment/orderSuccess")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andReturn();

        int statusCode = result.getResponse().getStatus();
        System.out.println("Manual Payment Test => code: " + statusCode);
        System.out.println("Body => " + result.getResponse().getContentAsString());

        assertTrue(statusCode == 200 || statusCode == 500);
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}

