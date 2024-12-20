package com.example.printmatic.controler;

import com.example.printmatic.dto.response.MessageResponseDTO;
import com.example.printmatic.dto.response.SearchUserDTO;
import com.example.printmatic.enums.RoleEnum;
import com.example.printmatic.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("grantRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<MessageResponseDTO> grantRole(
            @RequestParam(value = "email" , required = true) String email,
            @RequestParam(value = "role", required = true) RoleEnum role) {

        MessageResponseDTO result = adminService.grantRole(email, role);
        return ResponseEntity.status(result.status()).body(result);
    }
    @DeleteMapping("removeRole")
    public ResponseEntity<MessageResponseDTO> removeRole(
            @RequestParam(value = "email" , required = true) String email,
            @RequestParam(value = "role", required = true) RoleEnum role) {
        MessageResponseDTO result = adminService.removeRole(email, role);
        return ResponseEntity.status(result.status()).body(result);
    }

   @GetMapping("/search/email")
   public ResponseEntity<List<SearchUserDTO>> searchUser(@RequestParam(required = true) String query){
        return ResponseEntity.ok(adminService.searchUser(query));
   }
}
