package com.example.printmatic.controler;

import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @PostMapping("/create")
    private ResponseEntity<MessageResponseDTO> createOrder(@RequestBody OrderCreationDTO orderCreationDTO) {}
}
