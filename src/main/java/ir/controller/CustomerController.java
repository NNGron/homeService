package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferResponseDto;
import ir.service.customer.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Handles customer-related API requests.
 *
 * <p>This controller provides endpoints for customer operations, such as
 * retrieving offers linked to customer orders.</p>
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Retrieves a list of offers linked to the customer's orders.
     *
     * @param email    the email of the customer for authentication
     * @param password the password of the customer for authentication
     * @return a response containing a list of offers for the customer's orders
     */
    @GetMapping("/order-offers")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<List<OfferResponseDto>> getOffersOfOrderCustomer(@RequestParam String email,
                                                                           @RequestParam String password) {

        log.info("Request received to get offers for customer with email: {}", email);
        List<OfferResponseDto> customerOffers = customerService.getCustomerOffers(email, password);
        log.info("Successfully retrieved {} offers for customer with email: {}", customerOffers.size(), email);
        return ResponseEntity.ok(customerOffers);
    }
}
