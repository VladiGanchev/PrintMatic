package com.example.printmatic.controller;

import com.example.printmatic.dto.request.RegistrationDTO;
import com.example.printmatic.dto.request.LoginDTO;
import com.example.printmatic.init.DbInit;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import com.example.printmatic.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    @MockBean
    DbInit dbInit;

    private final Gson gson = new Gson();
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Transactional
    @Test
    void registerSuccess() throws Exception {
        String email = "user@gmail.com";
        String password = "password@gmail.com";
        RegistrationDTO registrationDTO = new RegistrationDTO(email, password, "Test", "Testov", "0887154628");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(registrationDTO)))
                .andExpect(status().isOk());
        Assertions.assertEquals(userRepository.count(), 1);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty())
            Assertions.fail("Cannot find user with email just registered");

        Assertions.assertEquals(user.get().getEmail(), email);
        Assertions.assertTrue(passwordEncoder.matches(password, user.get().getPassword()));
    }

    @Test
    void registerFail() throws Exception {
        userService.seedUsers();
        RegistrationDTO registrationDTO = new RegistrationDTO("invalidahhmail", "password", "user", "userov", "0887154628");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(registrationDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void loginSuccess() throws Exception {
        userService.seedUsers();
        LoginDTO loginDTO = new LoginDTO("john@doe.com", "password");

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginDTO))
        ).andExpect(status().isOk());
    }

    @Test
    void loginFail() throws Exception {
        userService.seedUsers();

        LoginDTO loginDTO = new LoginDTO("user@gmail.com", "password1");
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isForbidden());

        loginDTO = new LoginDTO("invalid", "password");
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isForbidden());

        loginDTO = new LoginDTO("user@gmail.com", "");
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(loginDTO)))
                .andExpect(status().isForbidden());
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}

