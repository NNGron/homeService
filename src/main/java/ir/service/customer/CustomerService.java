package ir.service.customer;

import ir.dto.offer.OfferResponseDto;

import java.util.List;

public interface CustomerService {
    List<OfferResponseDto> getCustomerOffers(String email, String password);
}
