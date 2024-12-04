package com.example.printmatic.controler;

import com.example.printmatic.dto.request.OrderCreationDTO;
import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/create")
    private ResponseEntity<MessageResponseDTO> createOrder(@Valid @RequestBody OrderCreationDTO orderCreationDTO,
                                                           BindingResult bindingResult , Principal principal) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                    new MessageResponseDTO(400, bindingResult.getAllErrors().getFirst().getDefaultMessage()));
        }

        MessageResponseDTO result = orderService.createOrder(orderCreationDTO, principal);
        return ResponseEntity.status(result.status()).body(result);
    }
}
