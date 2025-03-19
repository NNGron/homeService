package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.user.UserResponseDto;

import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/credits")
public class CreditController {

    private final UserService userService;


    @PutMapping("/add-balance")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserResponseDto> addBalance(@RequestParam String email,
                                                      @RequestParam Double balance) {
        UserResponseDto responseDto = userService.addCreditToCustomer(email, balance);
        return ResponseEntity.ok(responseDto);
    }
}
