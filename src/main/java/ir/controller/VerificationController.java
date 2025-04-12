package ir.controller;

import ir.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        log.info("Processing verification request with token: {}", token);

        String result = verificationService.verifyToken(token);

        return switch (result) {
            case "successCustomer" -> ResponseEntity.ok("Email verified successfully. Registration process completed.");
            case "successExpert" ->
                    ResponseEntity.ok("Email verified successfully. Your account is now waiting for admin approval.");
            case "tokenUsed" -> ResponseEntity.badRequest().body("This verification link has already been used.");
            case "tokenExpired" -> ResponseEntity.badRequest().body("This verification link has expired.");
            case "invalidToken" -> ResponseEntity.badRequest().body("Invalid verification link.");
            default ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during verification.");
        };
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
   //     verificationService.resendVerificationToken(email);
        return ResponseEntity.ok("Verification email has been resent if the email is registered.");
    }
}