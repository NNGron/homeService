package ir.service.capcha;

import ir.dto.captcha.CaptchaResponseDto;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class CaptchaService {

    private final CaptchaCacheService cacheService;

    public CaptchaService(CaptchaCacheService cacheService) {
        this.cacheService = cacheService;
    }

    public CaptchaResponseDto generateCaptcha() {
        String id = UUID.randomUUID().toString();
        String code = String.valueOf(new Random().nextInt(9000) + 1000);
        cacheService.put(id, code);
        return new CaptchaResponseDto(id, code);
    }

    public boolean verifyCaptcha(String id, String inputCode) {
        String storedCode = cacheService.get(id);
        if (storedCode == null) return false;
        boolean isValid = storedCode.equals(inputCode);
        if (isValid) {
            cacheService.evict(id);
        }
        return isValid;
    }
}
