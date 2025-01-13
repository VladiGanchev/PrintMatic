package com.example.printmatic.service;

import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SearchUserDTO;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AdminService adminService;

    private UserEntity testUser;
    private RoleEntity testRole;
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setEmail(TEST_EMAIL);
        testUser.setRoles(new ArrayList<>());

        testRole = new RoleEntity();
        testRole.setName(RoleEnum.EMPLOYEE.name());
        testRole.setUsers(new ArrayList<>());
    }

    @Test
    void grantRole_WhenUserNotFound_ReturnsNotFoundMessage() {

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        MessageResponseDTO response = adminService.grantRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(404, response.status());
        assertEquals("User whit this email not found", response.message());
        verify(userRepository, never()).save(any());
    }

    @Test
    void grantRole_WhenRoleAlreadyGranted_ReturnsBadRequestMessage() {

        testUser.getRoles().add(testRole);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleEnum.EMPLOYEE.name())).thenReturn(testRole);

        MessageResponseDTO response = adminService.grantRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(400, response.status());
        assertEquals("This role already is granted to this user", response.message());
        verify(userRepository, never()).save(any());
    }

    @Test
    void grantRole_Success() {

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleEnum.EMPLOYEE.name())).thenReturn(testRole);
        when(userRepository.save(any())).thenReturn(testUser);

        MessageResponseDTO response = adminService.grantRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(200, response.status());
        assertEquals("Role successfully granted to the user", response.message());
        verify(userRepository).save(testUser);
        assertTrue(testUser.getRoles().contains(testRole));
    }

    @Test
    void searchUser_ReturnsMatchingUsers() {

        List<UserEntity> userEntities = List.of(
                createUserEntity("john@example.com"),
                createUserEntity("jane@example.com"),
                createUserEntity("bob@example.com")
        );

        List<SearchUserDTO> userDTOs = List.of(
                createSearchUserDTO("john@example.com"),
                createSearchUserDTO("jane@example.com"),
                createSearchUserDTO("bob@example.com")
        );

        when(userRepository.findAll()).thenReturn(userEntities);
        for (int i = 0; i < userEntities.size(); i++) {
            when(modelMapper.map(userEntities.get(i), SearchUserDTO.class))
                    .thenReturn(userDTOs.get(i));
        }

        List<SearchUserDTO> result = adminService.searchUser("john");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(dto -> dto.getEmail().contains("john")));
    }

    @Test
    void removeRole_WhenUserNotFound_ReturnsNotFoundMessage() {

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        MessageResponseDTO response = adminService.removeRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(404, response.status());
        assertEquals("User not found", response.message());
        verify(userRepository, never()).save(any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void removeRole_WhenRoleNotGranted_ReturnsNotFoundMessage() {

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleEnum.EMPLOYEE.name())).thenReturn(testRole);

        MessageResponseDTO response = adminService.removeRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(404, response.status());
        assertEquals("This role is not granted to this user", response.message());
        verify(userRepository, never()).save(any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void removeRole_Success() {

        testUser.getRoles().add(testRole);
        testRole.getUsers().add(testUser);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName(RoleEnum.EMPLOYEE.name())).thenReturn(testRole);

        MessageResponseDTO response = adminService.removeRole(TEST_EMAIL, RoleEnum.EMPLOYEE);

        assertEquals(200, response.status());
        assertEquals("Role successfully removed from this user", response.message());
        verify(userRepository).save(testUser);
        verify(roleRepository).save(testRole);
        assertFalse(testUser.getRoles().contains(testRole));
        assertFalse(testRole.getUsers().contains(testUser));
    }

    private UserEntity createUserEntity(String email) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        return user;
    }

    private SearchUserDTO createSearchUserDTO(String email) {
        SearchUserDTO dto = new SearchUserDTO();
        dto.setEmail(email);
        return dto;
    }
}
