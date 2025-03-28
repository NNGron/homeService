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
    /**
     * Saves a new offer submitted by the expert.
     *
     * @param offerRequestDto the details of the offer to be saved
     * @param email           the expert's email for authentication
     * @param password        the expert's password for authentication
     * @return the saved offer as a {@link OfferResponseDto}
     */
    OfferResponseDto saveOffer(OfferRequestDto offerRequestDto, String email, String password);

    /**
     * Updates an existing offer submitted by the expert.
     *
     * @param offerNumber the unique number of the offer to be updated
     * @param offerRequestDto the updated details of the offer
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     * @return the updated offer as a {@link OfferResponseDto}
     */
    OfferResponseDto updateOffer(Long offerNumber, OfferRequestDto offerRequestDto, String email, String password);

    /**
     * Deletes an offer submitted by the expert.
     *
     * @param offerId the unique ID of the offer to be deleted
     * @param email the expert's email for authentication
     * @param password the expert's password for authentication
     */
    void deleteOffer(Long offerId, String email, String password);
}
