package ir.service.customer;

import ir.dto.offer.OfferResponseDto;
import ir.exception.CustomException;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Provides customer-related operations in the system.
 *
 * <p>The interface allows customers to retrieve offers linked to their orders
 * after authentication.</p>
 */
public interface CustomerService {

    /**
     * Retrieves a paginated list of offers for a given customer based on their email and password.
     *
     * @param email    the email of the customer for authentication
     * @param password the password of the customer for authentication
     * @param pageable the pagination information including page number, size, and sorting
     * @return a paginated list of {@link OfferResponseDto} representing the offers associated with the customer
     * @throws CustomException if authentication fails, the user is not a customer, or the customer has been deleted
     */
    List<OfferResponseDto> getOffersForCustomer(String email, String password, Pageable pageable);
}
