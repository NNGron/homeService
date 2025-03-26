package ir.service.offer;


import ir.entity.Offer;

public interface OfferService {
    Offer saveOffer(Offer offer);

    Offer findOfferById(Long offerId);

    void deleteOffer(Offer offer);
}
