package com.example.printmatic.controler;


import com.example.printmatic.dto.request.RegistrationDTO;
import com.example.printmatic.dto.request.UpdateUserDTO;
import com.example.printmatic.dto.response.JWTDTO;
import com.example.printmatic.dto.request.LoginDTO;
import com.example.printmatic.dto.response.UserDTO;
import com.example.printmatic.service.UserService;
import com.example.printmatic.utils.JWTUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.printmatic.dto.response.MessageResponseDTO;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/user")
@Slf4j
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    public UserController(
            UserService userService,
            AuthenticationManager authenticationManager,
            JWTUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("register")
    public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    new MessageResponseDTO(400,bindingResult.getAllErrors().getFirst().getDefaultMessage()));
        }
        Optional<RegistrationDTO> optionalRegistrationDTO = userService.registerUser(registrationDTO);

        if (optionalRegistrationDTO.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponseDTO(400,"User registration failed"));
        }

        return ResponseEntity.ok(new MessageResponseDTO(200,"User registration successful"));
    }

    @PostMapping("login")
    public ResponseEntity<JWTDTO> login(@Valid @RequestBody LoginDTO loginDTO,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
        );
        UserDetails userDetails =(UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new JWTDTO(userDetails.getUsername(), jwt, roles));
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUser(Principal principal) {
        UserDTO user = userService.getCurrentUser(principal);
        return ResponseEntity.ok(user);
    }

    @PostMapping("update")
    public ResponseEntity<MessageResponseDTO> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO,
                                                          BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    new MessageResponseDTO(400,bindingResult.getAllErrors().getFirst().getDefaultMessage()));
        }

        MessageResponseDTO result = userService.updateCurrentUser(updateUserDTO, principal);
        return ResponseEntity.status(result.status()).body(result);
    }

}
