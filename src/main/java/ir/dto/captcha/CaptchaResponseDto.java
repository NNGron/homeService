package ir.dto.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponseDto {
    private String id;
    private String code;
}
