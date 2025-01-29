package com.example.printmatic.init;

import com.example.printmatic.service.ServicePriceService;
import com.example.printmatic.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

@Controller
public class DbInit implements CommandLineRunner {
    private final UserService userService;
    private final ServicePriceService servicePriceService;
    private final DbInitDiscounts dbInitDiscounts;

    public DbInit(UserService userService, ServicePriceService servicePriceService, DbInitDiscounts dbInitDiscounts) {
        this.userService = userService;
        this.servicePriceService = servicePriceService;
        this.dbInitDiscounts = dbInitDiscounts;
    }

    @Override
    public void run(String... args) throws Exception {
        userService.seedUsers();
        servicePriceService.seedServices();
        dbInitDiscounts.seedDiscounts();
    }
}
