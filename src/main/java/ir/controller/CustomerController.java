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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/order-offers")
    @Operation(summary = "(Customer only sort(asc,desc) by 'expert.rating' or 'proposedPrice')")
    public ResponseEntity<List<OfferResponseDto>> getOffersOfOrderCustomer(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "proposedPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Received request to fetch offers for customer (token-based)");
        log.debug("Request details - sortBy: {}, sortDir: {}, pageNumber: {}, pageSize: {}", sortBy, sortDir, pageNumber, size);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortBy));

        List<OfferResponseDto> customerOffers = customerService.getOffersForCustomer(token, pageable);

        log.info("Successfully retrieved {} offers for customer.", customerOffers.size());
        return ResponseEntity.ok(customerOffers);
    }

}