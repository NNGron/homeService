package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferResponseDto;
import ir.service.customer.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/order-offers")
    @Operation(summary = "(Customer only sort(asc,desc) by 'expert.rating' or 'proposedPrice')")
    public ResponseEntity<List<OfferResponseDto>> getOffersOfOrderCustomer2(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "proposedPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Received request to fetch offers for customer.");
        log.debug("Request details - email: {}, sortBy: {}, sortDir: {}, pageNumber: {}, pageSize: {}",
                email, sortBy, sortDir, pageNumber, size);

        Sort.Direction direction =
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        log.info("Preparing to fetch offers for customer with email: {}", email);
        List<OfferResponseDto> customerOffers = customerService.getOffersForCustomer(email, password, pageable);

        log.info("Successfully retrieved {} offers for customer with email: {}", customerOffers.size(), email);
        return ResponseEntity.ok(customerOffers);
    }
}
