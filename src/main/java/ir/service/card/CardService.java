package ir.service.card;

import ir.dto.bankcard.BankCardRequestDto;
import ir.dto.bankcard.BankCardResponseDto;
import ir.entity.BankCard;
import ir.entity.Customer;
import ir.entity.User;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.CardRepository;
import ir.security.JwtTokenProvider;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public Optional<BankCard> findByOwner(User user) {
        return cardRepository.findByOwner(user);
    }

    public BankCardResponseDto add(String token, BankCardRequestDto bankCardRequestDto) {
        log.info("Starting the process of adding a bank card for user.");

        // Step 1: Validate token and extract username
        String username;
        try {
            username = jwtTokenProvider.extractUsername(token);
        } catch (Exception ex) {
            log.error("Invalid token provided: {}", ex.getMessage());
            throw new CustomException("Invalid token. Please provide a valid token.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 2: Find user by username and validate user existence
        User user = userService.findByUserName(username)
                .orElseThrow(() -> {
                    log.error("User not found for username: {}", username);
                    return new CustomException("User not found.", ErrorCode.NOT_FOUND.getCode());
                });

        log.debug("User {} successfully authenticated.", username);

        // Step 3: Check if the user is a Customer
        if (!(user instanceof Customer)) {
            log.error("User {} is not authorized to add a bank card.", user.getUserName());
            throw new CustomException("Only Customers can add bank cards.", ErrorCode.UNAUTHORIZED.getCode());
        }

        // Step 4: Check if user already has a card
        if (cardRepository.findByOwner(user).isPresent()) {
            log.error("User {} already has a registered bank card.", user.getUserName());
            throw new CustomException("You already have a registered bank card. Only one card is allowed.",
                    ErrorCode.CARD_LIMIT_EXCEEDED.getCode());
        }

        // Step 5: Map BankCardRequestDto to BankCard entity
        BankCard bankCard = new BankCard();
        bankCard.setOwner(user);
        bankCard.setCardNumber(bankCardRequestDto.getCardNumber());
        bankCard.setCardHolderName(bankCardRequestDto.getCardHolderName());
        bankCard.setExpiryDate(bankCardRequestDto.getExpiryDate());
        bankCard.setCvv2(bankCardRequestDto.getCvv2());
        bankCard.setPassword(passwordEncoder.encode(bankCardRequestDto.getPassword()));
        bankCard.setBalance(bankCardRequestDto.getBalance() == null ? BigDecimal.ZERO : bankCardRequestDto.getBalance());

        // Step 6: Save BankCard entity to the database
        try {
            cardRepository.save(bankCard);
            log.info("Bank card successfully registered for user {}.", user.getUserName());
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to save bank card due to duplicate card number: {}", bankCard.getCardNumber(), ex);
            throw new CustomException("Card number is already registered.", ErrorCode.DUPLICATE_CARD_NUMBER.getCode());
        }

        // Step 7: Map BankCard entity to response DTO
        BankCardResponseDto responseDto = new BankCardResponseDto();
        responseDto.setCardNumber(bankCard.getCardNumber());
        responseDto.setCardHolderName(bankCard.getCardHolderName());
        responseDto.setExpiryDate(bankCard.getExpiryDate());
        responseDto.setCvv2(bankCard.getCvv2());
        responseDto.setBalance(bankCard.getBalance());

        log.debug("Bank card successfully mapped to response DTO for user: {}", username);

        return responseDto;
    }

    public void save(BankCard bankCard) {
        cardRepository.save(bankCard);
    }

    public Optional<BankCard> findByCardNumberAndCvv2AndPassword(String cardNumber, Integer cvv2, String password) {
        return cardRepository.findByCardNumberAndCvv2AndPassword(cardNumber, cvv2, password);
    }

    public Optional<BankCard> findByCardHolderName(String cardHolderName) {
        return cardRepository.findByCardHolderName(cardHolderName);
    }
}

