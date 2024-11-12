package com.example.printmatic.init;

import com.example.printmatic.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

@Controller
public class DbInit implements CommandLineRunner {
    private UserService userService;

    public DbInit(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        userService.seedUsers();
    }
}
