package ir.service.customer;

import ir.convert.offer.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Customer;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides functionality for customer-related operations.
 *
 * <p>This service handles customer-specific tasks such as retrieving offers
 * linked to their orders. It ensures only authenticated and active customers
 * can perform these actions.</p>
 *
 * <p>Dependencies include:</p>
 * <ul>
 *     <li>{@link UserRepository} - To access user data.</li>
 *     <li>{@link OfferEntityToOfferResponseDtoConvertor} - To convert offer entities into response DTOs.</li>
 * </ul>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Service} - Marks this class as a service in the application.</li>
 *     <li>{@link AllArgsConstructor} - Generates a constructor for all fields.</li>
 *     <li>{@link Slf4j} - Enables logging within this class.</li>
 * </ul>
 */
@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    /**
     * Retrieves a list of offers for the authenticated customer's orders.
     *
     * @param email    the customer's email for login
     * @param password the customer's password for login
     * @return a list of offers converted to {@link OfferResponseDto}
     * @throws CustomException if:
     *                         <ul>
     *                             <li>The customer is not found or credentials are incorrect.</li>
     *                             <li>The customer's account has been deleted.</li>
     *                         </ul>
     */
    @Override
    public List<OfferResponseDto> getCustomerOffers(String email, String password) {
        log.info("Attempting to retrieve offers for customer with email: {}", email);
        return userRepository.findByEmailAndPassword(email, password)
                .filter(user -> {
                    if (user.getStatus().equals(UserStatus.DELETED)) {
                        log.error("User with email {} has been deleted and cannot retrieve offers.", email);
                        throw new CustomException("User already deleted", ErrorCode.NOT_FOUND.getCode());
                    }
                    return user instanceof Customer;
                })
                .map(user -> (Customer) user)
                .map(customer -> customer.getOrders().stream()
                        .flatMap(order -> order.getOffers().stream())
                        .collect(Collectors.toList()))
                .map(offerEntityToOfferResponseDtoConvertor::convertList)
                .orElseThrow(() -> {
                    log.error("Authentication failed for user with email: {}. User not found.", email);
                    return new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
                });
    }
}
