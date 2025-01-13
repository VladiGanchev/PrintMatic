package com.example.printmatic.controller;

import com.example.printmatic.dto.request.LoginDTO;
import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.JWTDTO;
import com.example.printmatic.dto.response.OrderResultDTO;
import com.example.printmatic.enums.*;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.OrderRepository;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;
    private String employeeToken;
    private OrderCreationDTO validOrder;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() throws Exception {
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

        RoleEntity employeeRole = new RoleEntity();
        employeeRole.setName("EMPLOYEE");
        employeeRole.setUsers(new ArrayList<>());
        roleRepository.save(employeeRole);

        // Create regular user
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setBalance(BigDecimal.valueOf(1000));
        user.setRoles(new ArrayList<>());
        user.getRoles().add(userRole);
        user.setOrders(new ArrayList<>());
        user.setPayments(new ArrayList<>());
        userRepository.save(user);

        // Create admin user
        UserEntity admin = new UserEntity();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setPhoneNumber("+1987654321");
        admin.setBalance(BigDecimal.valueOf(1000));
        admin.setRoles(new ArrayList<>());
        admin.getRoles().add(adminRole);
        admin.setOrders(new ArrayList<>());
        admin.setPayments(new ArrayList<>());
        userRepository.save(admin);

        UserEntity employee = new UserEntity();
        employee.setEmail("employee@example.com");
        employee.setPassword(passwordEncoder.encode("password123"));
        employee.setFirstName("Employee");
        employee.setLastName("User");
        employee.setPhoneNumber("+1987654322");
        employee.setBalance(BigDecimal.valueOf(1000));
        employee.setRoles(new ArrayList<>());
        employee.getRoles().add(employeeRole);
        employee.setOrders(new ArrayList<>());
        employee.setPayments(new ArrayList<>());
        userRepository.save(employee);


        // Login and get tokens
        userToken = loginAndGetToken("test@example.com", "password123");
        adminToken = loginAndGetToken("admin@example.com", "password123");
        employeeToken = loginAndGetToken("employee@example.com", "password123");

        // Create valid order DTO
        validOrder = new OrderCreationDTO();
        validOrder.setTitle("Test Order");
        validOrder.setCopies(1);
        validOrder.setDoubleSided(false);
        validOrder.setPageSize(PageSize.A4);
        validOrder.setPaperType(PaperType.REGULAR_MATE);
        validOrder.setDeadline(DeadlineEnum.ONE_DAY);
        validOrder.setFileUrl("test-file-url");
        validOrder.setTotalPages(10);
        validOrder.setColorfulPages(2);
        validOrder.setGrayscalePages(8);


    }

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginDTO loginDTO = new LoginDTO(email, password);

        MvcResult result = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JWTDTO jwtdto = objectMapper.readValue(result.getResponse().getContentAsString(), JWTDTO.class);
        return "Bearer " + jwtdto.token();
    }

    @Test
    void createOrder_Success() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResultDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                OrderResultDTO.class
        );

        assertEquals(200, response.getStatus());
        assertNotNull(response.getOrderId());
        assertNotNull(response.getPrice());
    }

    @Test
    void createOrder_InvalidData() throws Exception {
        validOrder.setCopies(-1);

        mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_Success() throws Exception {
        // First create an order
        MvcResult createResult = mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResultDTO createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderResultDTO.class
        );

        // Then get the order
        mockMvc.perform(get("/api/order/" + createResponse.getOrderId())
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.getOrderId()))
                .andExpect(jsonPath("$.title").value(validOrder.getTitle()));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {

        MvcResult createResult = mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResultDTO createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                OrderResultDTO.class
        );

        // Update order status
        mockMvc.perform(post("/api/order/updateOrderStatus/" + createResponse.getOrderId())
                        .header("Authorization", adminToken)
                        .param("orderStatus", OrderStatus.IN_PROGRESS.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void updateOrderStatus_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/order/updateOrderStatus/1")
                        .header("Authorization", userToken)
                        .param("orderStatus", OrderStatus.IN_PROGRESS.name()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserOrders_Success() throws Exception {

        mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isOk());

        // Get user orders
        mockMvc.perform(get("/api/order/user")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value(validOrder.getTitle()));
    }

    @Test
    void getPendingOrInProgress_Success() throws Exception {
        // create an order
        mockMvc.perform(post("/api/order/create")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isOk());

        // fetch pending
        mockMvc.perform(get("/api/order/getPendingOrInProgress")
                        .header("Authorization", adminToken)
                        .param("sortBy", SortBy.DEADLINE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value(OrderStatus.PENDING.name()));
    }

    @Test
    void getPendingOrInProgress_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/order/getPendingOrInProgress")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }
}
