package ir.service.customer;

import ir.dto.offer.OfferResponseDto;
import ir.entity.Customer;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.UserRepository;
import ir.security.JwtTokenProvider;
import ir.service.offer.OfferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final OfferService offerService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public List<OfferResponseDto> getOffersForCustomer(String token, Pageable pageable) {
        log.info("Authenticating customer using token");

        String username = jwtTokenProvider.extractUsername(token);
        Optional<User> userOptional = userRepository.findByUserName(username);

        if (userOptional.isEmpty() || !(userOptional.get() instanceof Customer)) {
            log.error("User with token is not a customer or not found.");
            throw new CustomException("User not found or not a customer.", ErrorCode.NOT_FOUND.getCode());
        }

        Customer customer = (Customer) userOptional.get();

        if (customer.getStatus().equals(UserStatus.NEW)) {
            log.error("Customer with token is not approved.");
            throw new CustomException("Customer should be approved.", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        if (customer.getStatus().equals(UserStatus.DELETED)) {
            log.error("Customer with token has been deleted.");
            throw new CustomException("Customer already deleted.", ErrorCode.NOT_FOUND.getCode());
        }

        Long customerId = customer.getId();
        log.debug("Customer ID retrieved: {}", customerId);

        List<OfferResponseDto> offerResponseDtos = offerService.findAllOffers(customerId, pageable);
        log.info("Successfully retrieved {} offers for customer with token.", offerResponseDtos.size());

        return offerResponseDtos;
    }
}