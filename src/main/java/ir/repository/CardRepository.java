package ir.repository;

import ir.entity.BankCard;
import ir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<BankCard, Long>, JpaSpecificationExecutor<BankCard> {
    Optional<BankCard> findByOwner(User user);
    Optional<BankCard> findByCardNumberAndCvvAndPassword(String cardNumber, Integer cvv, String password);
    Optional<BankCard> findByCardHolderName(String cardHolderName);
}
