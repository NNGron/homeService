package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.credit.CreditResponseDto;
import ir.dto.user.UserResponseDto;
import ir.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/credits")
public class CreditController {

    private final UserService userService;

    @PutMapping("/add-balance")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserResponseDto> addBalance(@RequestParam String email,
                                                      @RequestParam BigDecimal balance) {
        log.info("Received request to add balance for customer with email: {}", email);
        log.debug("Balance to be added: {}", balance);
        UserResponseDto responseDto = userService.addCreditToCustomer(email, balance);

        log.info("Successfully added balance for customer with email: {}", email);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/show-credit")
    @Operation(summary = "(Customer & Expert only)")
    public ResponseEntity<CreditResponseDto> showCredit(@RequestHeader("Authorization") String token) {
        log.info("Received request to show credit for user with token");
        log.debug("Authenticating user with provided email and password.");
        CreditResponseDto creditResponseDto = userService.getBalance(token);

        log.info("Successfully retrieved credit for user with token");
        return ResponseEntity.ok(creditResponseDto);
    }
}