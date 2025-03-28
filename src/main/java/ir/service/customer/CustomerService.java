package ir.service.customer;

import ir.dto.offer.OfferResponseDto;
import ir.exception.CustomException;

import java.util.List;

/**
 * Provides customer-related operations in the system.
 *
 * <p>The interface allows customers to retrieve offers linked to their orders
 * after authentication.</p>
 */
public interface CustomerService {

    /**
     * Retrieves offers linked to the authenticated customer's orders.
     *
     * @param email    the email address of the customer
     * @param password the password of the customer
     * @return a list of offers related to the customer's orders
     * @throws CustomException if authentication fails or the customer is not found
     */
    List<OfferResponseDto> getCustomerOffers(String email, String password);
}
