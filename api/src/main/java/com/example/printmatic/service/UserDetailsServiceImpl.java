package com.example.printmatic.service;

import com.example.printmatic.model.UserEntity;
import com.example.printmatic.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email not found: " + username);
        }

        List<SimpleGrantedAuthority> simpleGrantedAuthorities = user.get()
                .getRoles()
                .stream()
                .map(roleEntity -> new SimpleGrantedAuthority(roleEntity.getName()))
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                simpleGrantedAuthorities
        );
    }
}