package ir.service.expert;

import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;

/**
 * Service interface for managing expert offers.
 *
 * <p>This interface provides methods to create, update, and delete offers
 * submitted by experts. It ensures authentication for every operation.</p>
 */
public interface ExpertService {

    OfferResponseDto saveOffer(OfferRequestDto offerRequestDto, String token);

    OfferResponseDto updateOffer(Long offerNumber, OfferRequestDto offerRequestDto, String token);

    void deleteOffer(Long offerId, String token);
}
