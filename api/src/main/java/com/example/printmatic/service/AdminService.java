package com.example.printmatic.service;

import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SearchUserDTO;
import com.example.printmatic.dto.response.UserDTO;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.model.RoleEntity;
import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.RoleRepository;
import com.example.printmatic.repository.UserRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public AdminService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    public MessageResponseDTO grantRole(String email, RoleEnum role) {
        Optional<UserEntity> employeeOpt = userRepository.findByEmail(email);
        if(employeeOpt.isEmpty()){
            return new MessageResponseDTO(404, "User whit this email not found");
        }

        UserEntity employee = employeeOpt.get();
        RoleEntity roleEntity = roleRepository.findByName(role.name());

        if(employee.getRoles().contains(roleEntity)){
            return new MessageResponseDTO(400,"This role already is granted to this user");
        }

        List<RoleEntity> roles = new ArrayList<>(employee.getRoles());
        roles.add(roleEntity);

        employee.setRoles(roles);
        userRepository.save(employee);
        return new MessageResponseDTO(200, "Role successfully granted to the user");
    }

    public List<SearchUserDTO> searchUser(String query) {
        List<SearchUserDTO> users = userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, SearchUserDTO.class))
                .toList();

        List<SearchUserDTO> topFive = new ArrayList<>();
        FuzzySearch
                .extractTop(query, users.stream().map(SearchUserDTO::getEmail).toList(),5)
                .forEach(extractedResult -> topFive.add(users.get(extractedResult.getIndex())));
        return topFive;
    }
}
