package ir.service.card;


import ir.dto.bankcard.BankCardRequestDto;
import ir.dto.bankcard.BankCardResponseDto;
import ir.entity.BankCard;
import ir.entity.User;
import ir.repository.CardRepository;
import ir.security.JwtTokenProvider;
import ir.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public Optional<BankCard> findByOwner(User user) {
        return cardRepository.findByOwner(user);
    }

    public Optional<BankCard> findByCardNumberAndCvvAndPassword(String cardNumber, Integer cvv, String password) {
        return cardRepository.findByCardNumberAndCvvAndPassword(cardNumber, cvv, password);
    }

    public Optional<BankCard> findByCardHolderName(String cardHolderName) {
        return cardRepository.findByCardHolderName(cardHolderName);
    }

    public BankCardResponseDto add(String token, BankCardRequestDto bankCardRequestDto) {
        String username = tokenProvider.extractUsername(token);
        User user = userService.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BankCard bankCard = new BankCard();
        bankCard.setOwner(user);
        bankCard.setCardNumber(bankCardRequestDto.getCardNumber());
        bankCard.setCardHolderName(bankCardRequestDto.getCardHolderName());
        bankCard.setCvv(bankCardRequestDto.getCvv());
        bankCard.setPassword(passwordEncoder.encode(bankCardRequestDto.getPassword()));
        bankCard.setExpiryDate(bankCardRequestDto.getExpiryDate());

        bankCard = cardRepository.save(bankCard);

        BankCardResponseDto bankCardResponseDto = new BankCardResponseDto();
        bankCardResponseDto.setCardNumber(bankCard.getCardNumber());
        bankCardResponseDto.setCardHolderName(bankCard.getCardHolderName());
        bankCardResponseDto.setBalance(bankCard.getBalance());
        bankCardResponseDto.setExpiryDate(bankCard.getExpiryDate());

        return bankCardResponseDto;
    }

    public void save(BankCard bankCard) {
        cardRepository.save(bankCard);
    }
}
