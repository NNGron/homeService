package ir.service;

import ir.entity.Customer;
import ir.entity.Expert;
import ir.entity.User;
import ir.entity.VerificationToken;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.UserRepository;
import ir.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public void createVerificationTokenAndSendEmail(User user) {


        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user, token);
        log.info("Verification token created for user: {}", user.getEmail());
    }

    @Transactional
    public String verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            log.warn("Invalid verification token: {}", token);
            return "invalidToken";
        }

        if (verificationToken.isUsed()) {
            log.warn("Token already used: {}", token);
            return "tokenUsed";
        }

        if (verificationToken.isExpired()) {
            log.warn("Token expired: {}", token);
            return "tokenExpired";
        }

        User user = verificationToken.getUser();

        // علامت‌گذاری توکن به‌عنوان استفاده‌شده
        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        // تغییر وضعیت کاربر براساس نقش
        if (user instanceof Expert) {
            user.setStatus(UserStatus.PENDING_APPROVAL);
            userRepository.save(user);
            log.info("Expert account is now waiting for admin approval: {}", user.getEmail());
            return "successExpert";
        } else if (user instanceof Customer) {
            user.setStatus(UserStatus.APPROVED);
            userRepository.save(user);
            log.info("Customer account registration completed: {}", user.getEmail());
            return "successCustomer";
        }

        return "invalidRole"; // نقش غیرمنتظره
    }

    @Transactional
    public void activateUser(User user) {
        if (user instanceof Expert) {
            user.setStatus(UserStatus.PENDING_APPROVAL);
            log.info("Expert status set to PENDING_APPROVAL: {}", user.getEmail());
        } else {
            user.setStatus(UserStatus.APPROVED);
            log.info("Customer status set to ACTIVE: {}", user.getEmail());
        }
        userRepository.save(user);
    }

    @Transactional
    public void resendVerificationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Invalid email", ErrorCode.NOT_FOUND.getCode()));
        tokenRepository.deleteByUser_Id(user.getId());
        createVerificationTokenAndSendEmail(user);
    }
}