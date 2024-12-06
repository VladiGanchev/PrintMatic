package com.example.printmatic.controler;

import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.enums.RoleEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

   @PostMapping("grantRole")
   public ResponseEntity<MessageResponseDTO> grantRole(
           @RequestParam(value = "role", required = true) RoleEnum role) {
       return ResponseEntity.status(501).build();
   }
}
