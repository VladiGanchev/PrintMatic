package com.example.printmatic.service;

import com.example.printmatic.dto.request.UpdateUserDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.UserDTO;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.printmatic.dto.request.RegistrationDTO;
import com.example.printmatic.model.UserEntity;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepositoty;

    public UserService(
            UserRepository userRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder, RoleRepository roleRepositoty) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepositoty = roleRepositoty;
    }

    @Transactional
    public Optional<RegistrationDTO> registerUser(RegistrationDTO registrationDTO) {
        if(userRepository.existsByEmail(registrationDTO.getEmail())) {
            return Optional.empty();
        }

        UserEntity userEntity = modelMapper.map(registrationDTO, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        RoleEntity role = roleRepositoty.findByName(RoleEnum.USER.toString());
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(role);

        userEntity.setBalance(BigDecimal.ZERO);
        userEntity.setRoles(roles);

        userRepository.save(userEntity);
        return Optional.of(registrationDTO);
    }

    public UserDTO getCurrentUser(Principal principal) {
        UserEntity userEntity = userRepository.findByEmail(principal.getName()).get();
        return modelMapper.map(userEntity, UserDTO.class);
    }


    public MessageResponseDTO updateCurrentUser(@Valid UpdateUserDTO updateUserDTO, Principal principal) {
        Optional<UserEntity> currentUserOptional = userRepository.findByEmail(principal.getName());

        if(currentUserOptional.isEmpty()) {
            return new MessageResponseDTO(404, "User not found");
        }
        UserEntity currentUser = currentUserOptional.get();

        if (!updateUserDTO.getEmail().equals(currentUser.getEmail())) {
            Optional<UserEntity> existingUser = userRepository.findByEmail(updateUserDTO.getEmail());
            if (existingUser.isPresent()) {
                return new MessageResponseDTO(400, "User with this email already exists");
            }
        }

        currentUser.setFirstName(updateUserDTO.getFirstName());
        currentUser.setLastName(updateUserDTO.getLastName());
        currentUser.setEmail(updateUserDTO.getEmail());
        currentUser.setPhoneNumber(updateUserDTO.getPhoneNumber());

        try {
            userRepository.save(currentUser);
            return new MessageResponseDTO(200, "User updated successfully");
        } catch (Exception e) {
            return new MessageResponseDTO(500, "Failed to update user");
        }
    }

    public void seedUsers() {
        if(userRepository.count() == 0) {

                RoleEntity userRole = new RoleEntity();
                userRole.setName("USER");

                RoleEntity adminRole = new RoleEntity();
                adminRole.setName("ADMIN");

                RoleEntity employeeRole = new RoleEntity();
                employeeRole.setName("EMPLOYEE");

                roleRepositoty.save(userRole);
                roleRepositoty.save(adminRole);
                roleRepositoty.save(employeeRole);

                List<RoleEntity> roles = new ArrayList<>();
                roles.add(adminRole);

                UserEntity userEntity = new UserEntity();
                userEntity.setFirstName("John");
                userEntity.setLastName("Doe");
                userEntity.setEmail("john@doe.com");
                userEntity.setPassword(passwordEncoder.encode("password"));
                userEntity.setPhoneNumber("8888888888");
                userEntity.setBalance(new BigDecimal(0));
                userEntity.setRoles(roles);

                userRepository.save(userEntity);
        }
    }
}
