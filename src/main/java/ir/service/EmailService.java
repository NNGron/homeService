package ir.service;

import ir.entity.User;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(User user, String token) {
        try {
            // استفاده از localhost بدون نیاز به تنظیمات baseUrl
            String verificationUrl = "http://localhost:8080/v1/users/verify?token=" + token;

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("تأیید حساب کاربری - خدمات منزل");
            mailMessage.setText("کاربر گرامی " + user.getFirstName() + " " + user.getLastName() + "،\n\n" +
                    "برای تأیید حساب کاربری خود، لطفاً روی لینک زیر کلیک کنید:\n" +
                    verificationUrl + "\n\n" +
                    "این لینک تا 24 ساعت آینده معتبر است.\n\n" +
                    "با تشکر،\n" +
                    "تیم خدمات منزل");

            mailSender.send(mailMessage);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new CustomException("Error sending verification email", ErrorCode.ERROR_SENDING_VERIFY.getCode());
        }
    }
}