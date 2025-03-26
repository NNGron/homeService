package ir.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.dto.offer.OfferResponseDto;
import ir.service.customer.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/order-offers")
    @Operation(summary = "(Customer only)")
    public ResponseEntity<List<OfferResponseDto>> getOffersOfOrderCustomer(@RequestParam String email,
                                                                           @RequestParam String password) {
        List<OfferResponseDto> customerOffers = customerService.getCustomerOffers(email, password);
        return ResponseEntity.ok(customerOffers);
    }
}
