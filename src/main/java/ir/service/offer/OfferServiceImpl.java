package ir.service.offer;

import ir.entity.Offer;
import ir.exception.CustomException;
import ir.exception.ErrorCode;
import ir.repository.OfferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;

    @Override
    public Offer saveOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    @Override
    public Offer findOfferById(Long offerId) {
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new CustomException("Offer not found", ErrorCode.NOT_FOUND.getCode()));
    }

    @Override
    public void deleteOffer(Offer offer) {
        offerRepository.delete(offer);
    }
}
