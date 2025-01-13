package com.example.printmatic.service;

import com.example.printmatic.dto.request.RegistrationDTO;
import com.example.printmatic.dto.request.UpdateUserDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.UserDTO;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserService userService;

    private RegistrationDTO registrationDTO;
    private UserEntity userEntity;
    private RoleEntity roleEntity;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {

        registrationDTO = new RegistrationDTO();
        registrationDTO.setEmail("test@example.com");
        registrationDTO.setPassword("password");
        registrationDTO.setFirstName("Test");
        registrationDTO.setLastName("User");
        registrationDTO.setPhoneNumber("1234567890");


        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setFirstName("Test");
        userEntity.setLastName("User");
        userEntity.setPhoneNumber("1234567890");
        userEntity.setBalance(BigDecimal.ZERO);


        roleEntity = new RoleEntity();
        roleEntity.setName(RoleEnum.USER.toString());


        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setFirstName("Updated");
        updateUserDTO.setLastName("User");
        updateUserDTO.setPhoneNumber("0987654321");
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(any(RegistrationDTO.class), eq(UserEntity.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleEnum.USER.toString())).thenReturn(roleEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        Optional<RegistrationDTO> result = userService.registerUser(registrationDTO);

        assertTrue(result.isPresent());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Optional<RegistrationDTO> result = userService.registerUser(registrationDTO);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getCurrentUser_Success() {
        UserDTO userDTO = new UserDTO();
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(modelMapper.map(userEntity, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getCurrentUser(principal);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void updateCurrentUser_Success() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        MessageResponseDTO result = userService.updateCurrentUser(updateUserDTO, principal);

        assertEquals(200, result.status());
        assertEquals("User updated successfully", result.message());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateCurrentUser_UserNotFound() {
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        MessageResponseDTO result = userService.updateCurrentUser(updateUserDTO, principal);

        assertEquals(404, result.status());
        assertEquals("User not found", result.message());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateCurrentUser_EmailAlreadyExists() {
        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("updated@example.com");

        when(principal.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.of(existingUser));

        MessageResponseDTO result = userService.updateCurrentUser(updateUserDTO, principal);

        assertEquals(400, result.status());
        assertEquals("User with this email already exists", result.message());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void seedUsers_Success() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.save(any(RoleEntity.class))).thenReturn(new RoleEntity());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        userService.seedUsers();

        verify(roleRepository, times(3)).save(any(RoleEntity.class));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void seedUsers_SkipWhenUsersExist() {
        when(userRepository.count()).thenReturn(1L);

        userService.seedUsers();

        verify(roleRepository, never()).save(any(RoleEntity.class));
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}


