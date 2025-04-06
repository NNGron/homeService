package ir.service.customer;

import ir.dto.offer.OfferResponseDto;
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
     * Fetches all offers for a customer based on JWT token authentication.
     *
     * @param token JWT token of the customer
     * @param pageable pagination and sorting information
     * @return list of offer DTOs for the authenticated customer
     */
    List<OfferResponseDto> getOffersForCustomer(String token, Pageable pageable);
}
