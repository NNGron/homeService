package ir.controller;

import ir.dto.captcha.CaptchaResponseDto;
import ir.service.capcha.CaptchaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/generate")
    public ResponseEntity<CaptchaResponseDto> generateCaptcha() {
        CaptchaResponseDto captchaResponseDto = captchaService.generateCaptcha();
        return ResponseEntity.ok(captchaResponseDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String id, @RequestParam String code) {
        boolean ok = captchaService.verifyCaptcha(id, code);
        return ok ? ResponseEntity.ok("Valid Captcha")
                  : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired captcha");
    }
}
