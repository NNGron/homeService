package ir.service.offer;

import ir.convert.offer.OfferEntityToOfferResponseDtoConvertor;
import ir.dto.offer.OfferResponseDto;
import ir.entity.Offer;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.OfferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing offers.
 *
 * <p>This class provides functionality to save, retrieve, and delete offers,
 * while handling exceptions for invalid operations.</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferEntityToOfferResponseDtoConvertor offerEntityToOfferResponseDtoConvertor;

    /**
     * Saves the provided offer.
     *
     * @param offer the offer to be saved
     * @return the saved {@link Offer} entity
     */
    @Override
    public Offer saveOffer(Offer offer) {
        log.info("Saving new offer with details: {}", offer);
        return offerRepository.save(offer);
    }

    /**
     * Retrieves an offer by its unique ID.
     *
     * @param offerId the ID of the offer to retrieve
     * @return the {@link Offer} entity matching the given ID
     * @throws CustomException if no offer is found with the given ID
     */
    @Override
    public Offer findOfferById(Long offerId) {
        log.info("Searching for offer with ID: {}", offerId);
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new CustomException("Offer not found", ErrorCode.NOT_FOUND.getCode()));
    }

    /**
     * Deletes the specified offer.
     *
     * @param offer the offer to be deleted
     */
    @Override
    public void deleteOffer(Offer offer) {
        log.info("Deleting offer with ID: {}", offer.getId());
        offerRepository.delete(offer);
    }

    /**
     * Retrieves a paginated list of offers associated with a specific customer.
     *
     * @param customerId the ID of the customer whose offers should be retrieved
     * @param pageable   the pagination configuration specifying page number, size, and sorting
     * @return a list of {@link OfferResponseDto} representing the paginated offers related to the customer
     */
    @Override
    public List<OfferResponseDto> findAllOffers(Long customerId, Pageable pageable) {
        Specification<Offer> spec =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("order").get("customer").get("id"), customerId);
        List<Offer> offers = offerRepository.findAll(spec, pageable).getContent();
        return offerEntityToOfferResponseDtoConvertor.convertList(offers);
    }

    @Override
    public long countOffersByExpertEmail(String email) {
        return offerRepository.countOffersByExpertEmail(email);
    }
}
