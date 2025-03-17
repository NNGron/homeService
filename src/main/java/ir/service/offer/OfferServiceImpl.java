package ir.service.offer;

import ir.entity.Offer;
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
}
