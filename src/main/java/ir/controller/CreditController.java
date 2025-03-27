package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.credit.CreditResponseDto;
import ir.dto.user.UserResponseDto;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/credits")
public class CreditController {

    private final UserService userService;


    @PutMapping("/add-balance")
    @Operation(summary = "(Admin only)")
    public ResponseEntity<UserResponseDto> addBalance(@RequestParam String email,
                                                      @RequestParam BigDecimal balance) {
        UserResponseDto responseDto = userService.addCreditToCustomer(email, balance);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/show-credit")
    @Operation(summary = "(Customer & Expert only)")
    public ResponseEntity<CreditResponseDto> showCredit(@RequestParam String email,
                                                        @RequestParam String password) {
        CreditResponseDto creditResponseDto = userService.getBalance(email, password);
        return ResponseEntity.ok(creditResponseDto);
    }

}
