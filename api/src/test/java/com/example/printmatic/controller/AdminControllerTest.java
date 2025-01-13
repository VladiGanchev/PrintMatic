package com.example.printmatic.controller;

import com.example.printmatic.dto.request.LoginDTO;
import com.example.printmatic.dto.response.JWTDTO;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.init.DbInit;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import com.example.printmatic.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    @MockBean
    private DbInit dbInit;

    private final Gson gson = new Gson();

    private static final String ADMIN_EMAIL = "john@doe.com";
    private static final String ADMIN_PASSWORD = "password";

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
        roleRepository.deleteAll();


        userService.seedUsers();
    }


    private String loginAsAdminAndGetToken() throws Exception {
        LoginDTO loginDTO = new LoginDTO(ADMIN_EMAIL, ADMIN_PASSWORD);

        String responseString = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JWTDTO jwtDto = gson.fromJson(responseString, JWTDTO.class);
        return jwtDto.token();
    }

    @Test
    void testGrantRole_Success() throws Exception {

        UserEntity user = new UserEntity();
        user.setEmail("someuser@example.com");
        user.setPassword(passwordEncoder.encode("somePassword"));
        user.setFirstName("Some");
        user.setLastName("User");
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        String token = loginAsAdminAndGetToken();

        mockMvc.perform(post("/api/admin/grantRole")
                        .param("email", "someuser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Optional<UserEntity> updatedUser = userRepository.findByEmail("someuser@example.com");
        assertTrue(updatedUser.isPresent(), "User should exist after role is granted");
        assertTrue(
                updatedUser.get().getRoles().stream().anyMatch(r -> r.getName().equals("EMPLOYEE")),
                "User should have EMPLOYEE role"
        );
    }

    @Test
    void testGrantRole_Unauthorized_NoToken() throws Exception {

        mockMvc.perform(post("/api/admin/grantRole")
                        .param("email", "someuser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGrantRole_Unauthorized_NonAdminToken() throws Exception {

        UserEntity regularUser = new UserEntity();
        regularUser.setEmail("notadmin@example.com");
        regularUser.setPassword(passwordEncoder.encode("somePassword"));
        regularUser.setFirstName("Not");
        regularUser.setLastName("Admin");
        regularUser.setBalance(BigDecimal.ZERO);
        userRepository.save(regularUser);


        LoginDTO loginDTO = new LoginDTO("notadmin@example.com", "somePassword");
        String responseString = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JWTDTO regularUserJwt = gson.fromJson(responseString, JWTDTO.class);


        mockMvc.perform(post("/api/admin/grantRole")
                        .param("email", "someuser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name())
                        .header("Authorization", "Bearer " + regularUserJwt.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRemoveRole_Success() throws Exception {

        UserEntity user = new UserEntity();
        user.setRoles(new ArrayList<>());
        user.setEmail("roleUser@example.com");
        user.setPassword(passwordEncoder.encode("somePassword"));
        user.setFirstName("Employee");
        user.setLastName("User");
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        RoleEntity employeeRole = roleRepository.findByName(RoleEnum.EMPLOYEE.name());
        user.getRoles().add(employeeRole);
        userRepository.save(user);


        String token = loginAsAdminAndGetToken();


        mockMvc.perform(delete("/api/admin/removeRole")
                        .param("email", "roleUser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());


        Optional<UserEntity> updatedUser = userRepository.findByEmail("roleUser@example.com");
        assertTrue(updatedUser.isPresent());
        boolean hasEmployee = updatedUser.get().getRoles().stream()
                .anyMatch(r -> r.getName().equals("EMPLOYEE"));
        assertFalse(hasEmployee, "User should no longer have EMPLOYEE role after removal.");
    }
    @Test
    void testRemoveRole_Unauthorized_EmployeeToken() throws Exception {

        UserEntity employeeUser = new UserEntity();
        employeeUser.setRoles(new ArrayList<>());
        employeeUser.setEmail("employee@example.com");
        employeeUser.setPassword(passwordEncoder.encode("somePassword"));
        employeeUser.setFirstName("Employee");
        employeeUser.setLastName("User");
        employeeUser.setBalance(BigDecimal.ZERO);
        userRepository.save(employeeUser);


        RoleEntity employeeRole = roleRepository.findByName(RoleEnum.EMPLOYEE.name());
        employeeUser.getRoles().add(employeeRole);
        userRepository.save(employeeUser);

        UserEntity targetUser = new UserEntity();
        targetUser.setRoles(new ArrayList<>());
        targetUser.setEmail("roleUser@example.com");
        targetUser.setPassword(passwordEncoder.encode("somePassword"));
        targetUser.setFirstName("Target");
        targetUser.setLastName("User");
        targetUser.setBalance(BigDecimal.ZERO);
        targetUser.getRoles().add(employeeRole);
        userRepository.save(targetUser);

        LoginDTO loginDTO = new LoginDTO("employee@example.com", "somePassword");
        String responseString = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JWTDTO employeeJwt = gson.fromJson(responseString, JWTDTO.class);


        mockMvc.perform(delete("/api/admin/removeRole")
                        .param("email", "roleUser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name())
                        .header("Authorization", "Bearer " + employeeJwt.token()))
                .andExpect(status().isForbidden());

        // 4) Verify that the role was not removed
        Optional<UserEntity> roleUser = userRepository.findByEmail("roleUser@example.com");
        assertTrue(roleUser.isPresent());
        boolean stillHasRole = roleUser.get().getRoles().stream()
                .anyMatch(r -> r.getName().equals("EMPLOYEE"));
        assertTrue(stillHasRole, "Role should not be removed by an unauthorized employee.");
    }
    @Test
    void testRemoveRole_Unauthorized_NormalUserToken() throws Exception {

        UserEntity roleUser = new UserEntity();
        roleUser.setRoles(new ArrayList<>());
        roleUser.setEmail("roleUser@example.com");
        roleUser.setPassword(passwordEncoder.encode("rolePassword"));
        roleUser.setFirstName("Role");
        roleUser.setLastName("User");
        roleUser.setBalance(BigDecimal.ZERO);
        userRepository.save(roleUser);


        RoleEntity employeeRole = roleRepository.findByName(RoleEnum.EMPLOYEE.name());
        if (employeeRole == null) {
            employeeRole = new RoleEntity();
            employeeRole.setName(RoleEnum.EMPLOYEE.name());
            roleRepository.save(employeeRole);
        }
        roleUser.getRoles().add(employeeRole);
        userRepository.save(roleUser);

        UserEntity normalUser = new UserEntity();
        normalUser.setRoles(new ArrayList<>());
        normalUser.setEmail("normaluser@example.com");
        normalUser.setPassword(passwordEncoder.encode("userPassword"));
        normalUser.setFirstName("Normal");
        normalUser.setLastName("User");
        normalUser.setBalance(BigDecimal.ZERO);
        userRepository.save(normalUser);


        LoginDTO loginDTO = new LoginDTO("normaluser@example.com", "userPassword");
        String responseString = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JWTDTO normalUserJwt = gson.fromJson(responseString, JWTDTO.class);


        mockMvc.perform(delete("/api/admin/removeRole")
                        .param("email", "roleUser@example.com")
                        .param("role", RoleEnum.EMPLOYEE.name())
                        .header("Authorization", "Bearer " + normalUserJwt.token()))
                .andExpect(status().isForbidden());


        Optional<UserEntity> targetUser = userRepository.findByEmail("roleUser@example.com");
        assertTrue(targetUser.isPresent());
        boolean stillHasRole = targetUser.get().getRoles().stream()
                .anyMatch(r -> r.getName().equals(RoleEnum.EMPLOYEE.name()));
        assertTrue(stillHasRole, "Role should not be removed by an unauthorized normal user.");
    }

    @Test
    void testSearchUser_Success() throws Exception {

        UserEntity user = new UserEntity();
        user.setEmail("alice@example.com");
        user.setPassword(passwordEncoder.encode("somePassword"));
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        String token = loginAsAdminAndGetToken();


        String response = mockMvc.perform(get("/api/admin/search/email")
                        .param("query", "alice")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertTrue(response.contains("alice@example.com"), "Search results should include user with 'alice@example.com'");
    }
    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
