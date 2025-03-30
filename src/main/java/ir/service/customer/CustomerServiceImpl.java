package ir.service.customer;

import ir.convert.offer.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Customer;
import ir.entity.User;
import ir.entity.enums.UserStatus;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.user.UserRepository;
import ir.service.offer.OfferService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    private final OfferService offerService;

    /**
     * Retrieves a paginated list of offers associated with a specific customer.
     * <p>
     * Validates the email and password parameters and ensures that the user is authenticated
     * and has the role of "Customer."
     * </p>
     *
     * @param email    the email of the customer for authentication
     * @param password the password of the customer for authentication
     * @param pageable the pagination and sorting configuration
     * @return a list of {@link OfferResponseDto} representing the offers associated with the customer
     * @throws CustomException if validation fails, the user is not authenticated,
     *                         the user is not a customer, or the customer has been deleted
     */
    @Override
    public List<OfferResponseDto> getOffersForCustomer(String email, String password, Pageable pageable) {
        log.info("Authenticating customer with email: {}", email);

        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, password);

        if (optionalUser.isEmpty()) {
            log.error("Authentication failed for user with email: {}. User not found.", email);
            throw new CustomException("User not found", ErrorCode.NOT_FOUND.getCode());
        }

        User user = optionalUser.get();

        if (user.getStatus().equals(UserStatus.NEW)) {
            log.error("User with email {} is not approved.", email);
            throw new CustomException("User should be approved", ErrorCode.USER_STATUS_NOT_APPROVED.getCode());
        }

        if (user.getStatus().equals(UserStatus.DELETED)) {
            log.error("User with email {} has been deleted.", email);
            throw new CustomException("User already deleted", ErrorCode.NOT_FOUND.getCode());
        }

        if (!(user instanceof Customer customer)) {
            log.error("User with email {} is not a customer.", email);
            throw new CustomException("User is not a customer", ErrorCode.NOT_AUTHORIZED.getCode());
        }

        Long customerId = customer.getId();
        List<OfferResponseDto> offerResponseDtos = offerService.findAllOffers(customerId, pageable);

        log.info("Successfully retrieved {} offers for customer with email: {}", offerResponseDtos.size(), email);
        return offerResponseDtos;
    }
}
