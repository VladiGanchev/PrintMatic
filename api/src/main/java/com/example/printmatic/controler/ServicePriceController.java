package com.example.printmatic.controler;

import com.example.printmatic.dto.request.ServicePriceDTO;
import com.example.printmatic.dto.response.MultipleServicePriceResultDTO;
import com.example.printmatic.dto.response.ServicePriceResultDTO;
import com.example.printmatic.service.ServicePriceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/services")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ServicePriceController {

    private final ServicePriceService servicePriceService;

    public ServicePriceController(ServicePriceService servicePriceService) {
        this.servicePriceService = servicePriceService;
    }

    @GetMapping("/")
    public ResponseEntity<MultipleServicePriceResultDTO> getAllServices(Principal principal) {
        MultipleServicePriceResultDTO result = servicePriceService.getAllServicePrices(principal);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicePriceResultDTO> updateService(
            @PathVariable("id") Long servicePriceId,
            @Valid @RequestBody ServicePriceDTO servicePriceDTO,
            Principal principal) {
        ServicePriceResultDTO result = servicePriceService.updateServicePrice(servicePriceId, servicePriceDTO, principal);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
