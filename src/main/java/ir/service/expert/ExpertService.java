package ir.service.expert;


import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;

public interface ExpertService {
    OfferResponseDto saveOffer(OfferRequestDto offerRequestDto, String email, String password);
}
