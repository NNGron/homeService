package ir.service.expert;

import ir.dto.offer.OfferRequestDto;
import ir.dto.offer.OfferResponseDto;
import ir.dto.order.OrderResponseDto;
import ir.entity.Category;
import ir.entity.Order;

import java.util.List;

/**
 * Service interface for managing expert offers.
 *
 * <p>This interface provides methods to create, update, and delete offers
 * submitted by experts. It ensures authentication for every operation.</p>
 */
public interface ExpertService {

    OfferResponseDto saveOffer(OfferRequestDto offerRequestDto, String token);

    /**
     * Updates an existing offer submitted by the expert.
     *
     * @param offerNumber the unique number of the offer to be updated
     * @param offerRequestDto the updated details of the offer
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     * @return the updated offer as a {@link OfferResponseDto}
     */
    OfferResponseDto updateOffer(Long offerNumber, OfferRequestDto offerRequestDto, String token);

    /**
     * Deletes an offer submitted by the expert.
     *
     * @param offerId the unique ID of the offer to be deleted
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     */
    void deleteOffer(Long offerId, String token);
}
