package ir.service.offer;


import ir.dto.offer.OfferResponseDto;
import ir.entity.Offer;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing offers.
 *
 * <p>This interface provides methods for saving, retrieving, and deleting offers.</p>
 */
public interface OfferService {
    /**
     * Saves the given offer.
     *
     * @param offer the offer to be saved
     * @return the saved {@link Offer} entity
     */
    Offer saveOffer(Offer offer);

    /**
     * Finds an offer by its unique ID.
     *
     * @param offerId the ID of the offer to search for
     * @return the {@link Offer} entity matching the given ID
     */
    Offer findOfferById(Long offerId);

    /**
     * Deletes the given offer.
     *
     * @param offer the offer to be deleted
     */
    void deleteOffer(Offer offer);

    /**
     * Retrieves a paginated list of offers associated with a specific customer.
     *
     * @param customerId the unique ID of the customer whose offers need to be retrieved
     * @param pageable   the pagination and sorting configuration specifying page number, size, and sorting order
     * @return a list of {@link OfferResponseDto} representing the paginated and sorted offers related to the customer
     */
    List<OfferResponseDto> findAllOffers(Long customerId, Pageable pageable);

    long countOffersByExpertEmail(String email);
}