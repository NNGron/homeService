package ir.service.offer;


import ir.entity.Offer;

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
}
